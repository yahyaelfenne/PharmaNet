package ma.emsi.pharmanet.exception;

public class StockInsuffisantException extends PharmaNetException {
    private final int demande;
    private final int disponible;

    public StockInsuffisantException(String nomMedicament, int demande, int disponible) {
        super("Stock insuffisant pour " + nomMedicament
                + " : demandé " + demande + ", disponible " + disponible);
        this.demande = demande;
        this.disponible = disponible;
    }

    public int getDemande() { return demande; }
    public int getDisponible() { return disponible; }
}