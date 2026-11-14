package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Candidat;
import model.Election;
import model.User;
import service.AuthService;
import service.ElectionService;
import utils.CsvExporter;
import utils.PdfExporter;
import utils.SceneUtil;

import java.io.IOException;
import java.util.Map;

public class ResultsController {

    @FXML
    private Label electionTitleLabel;

    @FXML
    private BarChart<String, Number> resultsChart;

    @FXML
    private PieChart resultsPieChart;

    @FXML
    private Label winnerLabel;

    @FXML
    private Label electeursLabel;

    @FXML
    private Label votesLabel;

    @FXML
    private Label participationLabel;

    @FXML
    private Button backButton;

    private final ElectionService electionService = new ElectionService();
    private Election currentElection;

    public void setElectionData(Election election) {
        this.currentElection = election;
        electionTitleLabel.setText("Résultats : " + election.getTitre());
        chargerResultatsGraphique();
        chargerStatistiques();
    }

    private void chargerResultatsGraphique() {
        Map<Candidat, Integer> me = electionService.depouillerResultats(currentElection.getId());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de Voix");

        javafx.collections.ObservableList<PieChart.Data> partsCamembert =
                javafx.collections.FXCollections.observableArrayList();

        Candidat gagneur = null;
        int maxVoix = -1;

        for (Map.Entry<Candidat, Integer> entry : me.entrySet()) {
            Candidat c = entry.getKey();
            Integer voix = entry.getValue();

            series.getData().add(new XYChart.Data<>(c.getPrenom() + " " + c.getNom(), voix));
            partsCamembert.add(new PieChart.Data(c.getPrenom() + " " + c.getNom() + " (" + voix + ")", voix));

            if (voix > maxVoix) {
                maxVoix = voix;
                gagneur = c;
            }
        }

        resultsChart.getData().clear();
        resultsChart.getData().add(series);

        resultsPieChart.setData(partsCamembert);

        if (gagneur != null && maxVoix > 0) {
            winnerLabel.setText("Vainqueur temporaire/définitif : " + gagneur.getPrenom() + " " + gagneur.getNom() + " (" + maxVoix + " voix)");
        } else {
            winnerLabel.setText("Aucun vote enregistré pour le moment.");
        }
    }

    private void chargerStatistiques() {
        int electeurs = electionService.compterElecteursEligibles(currentElection);
        int votes = electionService.compterVotes(currentElection.getId());
        double taux = electeurs > 0 ? (votes * 100.0 / electeurs) : 0.0;

        electeursLabel.setText("Électeurs éligibles : " + electeurs);
        votesLabel.setText("Votes exprimés : " + votes);
        participationLabel.setText(String.format("Taux de participation : %.1f%%", taux));
    }

    @FXML
    private void handleExportCsv() {
        Map<Candidat, Integer> me = electionService.depouillerResultats(currentElection.getId());
        CsvExporter.exporterResultats(currentElection, me);
    }

    @FXML
    private void handleExportPdf() {
        Map<Candidat, Integer> me = electionService.depouillerResultats(currentElection.getId());
        int electeurs = electionService.compterElecteursEligibles(currentElection);
        PdfExporter.genererProcesVerbal(currentElection, me, electeurs);
    }

    @FXML
    private void handleRetour() {
        try {
            String fxmlFile;
            User currentUser = AuthService.getUtilisateurConnecte();

            if (currentUser != null && currentUser.getRole() == User.Role.ADMIN) {
                fxmlFile = "/view/admin_dashboard.fxml";
            } else {
                fxmlFile = "/view/voter_dashboard.fxml";
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}