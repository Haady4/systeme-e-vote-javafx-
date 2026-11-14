package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import service.AuthService;
import utils.SceneUtil;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        User user = authService.connecter(login, password);

        if (user != null) {
            errorMessageLabel.setText("");
            redirigerSelonRole(user);
        } else {
            errorMessageLabel.setText("Login ou mot de passe incorrect.");
        }
    }

    private void redirigerSelonRole(User user) {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        String fxmlFile = "";

        if (user.getRole() == User.Role.ADMIN) {
            fxmlFile = "/view/admin_dashboard.fxml";
        } else {
            fxmlFile = "/view/voter_dashboard.fxml";
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            SceneUtil.changerScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Erreur lors du chargement de la page.");
        }
    }
}