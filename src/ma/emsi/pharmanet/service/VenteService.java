package ma.emsi.pharmanet.service;

import ma.emsi.pharmanet.exception.*;
import ma.emsi.pharmanet.model.*;

import java.util.Map;

public class VenteService {

    /**
     * Réalise une vente : vérifie tout d'abord, puis modifie le stock.
     * Si une vérification échoue, rien n'est modifié.
     */
    public Vente vendre(Pharmacien pharmacien, Map<Medicament, Integer> panier,
                        boolean ordonnancePresentee) throws PharmaNetException {

        if (panier == null || panier.isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide");
        }
        Pharmacie pharmacie = pharmacien.getPharmacie();

        // Un seul thread à la fois par pharmacie (utile quand le serveur sera multi-clients)
        synchronized (pharmacie) {

            if (!pharmacien.isActif() || !pharmacie.isActif()) {
                throw new IllegalArgumentException("Le compte et la pharmacie doivent être actifs");
            }

            // Phase 1 : vérifications (aucune modification)
            for (Map.Entry<Medicament, Integer> ligne : panier.entrySet()) {
                Medicament demande = ligne.getKey();
                int qte = ligne.getValue();

                Stock stock = pharmacie.trouverStock(demande)
                        .orElseThrow(() -> new MedicamentIntrouvableException(demande.getNom()));
                Medicament m = stock.getMedicament();

                if (m.estExpire()) {
                    throw new MedicamentExpireException(m.getNom(), m.getDateExpiration());
                }
                if (m instanceof MedicamentSurOrdonnance mo && !mo.verifierOrdonnance(ordonnancePresentee)) {
                    throw new OrdonnanceRequiseException(m.getNom());
                }
                if (!stock.verifierDisponibilite(qte)) {
                    throw new StockInsuffisantException(m.getNom(), qte, stock.getQuantite());
                }
            }

            // Phase 2 : tout est valide, on modifie
            Vente vente = new Vente(pharmacie.getVentes().size() + 1, pharmacien);
            for (Map.Entry<Medicament, Integer> ligne : panier.entrySet()) {
                Stock stock = pharmacie.trouverStock(ligne.getKey()).orElseThrow();
                vente.ajouterLigne(stock.getMedicament(), ligne.getValue());
                stock.retirerQuantite(ligne.getValue());
            }
            pharmacien.enregistrerVente(vente);
            return vente;
        }
    }
}