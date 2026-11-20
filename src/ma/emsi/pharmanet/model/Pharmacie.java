package ma.emsi.pharmanet.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Pharmacie implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String adresse;
    private String ville;
    private String telephone;
    private boolean actif = true;
    private LocalDate gardeDebut;
    private LocalDate gardeFin;

    private final List<Stock> stocks = new ArrayList<>();
    private final List<Vente> ventes = new ArrayList<>();

    public Pharmacie(int id, String nom, String adresse, String ville, String telephone) {
        this.id = id;
        setNom(nom);
        this.adresse = adresse;
        this.ville = ville;
        this.telephone = telephone;
    }

    // ----- Stock -----
    public void ajouterStock(Medicament medicament, int quantite) {
        if (quantite <= 0) throw new IllegalArgumentException("La quantité doit être positive");
        trouverStock(medicament).ifPresentOrElse(
                s -> {
                    Medicament existant = s.getMedicament();
                    if (existant.getClass() != medicament.getClass() || existant.getPrix() != medicament.getPrix()) {
                        throw new IllegalArgumentException("Ce lot existe avec un prix ou un type différent");
                    }
                    s.ajouterQuantite(quantite);
                },
                () -> stocks.add(new Stock(medicament, quantite)));
    }

    public Optional<Stock> trouverStock(Medicament medicament) {
        return stocks.stream()
                .filter(s -> s.getMedicament().equals(medicament))
                .findFirst();
    }

    public List<Medicament> rechercherMedicament(String nom) {
        String recherche = nom == null ? "" : nom.toLowerCase();
        return stocks.stream()
                .filter(s -> s.getQuantite() > 0)
                .map(Stock::getMedicament)
                .filter(m -> !m.estExpire())
                .filter(m -> m.getNom().toLowerCase().contains(recherche))
                .toList();
    }

    public List<Medicament> medicamentsProchesExpiration(int jours) {
        return stocks.stream()
                .map(Stock::getMedicament)
                .filter(m -> m.estProcheExpiration(jours))
                .sorted((a, b) -> a.getDateExpiration().compareTo(b.getDateExpiration()))
                .toList();
    }

    // ----- Ventes -----
    public void ajouterVente(Vente vente) {
        ventes.add(Objects.requireNonNull(vente));
    }

    // ----- Garde -----
    public void definirGarde(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            throw new IllegalArgumentException("Période de garde invalide");
        }
        this.gardeDebut = debut;
        this.gardeFin = fin;
    }

    public boolean estDeGarde() {
        return estDeGarde(LocalDate.now());
    }

    public boolean estDeGarde(LocalDate jour) {
        return actif && gardeDebut != null && !jour.isBefore(gardeDebut) && !jour.isAfter(gardeFin);
    }

    // ----- Getters / setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom est obligatoire");
        this.nom = nom.trim();
    }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public List<Stock> getStocks() { return Collections.unmodifiableList(stocks); }
    public List<Vente> getVentes() { return Collections.unmodifiableList(ventes); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pharmacie p)) return false;
        return nom.equalsIgnoreCase(p.nom) && Objects.equals(adresse, p.adresse);
    }

    @Override
    public int hashCode() { return Objects.hash(nom.toLowerCase(), adresse); }

    @Override
    public String toString() {
        return "Pharmacie{" + nom + ", " + adresse + ", " + ville + ", tél " + telephone + "}";
    }
}