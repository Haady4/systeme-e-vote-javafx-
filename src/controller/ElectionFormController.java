package controller;

import dao.ElectionDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Election;
import model.Election.CibleRole;
import model.Election.Statut;
import utils.SceneUtil;

import java.io.IOException;
import java.time.LocalDateTime;

public class ElectionFormController {

    @FXML
    private TextField titreField;

    @FXML
    private TextField typeField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private ComboBox<CibleRole> cibleRoleCombo;

    @FXML
    private TextField ufrField;

    @FXML
    private TextField departementField;

    @FXML
    private TextField niveauField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button creerButton;

    @FXML
    private Button backButton;

    private final ElectionDao electionDao = new ElectionDao();

    @FXML
    public void initialize() {
        cibleRoleCombo.setItems(FXCollections.observableArrayList(CibleRole.values()));
        cibleRoleCombo.getSelectionModel().select(CibleRole.TOUS);
    }

    @FXML
    private void handleCreerElection() {
        String titre = titreField.getText() == null ? "" : titreField.getText().trim();
        String type = typeField.getText() == null ? "" : typeField.getText().trim();

        if (titre.isEmpty() || type.isEmpty() || dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            statusLabel.setText("Veuillez remplir le titre, le type et les deux dates.");
            return;
        }

        LocalDateTime dateDebut = dateDebutPicker.getValue().atStartOfDay();
        LocalDateTime dateFin = dateFinPicker.getValue().atTime(23, 59, 59);

        if (!dateFin.isAfter(dateDebut)) {
            statusLabel.setText("La date de fin doit être après la date de début.");
            return;
        }

        Election election = new Election();
        election.setTitre(titre);
        election.setTypeElection(type);
        election.setDateDebut(dateDebut);
        election.setDateFin(dateFin);
        election.setStatut(Statut.EN_PREPARATION);
        election.setCibleRole(cibleRoleCombo.getValue() == null ? CibleRole.TOUS : cibleRoleCombo.getValue());
        election.setCibleUfr(videSiVide(ufrField.getText()));
        election.setCibleDepartement(videSiVide(departementField.getText()));
        election.setCibleNiveau(videSiVide(niveauField.getText()));

        boolean succes = electionDao.creerElection(election);

        if (succes) {
            handleRetour();
        } else {
            statusLabel.setText("Erreur lors de la création de l'élection.");
        }
    }

    private String videSiVide(String s) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
