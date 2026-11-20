package ma.emsi.pharmanet.model;

import java.time.LocalDate;


public class MedicamentSurOrdonnance extends Medicament {

    private static final long serialVersionUID = 1L;


    private static final double TAUX_TAXE = 0.07;

    private final boolean ordonnanceObligatoire = true;

    public MedicamentSurOrdonnance(int id, String nom, double prix, LocalDate dateExpiration) {
        super(id, nom, prix, dateExpiration);
    }

    @Override
    public double calculerPrix() {
        return getPrix() * (1 + TAUX_TAXE);
    }


    public boolean verifierOrdonnance(boolean ordonnancePresentee) {
        return !ordonnanceObligatoire || ordonnancePresentee;
    }

    public boolean isOrdonnanceObligatoire() { return ordonnanceObligatoire; }
}