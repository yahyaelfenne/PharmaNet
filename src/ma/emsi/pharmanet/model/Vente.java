package ma.emsi.pharmanet.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Vente implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private final LocalDateTime date;
    private final Pharmacien pharmacien;
    private final List<LigneVente> lignes = new ArrayList<>();
    private boolean validee;

    public Vente(int id, Pharmacien pharmacien) {
        if (pharmacien == null) throw new IllegalArgumentException("Pharmacien obligatoire");
        this.id = id;
        this.pharmacien = pharmacien;
        this.date = LocalDateTime.now();
    }

    public void ajouterLigne(Medicament medicament, int quantite) {
        if (validee) throw new IllegalStateException("Vente déjà validée");
        lignes.add(new LigneVente(medicament, quantite));
    }

    public double calculerTotal() {
        return lignes.stream().mapToDouble(LigneVente::calculerSousTotal).sum();
    }


    public boolean validerVente() {
        if (lignes.isEmpty()) return false;
        validee = true;
        return true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDate() { return date; }
    public Pharmacien getPharmacien() { return pharmacien; }
    public boolean isValidee() { return validee; }
    public List<LigneVente> getLignes() { return Collections.unmodifiableList(lignes); }

    @Override
    public String toString() {
        return "Vente{id=" + id + ", date=" + date + ", lignes=" + lignes.size()
                + ", total=" + String.format("%.2f", calculerTotal()) + " MAD}";
    }
}