package controller;

import dao.CandidatDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Candidat;
import model.Election;
import utils.SceneUtil;

import java.io.IOException;
import java.util.List;

public class CandidatFormController {

    @FXML
    private Label electionTitleLabel;

    @FXML
    private ListView<Candidat> candidatListView;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField professionField;

    @FXML
    private TextArea programmeArea;

    @FXML
    private Label statusLabel;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button backButton;

    private final CandidatDao candidatDao = new CandidatDao();
    private Election currentElection;

    public void setElection(Election election) {
        this.currentElection = election;
        electionTitleLabel.setText("Candidats de : " + election.getTitre());
        chargerCandidats();
    }

    private void chargerCandidats() {
        List<Candidat> candidats = candidatDao.getCandidatsParElection(currentElection.getId());
        candidatListView.setItems(FXCollections.observableArrayList(candidats));
    }

    @FXML
    private void handleAjouterCandidat() {
        String nom = nomField.getText() == null ? "" : nomField.getText().trim();
        String prenom = prenomField.getText() == null ? "" : prenomField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty()) {
            statusLabel.setText("Le nom et le prénom sont obligatoires.");
            return;
        }

        Candidat c = new Candidat();
        c.setElectionId(currentElection.getId());
        c.setNom(nom);
        c.setPrenom(prenom);
        c.setProfession(professionField.getText());
        c.setProgramme(programmeArea.getText());

        boolean succes = candidatDao.ajouterCandidat(c);

        if (succes) {
            nomField.clear();
            prenomField.clear();
            professionField.clear();
            programmeArea.clear();
            statusLabel.setText("");
            chargerCandidats();
        } else {
            statusLabel.setText("Erreur lors de l'ajout du candidat.");
        }
    }

    @FXML
    private void handleSupprimerCandidat() {
        Candidat selected = candidatListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un candidat à supprimer.");
            return;
        }
        candidatDao.supprimerCandidat(selected.getId());
        chargerCandidats();
    }

    @FXML
    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/admin_dashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
