package ma.emsi.pharmanet.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public abstract class Medicament implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private double prix; // prix de base hors taxe
    private LocalDate dateExpiration;

    protected Medicament(int id, String nom, double prix, LocalDate dateExpiration) {
        this.id = id;
        setNom(nom);
        setPrix(prix);
        setDateExpiration(dateExpiration);
    }

    public abstract double calculerPrix();

    public boolean estExpire() {
        return dateExpiration.isBefore(LocalDate.now());
    }


    public boolean estProcheExpiration(int jours) {
        long restant = ChronoUnit.DAYS.between(LocalDate.now(), dateExpiration);
        return restant >= 0 && restant <= jours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du médicament est obligatoire");
        }
        this.nom = nom.trim();
    }

    public double getPrix() { return prix; }
    public void setPrix(double prix) {
        if (!Double.isFinite(prix) || prix < 0 || prix > 1_000_000_000) {
            throw new IllegalArgumentException("Le prix doit être compris entre 0 et 1 milliard de MAD");
        }
        this.prix = prix;
    }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) {
        if (dateExpiration == null) throw new IllegalArgumentException("Date d'expiration obligatoire");
        this.dateExpiration = dateExpiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament m)) return false;
        return nom.equalsIgnoreCase(m.nom) && dateExpiration.equals(m.dateExpiration);
    }

    @Override
    public int hashCode() { return Objects.hash(nom.toLowerCase(), dateExpiration); }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{nom='" + nom + "', prix=" + String.format("%.2f", calculerPrix())
                + " MAD, expire le " + dateExpiration + "}";
    }
}