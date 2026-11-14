package model;

public class User {
    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String login;
    private String password;
    private Role role;
    private String ufr;
    private String departement;
    private String niveauFiliere;

    public enum Role {
        ADMIN, ETUDIANT, ENSEIGNANT
    }

    
    public User() {}

    
    public User(int id, String matricule, String nom, String prenom, String email, 
                String login, String password, Role role, String ufr, 
                String departement, String niveauFiliere) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
        this.ufr = ufr;
        this.departement = departement;
        this.niveauFiliere = niveauFiliere;
    }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getUfr() { return ufr; }
    public void setUfr(String ufr) { this.ufr = ufr; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getNiveauFiliere() { return niveauFiliere; }
    public void setNiveauFiliere(String niveauFiliere) { this.niveauFiliere = niveauFiliere; }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + matricule + ") - " + role;
    }
}