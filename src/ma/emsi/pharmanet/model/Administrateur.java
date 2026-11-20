package ma.emsi.pharmanet.model;


public class Administrateur extends Utilisateur {

    private static final long serialVersionUID = 1L;

    public Administrateur(int id, String nom, String email, String motDePasse) {
        super(id, nom, email, motDePasse);
    }

    @Override
    public String getRole() { return "ADMINISTRATEUR"; }

    public void activerPharmacie(Pharmacie pharmacie)    { pharmacie.setActif(true); }
    public void desactiverPharmacie(Pharmacie pharmacie) { pharmacie.setActif(false); }

    public void activerCompte(Utilisateur utilisateur)    { utilisateur.setActif(true); }
    public void desactiverCompte(Utilisateur utilisateur) {
        if (utilisateur.equals(this)) {
            throw new IllegalArgumentException("Un administrateur ne peut pas désactiver son propre compte");
        }
        utilisateur.setActif(false);
    }
}