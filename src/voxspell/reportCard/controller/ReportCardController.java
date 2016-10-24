package voxspell.reportCard.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.dailyChallenges.ChallengeType;
import voxspell.dailyChallenges.DailyChallengeGUIController;
import voxspell.tools.ImageLoader;
import voxspell.tools.NumberFormatter;
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

    private static final Paint
            GREEN = Paint.valueOf("#9fe89f"),
            ORANGE = Paint.valueOf("#ffd569"),
            RED = Paint.valueOf("#F94923");
    protected static List<String[]> incorrectWords;
    @FXML
    protected Button mainMenuBtn, randomBtn;
    @FXML
    protected Text elapsedTimeTxt, bestStreakTxt;
    private NumberFormatter numberFormatter = new NumberFormatter();

    @FXML
    protected WordList wordList;
    protected List<String> words;
    @FXML
    protected Text accuracyTextView;
    protected ImageLoader imageLoader = new ImageLoader();
    protected Image
            backIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/logout_icon.png")),
            randomIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/random_icon.png")),
            nextIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/next_level_icon.png")),
            videoIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/video_icon.png"));
    private long elapsedTimeSeconds;
    private int bestStreak;
    private int mastered;
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
            createBaseGUI();
            createSubClassGUI();
        });
    }

    private final void createBaseGUI() {
        imageLoader.loadSquareImageForBtn(mainMenuBtn, backIcon, 40);
        imageLoader.loadSquareImageForBtn(randomBtn, randomIcon, 40);
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
        accuracyTextView.setText("Accuracy: " + numberFormatter.formatAccuracy(accuracy));

        Paint color;
        if (accuracy >= 90) {
            color = GREEN;
        } else if (accuracy >= 50) {
            color = ORANGE;
        } else {
            color = RED;
        }
        setTextColour(accuracyTextView, color);
    }

    private void setTextColour(Text textView, Paint colour) {
        textView.setFill(colour);
    }

    private void showElapsedTime() {
        elapsedTimeTxt.setText("Elapsed Time: " + numberFormatter.formatTime((int)elapsedTimeSeconds));

        Paint color;
        if (elapsedTimeSeconds <= words.size() * 5) {          // 5 seconds per word = green
            color = GREEN;
        } else if (elapsedTimeSeconds <= words.size() * 8) {   // 8 seconds per word = orange
            color = ORANGE;
        } else {
            color = RED;
        }
        setTextColour(elapsedTimeTxt, color);
    }

    private void showBestStreak() {
        bestStreakTxt.setText("Best Streak: " + bestStreak);
        Paint color;
        if (bestStreak >= words.size() * 0.8) {          // 80% streak = green
            color = GREEN;
        } else if (bestStreak >= words.size() * 0.6) {   // 60% streak = orange
            color = ORANGE;
        } else {
            color = RED;
        }
        setTextColour(bestStreakTxt, color);
    }

    protected abstract void createSubClassGUI();

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
