package controller;

import dao.ElectionDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Election;
import service.AuthService;
import service.ElectionService;
import utils.SceneUtil;

import java.io.IOException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private ListView<Election> electionListView;

    @FXML
    private Button viewResultsButton;

    @FXML
    private Button publishResultsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button nouvelleElectionButton;

    @FXML
    private Button ouvrirScrutinButton;

    @FXML
    private Button fermerScrutinButton;

    @FXML
    private Button gererCandidatsButton;

    @FXML
    private Button gererUtilisateursButton;

    private final ElectionDao electionDao = new ElectionDao();
    private final ElectionService electionService = new ElectionService();

    @FXML
    public void initialize() {
        electionService.mettreAJourStatutsExpires();
        chargerToutesLesElections();
    }

    private void chargerToutesLesElections() {
        List<Election> me = electionDao.getToutesLesElections();
        electionListView.setItems(FXCollections.observableArrayList(me));
    }

    @FXML
    private void handleVoirResultats() {
        Election selectedElection = electionListView.getSelectionModel().getSelectedItem();
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
    private void handlePublierResultats() {
        Election selectedElection = electionListView.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            afficherAlerte(AlertType.WARNING, "Aucune élection sélectionnée",
                    "Clique d'abord sur une élection dans la liste ci-dessus.");
            return;
        }

        boolean succes = electionService.publierResultats(selectedElection.getId());

        if (succes) {
            chargerToutesLesElections();
            afficherAlerte(AlertType.INFORMATION, "Résultats publiés",
                    "Les résultats de \"" + selectedElection.getTitre() + "\" sont maintenant visibles par les électeurs.");
        } else {
            afficherAlerte(AlertType.ERROR, "Échec",
                    "La publication des résultats a échoué.");
        }
    }

    @FXML
    private void handleNouvelleElection() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/election_form.fxml"));
            Stage stage = (Stage) nouvelleElectionButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOuvrirScrutin() {
        Election selectedElection = electionListView.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            afficherAlerte(AlertType.WARNING, "Aucune élection sélectionnée",
                    "Clique d'abord sur une élection dans la liste ci-dessus.");
            return;
        }
        electionDao.changerStatut(selectedElection.getId(), Election.Statut.OUVERTE);
        chargerToutesLesElections();
        afficherAlerte(AlertType.INFORMATION, "Scrutin ouvert",
                "Le scrutin \"" + selectedElection.getTitre() + "\" est maintenant OUVERT.");
    }

    @FXML
    private void handleFermerScrutin() {
        Election selectedElection = electionListView.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            afficherAlerte(AlertType.WARNING, "Aucune élection sélectionnée",
                    "Clique d'abord sur une élection dans la liste ci-dessus.");
            return;
        }
        electionDao.changerStatut(selectedElection.getId(), Election.Statut.FERMEE);
        chargerToutesLesElections();
        afficherAlerte(AlertType.INFORMATION, "Scrutin fermé",
                "Le scrutin \"" + selectedElection.getTitre() + "\" est maintenant FERMÉ.");
    }

    private void afficherAlerte(AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }

    @FXML
    private void handleGererCandidats() {
        Election selectedElection = electionListView.getSelectionModel().getSelectedItem();
        if (selectedElection == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/candidat_form.fxml"));
            Parent root = loader.load();

            CandidatFormController controller = loader.getController();
            controller.setElection(selectedElection);

            Stage stage = (Stage) gererCandidatsButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGererUtilisateurs() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/user_management.fxml"));
            Stage stage = (Stage) gererUtilisateursButton.getScene().getWindow();
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