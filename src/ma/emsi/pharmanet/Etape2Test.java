package ma.emsi.pharmanet;

import ma.emsi.pharmanet.exception.PharmaNetException;
import ma.emsi.pharmanet.model.*;
import ma.emsi.pharmanet.service.RechercheService;
import ma.emsi.pharmanet.service.VenteService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Etape2Test {

    public static void main(String[] args) {
        Pharmacie centrale = new Pharmacie(1, "Pharmacie Centrale", "12 Bd Zerktouni", "Casablanca", "0522000001");
        Pharmacie atlas = new Pharmacie(2, "Pharmacie Atlas", "5 Rue Hassan II", "Rabat", "0537000002");
        Pharmacien pharmacien = new Pharmacien(1, "Karim Alami", "karim@pharmanet.ma", "secret123", "PH-001", centrale);

        Medicament doliprane = new MedicamentLibre(1, "Doliprane 1000", 15.0, LocalDate.now().plusYears(1));
        Medicament amoxicilline = new MedicamentSurOrdonnance(3, "Amoxicilline 500", 40.0, LocalDate.now().plusMonths(8));
        Medicament perime = new MedicamentLibre(4, "Sirop ancien", 12.0, LocalDate.now().minusDays(5));
        Medicament absent = new MedicamentLibre(5, "Aspirine", 10.0, LocalDate.now().plusYears(1));

        centrale.ajouterStock(doliprane, 50);
        centrale.ajouterStock(amoxicilline, 20);
        centrale.ajouterStock(perime, 5);
        atlas.ajouterStock(doliprane, 10);

        VenteService ventes = new VenteService();

        // 1. Vente valide
        Map<Medicament, Integer> panier = new LinkedHashMap<>();
        panier.put(doliprane, 2);
        panier.put(amoxicilline, 1);
        tenter("Vente valide", ventes, pharmacien, panier, true);

        // 2. Ordonnance manquante
        tenter("Sans ordonnance", ventes, pharmacien, Map.of(amoxicilline, 1), false);

        // 3. Stock insuffisant
        tenter("Quantité trop grande", ventes, pharmacien, Map.of(doliprane, 999), false);

        // 4. Médicament expiré
        tenter("Médicament expiré", ventes, pharmacien, Map.of(perime, 1), false);

        // 5. Médicament absent du stock
        tenter("Médicament absent", ventes, pharmacien, Map.of(absent, 1), false);

        // Le stock ne doit avoir changé que pour la vente n°1
        System.out.println("\nStock final : " + centrale.getStocks());

        // Recherche dans le réseau
        RechercheService recherche = new RechercheService();
        List<Pharmacie> reseau = List.of(centrale, atlas);
        try {
            System.out.println("\nRecherche 'doliprane' :");
            recherche.rechercher("doliprane", reseau)
                    .forEach((p, meds) -> System.out.println("  " + p.getNom() + " -> " + meds));
            recherche.rechercher("zzz", reseau);
        } catch (PharmaNetException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    private static void tenter(String titre, VenteService service, Pharmacien pharmacien,
                               Map<Medicament, Integer> panier, boolean ordonnance) {
        System.out.println("\n--- " + titre + " ---");
        try {
            Vente v = service.vendre(pharmacien, panier, ordonnance);
            System.out.println("OK : " + v);
        } catch (PharmaNetException e) {
            System.out.println("REFUSÉ : " + e.getMessage());
        }
    }
}