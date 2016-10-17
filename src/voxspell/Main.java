package voxspell;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import voxspell.quiz.SpellingQuizController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Launches the application, also controller for the Main Menu.
 *
 * @author Will Molloy
 */
public class Main extends Application implements Initializable{

    // Scenes that will be loaded once for better performance
    private static Scene mainMenu, spellingQuiz, wordListEditor;
    // Overall window
    private static Stage window;
    // Popup window
    private static Stage popup;
    // Spelling quiz controller to initialise spelling quiz
    private static SpellingQuizController spellingQuizControllerInstance;
    @FXML
    private Button quizBtn, statBtn, challengeBtn, editorBtn;

    private String backgroundImage = Main.class.getResource("media/images/light_background-wallpaper-960x540.jpg").toExternalForm();
    private Image
            quizIcon = new Image(getClass().getResourceAsStream("media/images/abc.png")),
            statIcon = new Image(getClass().getResourceAsStream("media/images/stats.png")),
            challengeIcon = new Image(getClass().getResourceAsStream("media/images/checked.png")),
            editorIcon = new Image(getClass().getResourceAsStream("media/images/agenda.png"));


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
        setBackgroundForRoot(mainMenuRoot);

        FXMLLoader spellingQuizLoader = new FXMLLoader(getClass().getResource("quiz/fxml/Spelling_Quiz.fxml"));
        Parent spellingQuizRoot = spellingQuizLoader.load();
        spellingQuizControllerInstance = spellingQuizLoader.getController();
        spellingQuiz = new Scene(spellingQuizRoot);

        Parent wordListRoot = FXMLLoader.load(getClass().getResource("wordlistEditor/fxml/Word_List_Editor.fxml"));
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
        if (category == null) {
            window.setScene(mainMenu);
        } else {
            spellingQuizControllerInstance.newQuiz(category);
        }
    }

    @FXML
    private void handleWordListEditorButton(ActionEvent actionEvent) {
        window.setScene(wordListEditor);
    }

    /**
     * Want to reload the statistics screen each time to update statistics within the program
     */
    @FXML
    private void handleStatisticsButton(ActionEvent actionEvent) {
        try {
            Parent statsRoot = FXMLLoader.load(getClass().getResource("statistics/fxml/Statistics_Skeleton.fxml"));
            Scene statistics = new Scene(statsRoot);
            window.setScene(statistics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Want to reload this scene each time to update challenge progress.
     */
    @FXML
    private void handleDailyChallengeBtn(ActionEvent actionEvent) {
        try {
            Parent challengesRoot = FXMLLoader.load(getClass().getResource("dailyChallenges/fxml/Daily_Challenges.fxml"));
            Scene dailyChallenges = new Scene(challengesRoot);
            window.setScene(dailyChallenges);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setBackgroundForRoot(Parent root){
        root.setStyle("-fx-background-backgroundImage: url('" + backgroundImage + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch;");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadBtnImages();
    }

    private void loadBtnImages() {
        loadImageForBtn(quizBtn,quizIcon);
        loadImageForBtn(statBtn,statIcon);
        loadImageForBtn(challengeBtn,challengeIcon);
        loadImageForBtn(editorBtn,editorIcon);
    }

    private void loadImageForBtn(Button button, Image image){
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        button.setGraphic(imageView);
    }
}
