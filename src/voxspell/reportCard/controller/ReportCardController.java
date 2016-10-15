package voxspell.reportCard.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the ReportCards shown at the end of a quiz.
 *
 * @author Will
 */
public abstract class ReportCardController {

    protected static List<String[]> incorrectWords;
    @FXML
    protected Button retryLevelBtn;
    @FXML
    protected WordList wordList;
    protected List<String> words;
    private int mastered;
    @FXML
    private Text accuracyTextView;
    private List<String> wordFirstAttempts;

    public final void setValues(List<String> words, List<String> wordFirstAttempts, WordList wordList) {
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordList = wordList;
    }

    public final void generateScene() {
        Platform.runLater(() -> {
            generateAccuracy();
            createGUI();
            retryLevelBtn.setText("Retry " + wordList.toString());
        });
    }

    private void generateAccuracy() {
        incorrectWords = new ArrayList<>();
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
