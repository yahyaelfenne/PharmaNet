package ma.emsi.pharmanet.model;

import java.time.LocalDate;


public class MedicamentLibre extends Medicament {

    private static final long serialVersionUID = 1L;


    private static final double TAUX_TAXE = 0.20;

    public MedicamentLibre(int id, String nom, double prix, LocalDate dateExpiration) {
        super(id, nom, prix, dateExpiration);
    }

    @Override
    public double calculerPrix() {
        return getPrix() * (1 + TAUX_TAXE);
    }
}