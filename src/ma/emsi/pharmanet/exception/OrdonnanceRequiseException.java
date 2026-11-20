package ma.emsi.pharmanet.exception;

public class OrdonnanceRequiseException extends PharmaNetException {
    public OrdonnanceRequiseException(String nomMedicament) {
        super("Ordonnance obligatoire pour : " + nomMedicament);
    }
}