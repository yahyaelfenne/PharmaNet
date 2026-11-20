package ma.emsi.pharmanet.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public abstract class Utilisateur implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String email;
    private String MotDePasseHash;
    private boolean actif = true;

    protected Utilisateur(int id, String nom, String email, String motDePasse) {
        this.id = id;
        setNom(nom);
        setEmail(email);
        setMotDePasse(motDePasse);
    }

    public abstract String getRole();

    public boolean seConnecter(String email , String MotDePasse){
        return actif
                && this.email.equalsIgnoreCase(email)
                && this.MotDePasseHash.equals(hasher(MotDePasse));
    }

    protected static String hasher(String texte){

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(texte.getBytes(StandardCharsets.UTF_8)));

        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponible",e);
        }
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getNom(){
        return nom;
    }
    public void setNom(String nom){
        if ( nom == null || nom.isBlank()){
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        this.nom = nom.trim();
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        if (email == null || !email.contains("@")){
            throw new IllegalArgumentException("Email invalide" +email);
        }
        this.email = email.trim();
    }
    public String getMotDePasseHash(){
        return MotDePasseHash;
    }
    public void setMotDePasse(String MotDePasse){
        if (MotDePasse == null || MotDePasse.length() < 4){
            throw new IllegalArgumentException("Mot de passe trop court (4 caractères minimum)");
        }
        this.MotDePasseHash = hasher(MotDePasse);
    }
    public void setMotDePasseHash(String Hash){
        this.MotDePasseHash = Hash;
    }

    public boolean isActif(){
        return actif;
    }
    public void setActif(boolean actif){
        this.actif = actif;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Utilisateur u)) return false;
        return email.equalsIgnoreCase(u.email);
    }

    @Override
    public int hashCode(){
        return Objects.hash(email.toLowerCase());
    }

    @Override
    public String toString(){
        return getRole() + "{id=" + id + ",nom='" + nom + "',email='" + email + "',actif=" + actif +"}";
    }

}
