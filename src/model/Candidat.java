package model;

public class Candidat {
    private int id;
    private int electionId;
    private String nom;
    private String prenom;
    private String profession;
    private String programme;
    private String photo;

    public Candidat() {}

    public Candidat(int id, int electionId, String nom, String prenom, String profession, String programme, String photo) {
        this.id = id;
        this.electionId = electionId;
        this.nom = nom;
        this.prenom = prenom;
        this.profession = profession;
        this.programme = programme;
        this.photo = photo;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}