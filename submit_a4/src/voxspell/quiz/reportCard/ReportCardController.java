package voxspell.reportCard;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the ReportCards shown at the end of a quiz.
 *
 * @author Will
 */
public abstract class ReportCardController {

    @FXML
    protected Button retryLevelBtn, proceedToLevelBtn;
    @FXML
    protected WordList wordList;
    @FXML
    private Text accuracyTextView;
    private List<String> words;
    private List<String> wordFirstAttempts;
    private int mastered;
    @FXML
    private Button viewMistakesBtn;
    private List<WordCorrection> corrections = new ArrayList<>();

    public final void setValues(List<String> words, List<String> wordFirstAttempts, WordList wordList) {
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordList = wordList;
    }

    public final void generateScene() {
        Platform.runLater(() -> {
            createGUI();
            generateAccuracy();
            proceedToLevelBtn.setText("Proceed to " + wordList.next().toString());
            retryLevelBtn.setText("Retry " + wordList.toString());
        });
    }

    private void generateAccuracy() {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals(wordFirstAttempts.get(i))) {
                mastered++;
            } else {
                corrections.add(new WordCorrection(words.get(i), wordFirstAttempts.get(i)));
            }
        }
        double accuracy = mastered * 100.0 / words.size();
        accuracyTextView.setText("Accuracy: " + accuracy + "%");
        if (accuracy == 100) {
            viewMistakesBtn.setDisable(true);
        }
    }

    public abstract void createGUI();

    @FXML
    final void handleRetryLevelBtn(ActionEvent actionEvent) {
        Main.newSpellingQuiz(wordList.toString());
    }

    @FXML
    final void handleReturnToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    final void handleNextLevelBtn(ActionEvent actionEvent) {
        Main.newSpellingQuiz(wordList.next().toString());
    }

    @FXML
    final void handleMistakesBtn(ActionEvent actionEvent) {
        if (mastered == words.size()) {
            showNothingToCorrectPopup();
        } else {
            showCorrectionDialog();
        }
    }

    private void showNothingToCorrectPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nothing To Correct");
        alert.setHeaderText(null);
        alert.setContentText("Nothing to correct!");
        alert.showAndWait();
    }

    private void showCorrectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent wordCorrectionsRoot = loader.load(Main.class.getResource("main_menu_fxml/Word_Corrections_Popup.main_menu_fxml").openStream());
            Scene scene = new Scene(wordCorrectionsRoot);
            WordCorrectionsPopup controller = loader.getController();
            controller.createTable(corrections);
            Main.showPopup(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    final void handleRandomCategoryBtn(ActionEvent actionEvent) {
        List<WordList> wordLists = new ArrayList<>(WordListEditorController.getWordLists());
        Collections.shuffle(wordLists);
        Main.newSpellingQuiz(wordLists.get(0).toString());
    }
}
