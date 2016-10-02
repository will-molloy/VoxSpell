package voxspell.quiz.reportCard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import voxspell.Main;
import voxspell.quiz.SpellingQuiz;

import java.util.ArrayList;

/**
 * Represents a report card once finishing a spelling quiz.
 *
 * @author Will Molloy
 */
public abstract class ReportCardFactory {

    protected static int level;
    protected ArrayList words;
    protected ArrayList wordFirstAttempts;
    protected ArrayList wordSecondAttempts;
    protected SpellingQuiz spellingQuiz;

    public final void setValues(SpellingQuiz spellingQuiz, ArrayList<String> words, ArrayList<String> wordFirstAttempts, ArrayList<String> wordSecondAttempts, int level) {
        this.spellingQuiz = spellingQuiz;
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordSecondAttempts = wordSecondAttempts;
        ReportCardFactory.level = level;
    }

    public abstract void showScene();

    @FXML
    private void handleRetryLevelBtn(ActionEvent actionEvent) {
        Main.newQuizLevel(level);
    }

    @FXML
    private void handleReturnToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

}
