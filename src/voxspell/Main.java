package voxspell;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import voxspell.quiz.SpellingQuizController;

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
    private static SpellingQuizController spellingQuizControllerInstance;

    /**
     * Shows the Main Menu - used by back buttons throughout the application.
     */
    public static void showMainMenu() {
        window.setScene(mainMenu);
    }

    /**
     * The application wil show the given scene
     */
    public static void setAndShowScene(Scene scene) {
        Platform.runLater(() -> window.setScene(scene));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void newQuizLevel(int level) {
        spellingQuizControllerInstance.newQuiz(level);
        window.setScene(spellingQuiz);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        window = primaryStage;

        Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
        mainMenu = new Scene(mainMenuRoot);

        FXMLLoader spellingQuizLoader = new FXMLLoader(this.getClass().getResource("quiz/Spelling_Quiz.fxml"));
        Parent spellingQuizRoot = spellingQuizLoader.load();
        spellingQuizControllerInstance = spellingQuizLoader.getController();

        spellingQuiz = new Scene(spellingQuizRoot);

        window.setTitle("VoxSpell");
        window.setScene(mainMenu);
        window.show();
    }

    @FXML
    private void handleQuizButton(ActionEvent actionEvent) {
        window.setScene(spellingQuiz);
        int level = spellingQuizControllerInstance.promptUserForInitialLevel();
        spellingQuizControllerInstance.newQuiz(level);
    }

    @FXML
    private void handleStatisticsButton(ActionEvent actionEvent) {
    }

    @FXML
    private void handleSettingsButton(ActionEvent actionEvent) {
    }
}
