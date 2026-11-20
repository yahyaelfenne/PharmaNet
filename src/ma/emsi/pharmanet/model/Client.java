package ma.emsi.pharmanet.model;

import java.util.List;


public class Client extends Utilisateur {

    private static final long serialVersionUID = 1L;

    public Client(int id, String nom, String email, String motDePasse) {
        super(id, nom, email, motDePasse);
    }

    @Override
    public String getRole() { return "CLIENT"; }


    public List<Pharmacie> rechercherMedicament(String nom, List<Pharmacie> pharmacies) {
        return pharmacies.stream()
                .filter(Pharmacie::isActif)
                .filter(p -> !p.rechercherMedicament(nom).isEmpty())
                .toList();
    }

    public List<Pharmacie> consulterPharmaciesDeGarde(List<Pharmacie> pharmacies) {
        return pharmacies.stream()
                .filter(Pharmacie::isActif)
                .filter(Pharmacie::estDeGarde)
                .toList();
    }


    public List<Pharmacie> consulterPharmaciesDeGarde(List<Pharmacie> pharmacies, String ville) {
        return consulterPharmaciesDeGarde(pharmacies).stream()
                .filter(p -> p.getVille().equalsIgnoreCase(ville))
                .toList();
    }
}