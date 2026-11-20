package ma.emsi.pharmanet.service;

import ma.emsi.pharmanet.exception.MedicamentIntrouvableException;
import ma.emsi.pharmanet.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RechercheService {

    /** Pour chaque pharmacie active, la liste des médicaments trouvés (les pharmacies sans résultat sont ignorées). */
    public Map<Pharmacie, List<Medicament>> rechercher(String nom, List<Pharmacie> reseau)
            throws MedicamentIntrouvableException {

        Map<Pharmacie, List<Medicament>> resultats = reseau.stream()
                .filter(Pharmacie::isActif)
                .collect(Collectors.toMap(p -> p, p -> p.rechercherMedicament(nom)));

        resultats.values().removeIf(List::isEmpty);

        if (resultats.isEmpty()) {
            throw new MedicamentIntrouvableException(nom);
        }
        return resultats;
    }
}