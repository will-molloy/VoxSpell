package voxspell;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import voxspell.quiz.SpellingQuizController;

import java.io.File;
import java.io.IOException;

/**
 * Launches the application, also controller for the Main Menu.
 *
 * @author Will Molloy
 */
public class Main extends Application {

    // Scenes accessed by the main menu
    public static Scene mainMenu, spellingQuiz, wordListEditor;
    // Overall window
    private static Stage window;
    // Popup window
    private static Stage popup;
    private static SpellingQuizController spellingQuizControllerInstance;

    public static void main(String[] args) {
        launch(args);
    }

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

    /**
     * Shows the given scene as a popup.
     * Prevents the user from accessing the primary stage until closing the popup.
     */
    public static void showPopup(Scene scene) {
        Platform.runLater(() -> {
            popup.setScene(scene);
            popup.show();
        });
    }

    public static void hidePopup() {
        popup.hide();
    }

    public static File showFileChooserAndReturnChosenFile(FileChooser fileChooser) {
        return fileChooser.showOpenDialog(window);
    }

    public static void newSpellingQuiz(String quizName) {
        spellingQuizControllerInstance.newQuiz(quizName);
        window.setScene(spellingQuiz);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        window = primaryStage;

        Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("fxml/Main_Menu.fxml"));
        mainMenu = new Scene(mainMenuRoot);

        FXMLLoader spellingQuizLoader = new FXMLLoader(this.getClass().getResource("fxml/Spelling_Quiz.fxml"));
        Parent spellingQuizRoot = spellingQuizLoader.load();
        spellingQuizControllerInstance = spellingQuizLoader.getController();
        spellingQuiz = new Scene(spellingQuizRoot);

        Parent wordListRoot = FXMLLoader.load(getClass().getResource("fxml/Word_List_Editor.fxml"));
        wordListEditor = new Scene(wordListRoot);

        window.setTitle("VoxSpell");
        window.setScene(mainMenu);
        window.setResizable(false);
        window.show();

        // Popups to be used later
        popup = new Stage();
        popup.setResizable(false);
        hidePopup();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(window.getScene().getWindow());
    }

    @FXML
    private void handleQuizButton(ActionEvent actionEvent) {
        window.setScene(spellingQuiz);
        String category = spellingQuizControllerInstance.promptUserForInitialLevel();
        spellingQuizControllerInstance.newQuiz(category);
    }

    @FXML
    private void handleStatisticsButton(ActionEvent actionEvent) {
    }

    @FXML
    private void handleWordListEditorButton(ActionEvent actionEvent) {
        window.setScene(wordListEditor);
    }


}
