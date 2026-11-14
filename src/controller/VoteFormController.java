package controller;

import dao.CandidatDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Candidat;
import model.Election;
import model.User;
import service.VoteService;
import utils.SceneUtil;

import java.io.IOException;
import java.util.List;

public class VoteFormController {

    @FXML
    private Label electionTitleLabel;

    @FXML
    private ListView<Candidat> candidatListView;

    @FXML
    private Button validerVoteButton;

    @FXML
    private Label statusLabel;


    @FXML
    private Button backButton;

    private Election currentElection;
    private User currentUser;
    private final CandidatDao candidatDao = new CandidatDao();
    private final VoteService voteService = new VoteService();

    public void setElectionData(Election election, User user) {
        this.currentElection = election;
        this.currentUser = user;
        electionTitleLabel.setText("Vote : " + election.getTitre());
        chargerCandidats();
    }

    private void chargerCandidats() {
        List<Candidat> candidats = candidatDao.getCandidatsParElection(currentElection.getId());
        candidatListView.setItems(FXCollections.observableArrayList(candidats));
    }

    @FXML
    private void handleValiderVote() {
        Candidat selectedCandidat = candidatListView.getSelectionModel().getSelectedItem();

        if (selectedCandidat == null) {
            statusLabel.setText("Veuillez sélectionner un candidat.");
            return;
        }

        boolean succes = voteService.voter(currentUser.getId(), currentElection, selectedCandidat.getId());

        if (succes) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vote Enregistré");
            alert.setHeaderText(null);
            alert.setContentText("Votre vote a été enregistré avec succès !");
            alert.showAndWait();
            handleRetour();
        } else {
            statusLabel.setText("Erreur : Impossible d'enregistrer le vote (déjà voté ou élection fermée).");
        }
    }

    @FXML
    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/voter_dashboard.fxml"));
            Stage stage = (Stage) validerVoteButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}