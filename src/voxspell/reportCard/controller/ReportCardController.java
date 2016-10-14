package voxspell.reportCard.controller;

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
import voxspell.wordlistEditor.Word;
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
    protected Button retryLevelBtn;
    @FXML
    protected WordList wordList;
    @FXML
    private Text accuracyTextView;
    protected List<String> words;
    private List<String> wordFirstAttempts;
    int mastered;
    private List<String[]> incorrectWords = new ArrayList<>();

    public final void setValues(List<String> words, List<String> wordFirstAttempts, WordList wordList) {
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordList = wordList;
    }

    public final void generateScene() {
        Platform.runLater(() -> {
            createGUI();
            generateAccuracy();
            retryLevelBtn.setText("Retry " + wordList.toString());
        });
    }

    private void generateAccuracy() {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals(wordFirstAttempts.get(i))) {
                mastered++;
            } else {
                incorrectWords.add(new String[]{words.get(i), wordFirstAttempts.get(i)});
            }
        }
        double accuracy = mastered * 100.0 / words.size();
        accuracyTextView.setText("Accuracy: " + accuracy + "%");
    }

    protected abstract void createGUI();

    @FXML
    final void handleRetryLevelBtn(ActionEvent actionEvent) {
        Main.newSpellingQuiz(wordList.toString());
    }

    @FXML
    final void handleReturnToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    final void handleRandomCategoryBtn(ActionEvent actionEvent) {
        List<WordList> wordLists = new ArrayList<>(WordListEditorController.getWordLists());
        Collections.shuffle(wordLists);
        Main.newSpellingQuiz(wordLists.get(0).toString());
    }
}
