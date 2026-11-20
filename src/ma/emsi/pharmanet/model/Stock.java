package ma.emsi.pharmanet.model;


public class Stock implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final Medicament medicament;
    private int quantite;

    public Stock(Medicament medicament, int quantite) {
        if (medicament == null) throw new IllegalArgumentException("Médicament obligatoire");
        if (quantite < 0) throw new IllegalArgumentException("Quantité négative");
        this.medicament = medicament;
        this.quantite = quantite;
    }

    public void ajouterQuantite(int qte) {
        if (qte <= 0) throw new IllegalArgumentException("La quantité à ajouter doit être positive");
        if (qte > Integer.MAX_VALUE - quantite) {
            throw new IllegalArgumentException("La quantité maximale en stock serait dépassée");
        }
        quantite += qte;
    }


    public boolean retirerQuantite(int qte) {
        if (qte <= 0) throw new IllegalArgumentException("La quantité à retirer doit être positive");
        if (!verifierDisponibilite(qte)) return false;
        quantite -= qte;
        return true;
    }

    public boolean verifierDisponibilite(int qte) {
        return qte > 0 && quantite >= qte;
    }

    public Medicament getMedicament() { return medicament; }
    public int getQuantite() { return quantite; }

    @Override
    public String toString() {
        return medicament.getNom() + " x" + quantite;
    }
}