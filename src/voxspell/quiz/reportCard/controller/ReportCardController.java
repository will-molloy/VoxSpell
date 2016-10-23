package voxspell.quiz.reportCard.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.dailyChallenges.ChallengeType;
import voxspell.dailyChallenges.DailyChallengeGUIController;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the ReportCards shown at the end of a quiz.
 *
 * @author Will Molloy
 */
public abstract class ReportCardController {

    protected static List<String[]> incorrectWords;

    @FXML
    protected Text elapsedTimeTxt, bestStreakTxt;

    @FXML
    protected WordList wordList;
    protected List<String> words;
    private long elapsedTimeSeconds;
    private int bestStreak;
    private int mastered;
    @FXML
    private Text accuracyTextView;
    private List<String> wordFirstAttempts;

    public final void setValues(List<String> words, List<String> wordFirstAttempts, WordList wordList, long elapsedTime, int bestStreak) {
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordList = wordList;
        this.elapsedTimeSeconds = elapsedTime;
        this.bestStreak = bestStreak;
    }

    public final void generateScene() {
        Platform.runLater(() -> {
            generateAccuracy();
            showElapsedTime();
            showBestStreak();
            createGUI();
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
        if (accuracy == 100) {
            DailyChallengeGUIController d = new DailyChallengeGUIController();
            d.updateChallenge(ChallengeType.QUIZ_ACCURACY, 100);
        }
        accuracyTextView.setText("Accuracy: " + new DecimalFormat("####0.00").format(accuracy) + "%");
    }

    private void showElapsedTime() {
        int mins = (int) (elapsedTimeSeconds / 60);
        int seconds = (int) (elapsedTimeSeconds % 60);
        String formatMins;
        if (mins == 0) {
            formatMins = "00";
        } else if (mins < 10) {
            formatMins = "0" + mins;
        } else {
            formatMins = mins + "";
        }

        String formatSecs = seconds < 10 ? "0" + seconds : seconds + "";
        elapsedTimeTxt.setText("Elapsed Time: " + formatMins + ":" + formatSecs);
    }

    private void showBestStreak() {
        bestStreakTxt.setText("Best Streak: " + bestStreak);
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
