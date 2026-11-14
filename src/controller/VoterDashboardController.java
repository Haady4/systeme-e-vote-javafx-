package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Election;
import model.User;
import service.AuthService;
import service.ElectionService;
import utils.SceneUtil;

import java.io.IOException;
import java.util.List;

public class VoterDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<Election> electionListView;

    @FXML
    private Button voteButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ListView<Election> resultsElectionListView;

    @FXML
    private Button viewResultsButton;

    private final ElectionService electionService = new ElectionService();
    private User currentUser;

    @FXML
    public void initialize() {
        electionService.mettreAJourStatutsExpires();
        currentUser = AuthService.getUtilisateurConnecte();
        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getPrenom() + " " + currentUser.getNom());
            chargerElections();
            chargerResultatsPublies();
        }
    }

    private void chargerElections() {
        List<Election> elections = electionService.getElectionsDisponibles(currentUser);
        electionListView.setItems(FXCollections.observableArrayList(elections));
    }

    private void chargerResultatsPublies() {
        List<Election> resultats = electionService.getElectionsResultatsPublies(currentUser);
        resultsElectionListView.setItems(FXCollections.observableArrayList(resultats));
    }

    @FXML
    private void handleOuvrirScrutin() {
        Election selectedElection = electionListView.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/vote_form.fxml"));
            Parent root = loader.load();

            VoteFormController controller = loader.getController();
            controller.setElectionData(selectedElection, currentUser);

            Stage stage = (Stage) voteButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoirResultats() {
        Election selectedElection = resultsElectionListView.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/results.fxml"));
            Parent root = loader.load();

            ResultsController controller = loader.getController();
            controller.setElectionData(selectedElection);

            Stage stage = (Stage) viewResultsButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        AuthService.deconnecter();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root, 400, 350);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}