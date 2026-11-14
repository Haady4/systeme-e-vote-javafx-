package model;

import java.time.LocalDateTime;

public class Election {
    private int id;
    private String titre;
    private String typeElection;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Statut statut;
    private CibleRole cibleRole;
    private String cibleUfr;
    private String cibleDepartement;
    private String cibleNiveau;

    public enum Statut {
        EN_PREPARATION, OUVERTE, FERMEE
    }

    public enum CibleRole {
        ADMIN, ETUDIANT, ENSEIGNANT, TOUS
    }

    public Election() {}

    public Election(int id, String titre, String typeElection, LocalDateTime dateDebut, 
                    LocalDateTime dateFin, Statut statut, CibleRole cibleRole, 
                    String cibleUfr, String cibleDepartement, String cibleNiveau) {
        this.id = id;
        this.titre = titre;
        this.typeElection = typeElection;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.cibleRole = cibleRole;
        this.cibleUfr = cibleUfr;
        this.cibleDepartement = cibleDepartement;
        this.cibleNiveau = cibleNiveau;
    }

   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getTypeElection() { return typeElection; }
    public void setTypeElection(String typeElection) { this.typeElection = typeElection; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public CibleRole getCibleRole() { return cibleRole; }
    public void setCibleRole(CibleRole cibleRole) { this.cibleRole = cibleRole; }

    public String getCibleUfr() { return cibleUfr; }
    public void setCibleUfr(String cibleUfr) { this.cibleUfr = cibleUfr; }

    public String getCibleDepartement() { return cibleDepartement; }
    public void setCibleDepartement(String cibleDepartement) { this.cibleDepartement = cibleDepartement; }

    public String getCibleNiveau() { return cibleNiveau; }
    public void setCibleNiveau(String cibleNiveau) { this.cibleNiveau = cibleNiveau; }

    @Override
    public String toString() {
        return titre + " [" + statut + "]";
    }
    private boolean resultatsPublies;

    public boolean isResultatsPublies() { return resultatsPublies; }
    public void setResultatsPublies(boolean resultatsPublies) { this.resultatsPublies = resultatsPublies; }
}