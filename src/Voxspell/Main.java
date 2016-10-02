package Voxspell;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Launches the application, also controller for the Main Menu.
 *
 * @author Will Molloy
 */
public class Main extends Application {

    private static Stage window;
    private static Parent mainMenu, spellingQuiz;//, viewStats, settings;

    @Override
    public void start(Stage primaryStage) throws IOException {
        window = primaryStage;

        mainMenu = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
        spellingQuiz = FXMLLoader.load(getClass().getResource("Spelling_Quiz.fxml"));

        window.setTitle("VoxSpell");
        window.setScene(new Scene(mainMenu));
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    @FXML
    public void handleQuizButton(ActionEvent actionEvent) {
        window.setScene(new Scene(spellingQuiz));
    }

    @FXML
    public void handleStatisticsButton(ActionEvent actionEvent) {
    }

    @FXML
    public void handleSettingsButton(ActionEvent actionEvent) {
    }
}
