package ma.emsi.pharmanet.model;


public class LigneVente implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final Medicament medicament;
    private final int quantite;
    private final double prixUnitaire;

    public LigneVente(Medicament medicament, int quantite) {
        if (medicament == null) throw new IllegalArgumentException("Médicament obligatoire");
        if (quantite <= 0) throw new IllegalArgumentException("La quantité doit être positive");
        this.medicament = medicament;
        this.quantite = quantite;
        this.prixUnitaire = medicament.calculerPrix(); // prix figé au moment de la vente
    }

    public double calculerSousTotal() {
        return prixUnitaire * quantite;
    }

    public Medicament getMedicament() { return medicament; }
    public int getQuantite() { return quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }

    @Override
    public String toString() {
        return medicament.getNom() + " x" + quantite + " = " + String.format("%.2f", calculerSousTotal()) + " MAD";
    }
}