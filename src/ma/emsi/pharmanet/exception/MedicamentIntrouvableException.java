package ma.emsi.pharmanet.exception;

public class MedicamentIntrouvableException extends PharmaNetException   {
    public MedicamentIntrouvableException(String nomMedicament){
        super("Medicament introuvable : "+nomMedicament);
    }
    
}
