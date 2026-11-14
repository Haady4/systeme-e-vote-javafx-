package utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Centralise le changement de scène pour éviter que la fenêtre garde
 * l'ancienne taille et coupe le bas des écrans qui ont plus de contenu
 * (ex: gestion des utilisateurs). JavaFX ne redimensionne pas
 * automatiquement le Stage quand on lui donne une nouvelle Scene après
 * le premier show().
 */
public class SceneUtil {

    private SceneUtil() {}

    /** Change de scène et adapte automatiquement la taille de la fenêtre au contenu. */
    public static void changerScene(Stage stage, Parent root) {
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.sizeToScene();

        // Si le contenu est plus grand que l'écran disponible (petit écran,
        // écran secondaire...), on limite la fenêtre à l'écran visible.
        // Le ScrollPane des écrans concernés permet alors de voir le reste.
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        if (stage.getWidth() > visualBounds.getWidth()) {
            stage.setWidth(visualBounds.getWidth());
        }
        if (stage.getHeight() > visualBounds.getHeight()) {
            stage.setHeight(visualBounds.getHeight());
        }

        stage.centerOnScreen();
        stage.show();
    }

    /** Change de scène avec une taille fixe (ex: écran de connexion). */
    public static void changerScene(Stage stage, Parent root, double largeur, double hauteur) {
        stage.setResizable(false);
        stage.setScene(new Scene(root, largeur, hauteur));
        stage.centerOnScreen();
        stage.show();
    }
}
