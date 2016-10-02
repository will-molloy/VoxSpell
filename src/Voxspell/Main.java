package Voxspell;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launches the application, also controller for the Main Menu.
 *
 * @author Will Molloy
 */
public class Main extends Application {

    // Overall window
    private static Stage window;
    // Scenes accessed by the main menu
    private static Scene mainMenu, spellingQuiz;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Shows the Main Menu - used by back buttons throughout the application.
     */
    public static void showMainMenu() {
        window.setScene(mainMenu);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        window = primaryStage;

        Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
        mainMenu = new Scene(mainMenuRoot);

        Parent spellingQuizRoot = FXMLLoader.load(getClass().getResource("Spelling_Quiz.fxml"));
        spellingQuiz = new Scene(spellingQuizRoot);

        window.setTitle("VoxSpell");
        window.setScene(mainMenu);
        window.show();
    }

    @FXML
    private void handleQuizButton(ActionEvent actionEvent) {
        window.setScene(spellingQuiz);
    }

    @FXML
    private void handleStatisticsButton(ActionEvent actionEvent) {
    }

    @FXML
    private void handleSettingsButton(ActionEvent actionEvent) {
    }
}
