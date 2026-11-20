package ma.emsi.pharmanet.model;

public class Pharmacien extends Utilisateur {

    private static final long serialVersionUID = 1L;

    private String matricule;
    private Pharmacie pharmacie;

    public Pharmacien(int id, String nom, String email, String motDePasse,
                      String matricule, Pharmacie pharmacie) {
        super(id, nom, email, motDePasse);
        setMatricule(matricule);
        this.pharmacie = pharmacie;
    }

    @Override
    public String getRole() { return "PHARMACIEN"; }

    public void ajouterAuStock(Medicament medicament, int quantite) {
        verifierPharmacie();
        pharmacie.ajouterStock(medicament, quantite);
    }

    public boolean retirerDuStock(Medicament medicament, int quantite) {
        verifierPharmacie();
        return pharmacie.trouverStock(medicament)
                .map(s -> s.retirerQuantite(quantite))
                .orElse(false);
    }

    public void enregistrerVente(Vente vente) {
        verifierPharmacie();
        if (!vente.validerVente()) {
            throw new IllegalStateException("Vente invalide : aucune ligne");
        }
        pharmacie.ajouterVente(vente);
    }

    private void verifierPharmacie() {
        if (pharmacie == null) {
            throw new IllegalStateException("Ce pharmacien n'est rattaché à aucune pharmacie");
        }
    }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) {
        if (matricule == null || matricule.isBlank()) {
            throw new IllegalArgumentException("Le matricule est obligatoire");
        }
        this.matricule = matricule.trim();
    }

    public Pharmacie getPharmacie() { return pharmacie; }
    public void setPharmacie(Pharmacie pharmacie) { this.pharmacie = pharmacie; }
}