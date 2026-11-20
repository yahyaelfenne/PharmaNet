package ma.emsi.pharmanet.console;

import ma.emsi.pharmanet.exception.MedicamentIntrouvableException;
import ma.emsi.pharmanet.exception.PharmaNetException;
import ma.emsi.pharmanet.model.*;
import ma.emsi.pharmanet.service.RechercheService;
import ma.emsi.pharmanet.service.VenteService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);
    private final DonneesDemo donnees;
    private final FichierDonnees fichier;
    private final VenteService venteService = new VenteService();
    private final RechercheService rechercheService = new RechercheService();

    private ConsoleApp(FichierDonnees fichier) throws IOException, ClassNotFoundException {
        this.fichier = fichier;
        this.donnees = fichier.charger();
    }

    public static void main(String[] args) {
        Path chemin = Path.of(args.length == 0 ? "pharmanet.dat" : args[0]);
        try (FichierDonnees fichier = new FichierDonnees(chemin)) {
            new ConsoleApp(fichier).demarrer();
        } catch (NoSuchElementException e) {
            System.out.println("\nFin de la saisie.");
        } catch (IOException | UncheckedIOException | ClassNotFoundException e) {
            System.err.println("Erreur de données : " + e.getMessage());
            System.exit(1);
        }
    }

    private void sauvegarder() {
        try {
            fichier.sauvegarder(donnees);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ================= Accueil et connexion =================

    private void demarrer() {
        titre("PHARMANET - Réseau des pharmacies");
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n1. Se connecter");
            System.out.println("0. Quitter");
            switch (lireEntier("Choix : ")) {
                case 1 -> connexion();
                case 0 -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
        System.out.println("Au revoir !");
    }

    private void connexion() {
        String email = lireTexte("Email : ");
        String motDePasse = lireTexte("Mot de passe : ");

        Utilisateur connecte = donnees.getUtilisateurs().stream()
                .filter(u -> u.seConnecter(email, motDePasse))
                .findFirst()
                .orElse(null);

        if (connecte == null) {
            System.out.println("Identifiants incorrects ou compte désactivé.");
            return;
        }
        System.out.println("\nBienvenue " + connecte.getNom() + " (" + connecte.getRole() + ")");

        if (connecte instanceof Client c) {
            menuClient(c);
        } else if (connecte instanceof Pharmacien p) {
            menuPharmacien(p);
        } else if (connecte instanceof Administrateur a) {
            menuAdministrateur(a);
        }
    }

    // ================= Espace client =================

    private void menuClient(Client client) {
        boolean enCours = true;
        while (enCours) {
            titre("ESPACE CLIENT");
            System.out.println("1. Rechercher un médicament");
            System.out.println("2. Pharmacies de garde");
            System.out.println("3. Pharmacies de garde par ville");
            System.out.println("0. Déconnexion");
            switch (lireEntier("Choix : ")) {
                case 1 -> rechercherMedicament();
                case 2 -> afficherPharmacies(client.consulterPharmaciesDeGarde(donnees.getPharmacies()));
                case 3 -> {
                    String ville = lireTexte("Ville : ");
                    afficherPharmacies(client.consulterPharmaciesDeGarde(donnees.getPharmacies(), ville));
                }
                case 0 -> enCours = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private void rechercherMedicament() {
        String nom = lireTexte("Nom du médicament : ");
        try {
            Map<Pharmacie, List<Medicament>> resultats =
                    rechercheService.rechercher(nom, donnees.getPharmacies());
            resultats.forEach((pharmacie, medicaments) -> {
                System.out.println("\n" + pharmacie.getNom() + " - " + pharmacie.getAdresse()
                        + ", " + pharmacie.getVille() + " - Tél " + pharmacie.getTelephone()
                        + (pharmacie.estDeGarde() ? " [DE GARDE]" : ""));
                for (Medicament m : medicaments) {
                    int quantite = pharmacie.trouverStock(m).map(Stock::getQuantite).orElse(0);
                    System.out.printf("   - %s : %.2f MAD (%d en stock)%n", m.getNom(), m.calculerPrix(), quantite);
                }
            });
        } catch (MedicamentIntrouvableException e) {
            System.out.println(e.getMessage());
        }
    }

    private void afficherPharmacies(List<Pharmacie> pharmacies) {
        if (pharmacies.isEmpty()) {
            System.out.println("Aucune pharmacie trouvée.");
            return;
        }
        for (Pharmacie p : pharmacies) {
            System.out.println("  - " + p.getNom() + " - " + p.getAdresse() + ", "
                    + p.getVille() + " - Tél " + p.getTelephone());
        }
    }

    // ================= Espace pharmacien =================

    private void menuPharmacien(Pharmacien pharmacien) {
        Pharmacie pharmacie = pharmacien.getPharmacie();
        if (pharmacie == null || !pharmacie.isActif()) {
            System.out.println("Aucune pharmacie active associée à ce compte.");
            return;
        }
        boolean enCours = true;
        while (enCours) {
            titre("ESPACE PHARMACIEN - " + pharmacie.getNom());
            System.out.println("1. Voir le stock");
            System.out.println("2. Ajouter un médicament au stock");
            System.out.println("3. Enregistrer une vente");
            System.out.println("4. Médicaments proches de l'expiration");
            System.out.println("5. Retirer des unités du stock");
            System.out.println("6. Historique des ventes");
            System.out.println("0. Déconnexion");
            switch (lireEntier("Choix : ")) {
                case 1 -> afficherStock(pharmacie);
                case 2 -> ajouterMedicament(pharmacien);
                case 3 -> vendre(pharmacien);
                case 4 -> afficherExpirations(pharmacie);
                case 5 -> retirerStock(pharmacien);
                case 6 -> afficherVentes(pharmacie);
                case 0 -> enCours = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherStock(Pharmacie pharmacie) {
        List<Stock> stocks = pharmacie.getStocks();
        if (stocks.isEmpty()) {
            System.out.println("Le stock est vide.");
            return;
        }
        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            Medicament m = s.getMedicament();
            System.out.printf("%2d. %-22s x%-4d %8.2f MAD  exp. %s%s%n",
                    i + 1, m.getNom(), s.getQuantite(), m.calculerPrix(),
                    m.getDateExpiration(), m.estExpire() ? "  [EXPIRÉ]" : "");
        }
    }

    private void ajouterMedicament(Pharmacien pharmacien) {
        try {
            String nom = lireTexte("Nom : ");
            double prix = lireDecimal("Prix de base (MAD) : ");
            LocalDate expiration = LocalDate.parse(lireTexte("Date d'expiration (AAAA-MM-JJ) : "));
            int type = lireEntier("Type (1 = vente libre, 2 = sur ordonnance) : ");
            int quantite = lireEntier("Quantité : ");

            if (type != 1 && type != 2) throw new IllegalArgumentException("Type de médicament invalide");

            Medicament medicament = (type == 2)
                    ? new MedicamentSurOrdonnance(donnees.prochainIdMedicament(), nom, prix, expiration)
                    : new MedicamentLibre(donnees.prochainIdMedicament(), nom, prix, expiration);

            pharmacien.ajouterAuStock(medicament, quantite);
            sauvegarder();
            System.out.println("Médicament ajouté au stock.");
        } catch (DateTimeParseException e) {
            System.out.println("Date invalide : utilisez le format AAAA-MM-JJ (ex. 2027-03-15).");
        } catch (IllegalArgumentException e) {
            System.out.println("Données invalides : " + e.getMessage());
        }
    }

    private void vendre(Pharmacien pharmacien) {
        Pharmacie pharmacie = pharmacien.getPharmacie();
        List<Stock> stocks = new ArrayList<>(pharmacie.getStocks());
        if (stocks.isEmpty()) {
            System.out.println("Le stock est vide.");
            return;
        }
        afficherStock(pharmacie);

        Map<Medicament, Integer> panier = new LinkedHashMap<>();
        while (true) {
            int numero = lireEntier("Numéro du médicament (0 pour terminer) : ");
            if (numero == 0) break;
            if (numero < 1 || numero > stocks.size()) {
                System.out.println("Numéro invalide.");
                continue;
            }
            int quantite = lireEntier("Quantité : ");
            if (quantite <= 0) {
                System.out.println("La quantité doit être positive.");
                continue;
            }
            Medicament medicament = stocks.get(numero - 1).getMedicament();
            int dejaAjoute = panier.getOrDefault(medicament, 0);
            if (quantite > Integer.MAX_VALUE - dejaAjoute) {
                System.out.println("Quantité totale trop grande.");
                continue;
            }
            panier.put(medicament, dejaAjoute + quantite);
        }

        if (panier.isEmpty()) {
            System.out.println("Panier vide, vente annulée.");
            return;
        }
        boolean ordonnance = lireTexte("Ordonnance présentée ? (o/n) : ").equalsIgnoreCase("o");

        try {
            Vente vente = venteService.vendre(pharmacien, panier, ordonnance);
            sauvegarder();
            System.out.printf("%nVente enregistrée : n°%d%n", vente.getId());
            vente.getLignes().forEach(ligne -> System.out.println("  " + ligne));
            System.out.printf("TOTAL : %.2f MAD%n", vente.calculerTotal());
        } catch (PharmaNetException | IllegalArgumentException e) {
            System.out.println("Vente refusée : " + e.getMessage());
        }
    }

    private void retirerStock(Pharmacien pharmacien) {
        Pharmacie pharmacie = pharmacien.getPharmacie();
        afficherStock(pharmacie);
        List<Stock> stocks = pharmacie.getStocks();
        if (stocks.isEmpty()) return;
        int numero = lireEntier("Numéro du lot (0 pour annuler) : ");
        if (numero == 0) return;
        if (numero < 1 || numero > stocks.size()) {
            System.out.println("Numéro invalide.");
            return;
        }
        int quantite = lireEntier("Quantité à retirer : ");
        if (quantite <= 0 || !pharmacien.retirerDuStock(stocks.get(numero - 1).getMedicament(), quantite)) {
            System.out.println("Quantité invalide ou stock insuffisant.");
            return;
        }
        sauvegarder();
        System.out.println("Stock mis à jour.");
    }

    private void afficherVentes(Pharmacie pharmacie) {
        if (pharmacie.getVentes().isEmpty()) System.out.println("Aucune vente enregistrée.");
        for (Vente vente : pharmacie.getVentes()) {
            System.out.println(vente);
            vente.getLignes().forEach(ligne -> System.out.println("  " + ligne));
        }
    }

    private void afficherExpirations(Pharmacie pharmacie) {
        int jours = lireEntier("Dans combien de jours maximum ? ");
        if (jours < 0) {
            System.out.println("Le nombre de jours doit être positif ou nul.");
            return;
        }
        List<Medicament> liste = pharmacie.medicamentsProchesExpiration(jours);
        if (liste.isEmpty()) {
            System.out.println("Aucun médicament n'expire dans les " + jours + " prochains jours.");
            return;
        }
        for (Medicament m : liste) {
            System.out.println("  - " + m.getNom() + " expire le " + m.getDateExpiration());
        }
    }

    // ================= Espace administrateur =================

    private void menuAdministrateur(Administrateur admin) {
        boolean enCours = true;
        while (enCours) {
            titre("ESPACE ADMINISTRATEUR");
            System.out.println("1. Lister les pharmacies");
            System.out.println("2. Activer / désactiver une pharmacie");
            System.out.println("3. Lister les comptes");
            System.out.println("4. Activer / désactiver un compte");
            System.out.println("5. Planifier une garde");
            System.out.println("0. Déconnexion");
            switch (lireEntier("Choix : ")) {
                case 1 -> listerPharmacies();
                case 2 -> basculerPharmacie(admin);
                case 3 -> listerComptes();
                case 4 -> basculerCompte(admin);
                case 5 -> planifierGarde();
                case 0 -> enCours = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    private void listerPharmacies() {
        List<Pharmacie> liste = donnees.getPharmacies();
        for (int i = 0; i < liste.size(); i++) {
            Pharmacie p = liste.get(i);
            System.out.printf("%2d. %-20s %-12s %s%s%n", i + 1, p.getNom(), p.getVille(),
                    p.isActif() ? "[active]" : "[DÉSACTIVÉE]", p.estDeGarde() ? " [de garde]" : "");
        }
    }

    private void basculerPharmacie(Administrateur admin) {
        listerPharmacies();
        int numero = lireEntier("Numéro de la pharmacie (0 pour annuler) : ");
        List<Pharmacie> liste = donnees.getPharmacies();
        if (numero < 1 || numero > liste.size()) return;

        Pharmacie p = liste.get(numero - 1);
        if (p.isActif()) {
            admin.desactiverPharmacie(p);
            sauvegarder();
            System.out.println(p.getNom() + " désactivée.");
        } else {
            admin.activerPharmacie(p);
            sauvegarder();
            System.out.println(p.getNom() + " activée.");
        }
    }

    private void planifierGarde() {
        listerPharmacies();
        List<Pharmacie> pharmacies = donnees.getPharmacies();
        int numero = lireEntier("Numéro de la pharmacie (0 pour annuler) : ");
        if (numero == 0) return;
        if (numero < 1 || numero > pharmacies.size()) {
            System.out.println("Numéro invalide.");
            return;
        }
        try {
            LocalDate debut = LocalDate.parse(lireTexte("Début (AAAA-MM-JJ) : "));
            LocalDate fin = LocalDate.parse(lireTexte("Fin (AAAA-MM-JJ) : "));
            pharmacies.get(numero - 1).definirGarde(debut, fin);
            sauvegarder();
            System.out.println("Garde planifiée.");
        } catch (DateTimeParseException | IllegalArgumentException e) {
            System.out.println("Période invalide. Saisissez deux dates au format AAAA-MM-JJ, dans l’ordre.");
        }
    }

    private void listerComptes() {
        List<Utilisateur> liste = donnees.getUtilisateurs();
        for (int i = 0; i < liste.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, liste.get(i));
        }
    }

    private void basculerCompte(Administrateur admin) {
        listerComptes();
        int numero = lireEntier("Numéro du compte (0 pour annuler) : ");
        List<Utilisateur> liste = donnees.getUtilisateurs();
        if (numero < 1 || numero > liste.size()) return;

        Utilisateur u = liste.get(numero - 1);
        try {
            if (u.isActif()) {
                admin.desactiverCompte(u);
                sauvegarder();
                System.out.println("Compte de " + u.getNom() + " désactivé.");
            } else {
                admin.activerCompte(u);
                sauvegarder();
                System.out.println("Compte de " + u.getNom() + " activé.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= Utilitaires de saisie et d'affichage =================

    private String lireTexte(String invite) {
        System.out.print(invite);
        return scanner.nextLine().trim();
    }

    private int lireEntier(String invite) {
        while (true) {
            try {
                return Integer.parseInt(lireTexte(invite));
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre entier.");
            }
        }
    }

    private double lireDecimal(String invite) {
        while (true) {
            try {
                return Double.parseDouble(lireTexte(invite).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre (ex. 15.50).");
            }
        }
    }

    private void titre(String texte) {
        System.out.println("\n=== " + texte + " ===");
    }
}