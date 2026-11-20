package ma.emsi.pharmanet.exception;

import java.time.LocalDate;

public class MedicamentExpireException extends PharmaNetException {
    public MedicamentExpireException(String nomMedicament, LocalDate dateExpiration) {
        super("Médicament expiré : " + nomMedicament + " (expiré le " + dateExpiration + ")");
    }
}