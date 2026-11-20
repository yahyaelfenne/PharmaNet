package ma.emsi.pharmanet.console;

import ma.emsi.pharmanet.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * État du réseau initialisé avec des données de démonstration.
 */
public class DonneesDemo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int prochainIdMedicament = 100;

    public int prochainIdMedicament() { return prochainIdMedicament++; }

    private final List<Pharmacie> pharmacies = new ArrayList<>();
    private final List<Utilisateur> utilisateurs = new ArrayList<>();

    public DonneesDemo() {
        Pharmacie centrale = new Pharmacie(1, "Pharmacie Centrale", "12 Bd Zerktouni", "Casablanca", "0522000001");
        Pharmacie atlas = new Pharmacie(2, "Pharmacie Atlas", "5 Rue Hassan II", "Rabat", "0537000002");
        centrale.definirGarde(LocalDate.now().minusDays(1), LocalDate.now().plusDays(2));

        Medicament doliprane = new MedicamentLibre(1, "Doliprane 1000", 15.0, LocalDate.now().plusYears(1));
        Medicament vitamineC = new MedicamentLibre(2, "Vitamine C", 20.0, LocalDate.now().plusDays(10));
        Medicament amoxicilline = new MedicamentSurOrdonnance(3, "Amoxicilline 500", 40.0, LocalDate.now().plusMonths(8));
        Medicament sirop = new MedicamentLibre(4, "Sirop ancien", 12.0, LocalDate.now().minusDays(5));

        centrale.ajouterStock(doliprane, 50);
        centrale.ajouterStock(vitamineC, 30);
        centrale.ajouterStock(amoxicilline, 20);
        centrale.ajouterStock(sirop, 5);
        atlas.ajouterStock(doliprane, 10);

        pharmacies.add(centrale);
        pharmacies.add(atlas);

        utilisateurs.add(new Administrateur(1, "Admin", "admin@pharmanet.ma", "admin123"));
        utilisateurs.add(new Pharmacien(2, "Karim Alami", "karim@pharmanet.ma", "secret123", "PH-001", centrale));
        utilisateurs.add(new Client(3, "Sara Bennani", "sara@mail.com", "client123"));
    }

    public List<Pharmacie> getPharmacies() { return pharmacies; }
    public List<Utilisateur> getUtilisateurs() { return utilisateurs; }
}