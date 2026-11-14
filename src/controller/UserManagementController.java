package controller;

import dao.UserDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.User.Role;
import utils.PasswordUtil;
import utils.SceneUtil;

import java.io.IOException;
import java.util.List;

public class UserManagementController {

    @FXML
    private TextField rechercheField;

    @FXML
    private ListView<User> userListView;

    @FXML
    private TextField matriculeField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<Role> roleCombo;

    @FXML
    private TextField ufrField;

    @FXML
    private TextField departementField;

    @FXML
    private TextField niveauField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button nouveauButton;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button rechercherButton;

    @FXML
    private Button reinitialiserButton;

    @FXML
    private Button backButton;

    private final UserDao userDao = new UserDao();
    private User utilisateurSelectionne;

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        roleCombo.getSelectionModel().select(Role.ETUDIANT);

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) {
                remplirFormulaire(nouveau);
            }
        });

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        List<User> liste = userDao.tousLesUtilisateurs();
        userListView.setItems(FXCollections.observableArrayList(liste));
    }

    private void remplirFormulaire(User u) {
        utilisateurSelectionne = u;
        matriculeField.setText(u.getMatricule());
        nomField.setText(u.getNom());
        prenomField.setText(u.getPrenom());
        emailField.setText(u.getEmail());
        loginField.setText(u.getLogin());
        passwordField.clear();
        roleCombo.setValue(u.getRole());
        ufrField.setText(u.getUfr());
        departementField.setText(u.getDepartement());
        niveauField.setText(u.getNiveauFiliere());
        statusLabel.setText("");
    }

    @FXML
    private void handleNouveau() {
        utilisateurSelectionne = null;
        userListView.getSelectionModel().clearSelection();
        matriculeField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        loginField.clear();
        passwordField.clear();
        roleCombo.getSelectionModel().select(Role.ETUDIANT);
        ufrField.clear();
        departementField.clear();
        niveauField.clear();
        statusLabel.setText("");
    }

    @FXML
    private void handleRechercher() {
        String terme = rechercheField.getText() == null ? "" : rechercheField.getText().trim();
        if (terme.isEmpty()) {
            chargerUtilisateurs();
            return;
        }
        List<User> resultats = userDao.rechercher(terme);
        userListView.setItems(FXCollections.observableArrayList(resultats));
    }

    @FXML
    private void handleReinitialiser() {
        rechercheField.clear();
        chargerUtilisateurs();
    }

    @FXML
    private void handleAjouter() {
        if (!champsObligatoiresRemplis(true)) {
            return;
        }

        User user = new User();
        user.setMatricule(matriculeField.getText().trim());
        user.setNom(nomField.getText().trim());
        user.setPrenom(prenomField.getText().trim());
        user.setEmail(videSiVide(emailField.getText()));
        user.setLogin(loginField.getText().trim());
        user.setPassword(PasswordUtil.hashPassword(passwordField.getText()));
        user.setRole(roleCombo.getValue());
        user.setUfr(videSiVide(ufrField.getText()));
        user.setDepartement(videSiVide(departementField.getText()));
        user.setNiveauFiliere(videSiVide(niveauField.getText()));

        boolean succes = userDao.ajouter(user);

        if (succes) {
            handleNouveau();
            chargerUtilisateurs();
        } else {
            statusLabel.setText("Erreur lors de l'ajout (login ou matricule déjà utilisé ?).");
        }
    }

    @FXML
    private void handleModifier() {
        if (utilisateurSelectionne == null) {
            statusLabel.setText("Sélectionnez d'abord un utilisateur dans la liste.");
            return;
        }
        if (!champsObligatoiresRemplis(false)) {
            return;
        }

        User user = new User();
        user.setId(utilisateurSelectionne.getId());
        user.setMatricule(matriculeField.getText().trim());
        user.setNom(nomField.getText().trim());
        user.setPrenom(prenomField.getText().trim());
        user.setEmail(videSiVide(emailField.getText()));
        user.setLogin(loginField.getText().trim());

        String motDePasseSaisi = passwordField.getText();
        if (motDePasseSaisi == null || motDePasseSaisi.isEmpty()) {
            user.setPassword(utilisateurSelectionne.getPassword());
        } else {
            user.setPassword(PasswordUtil.hashPassword(motDePasseSaisi));
        }

        user.setRole(roleCombo.getValue());
        user.setUfr(videSiVide(ufrField.getText()));
        user.setDepartement(videSiVide(departementField.getText()));
        user.setNiveauFiliere(videSiVide(niveauField.getText()));

        boolean succes = userDao.modifier(user);

        if (succes) {
            handleNouveau();
            chargerUtilisateurs();
        } else {
            statusLabel.setText("Erreur lors de la modification.");
        }
    }

    @FXML
    private void handleSupprimer() {
        if (utilisateurSelectionne == null) {
            statusLabel.setText("Sélectionnez d'abord un utilisateur dans la liste.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer l'utilisateur " + utilisateurSelectionne + " ?");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse.getButtonData().isDefaultButton()) {
                userDao.supprimer(utilisateurSelectionne.getId());
                handleNouveau();
                chargerUtilisateurs();
            }
        });
    }

    private boolean champsObligatoiresRemplis(boolean creation) {
        String matricule = matriculeField.getText() == null ? "" : matriculeField.getText().trim();
        String nom = nomField.getText() == null ? "" : nomField.getText().trim();
        String prenom = prenomField.getText() == null ? "" : prenomField.getText().trim();
        String login = loginField.getText() == null ? "" : loginField.getText().trim();

        if (matricule.isEmpty() || nom.isEmpty() || prenom.isEmpty() || login.isEmpty()) {
            statusLabel.setText("Matricule, nom, prénom et login sont obligatoires.");
            return false;
        }

        if (creation && (passwordField.getText() == null || passwordField.getText().isEmpty())) {
            statusLabel.setText("Le mot de passe est obligatoire pour créer un utilisateur.");
            return false;
        }

        if (roleCombo.getValue() == null) {
            statusLabel.setText("Veuillez choisir un rôle.");
            return false;
        }

        return true;
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
