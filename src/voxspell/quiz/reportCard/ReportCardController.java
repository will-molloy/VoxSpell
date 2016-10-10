package voxspell.quiz.reportCard;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.wordlistEditor.WordList;

import java.util.List;

/**
 * Created by will on 3/10/16.
 */
public abstract class ReportCardController {

    private static int NUM_WORDS;
    @FXML
    protected Text passedOrFailedLevelText, wordsSpeltCorrectlyText;
    @FXML
    protected Text wordComparisonView;
    @FXML
    protected Button retryLevelBtn, proceedToLevelBtn;
    @FXML
    protected PieChart pieChart;
    protected WordList wordList;

    private List words;
    private List wordFirstAttempts;
    private List wordSecondAttempts;
    private int mastered, faulted, failed;

    public final void setValues(List<String> words, List<String> wordFirstAttempts, List<String> wordSecondAttempts, WordList wordList, int quizSize) {
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordSecondAttempts = wordSecondAttempts;
        this.wordList = wordList;
        NUM_WORDS = quizSize;
    }

    public final void generateScene() {
        Platform.runLater(() -> {
            setLevelText();
            generateStatistics();
            setWordsCorrectText();
            setWordComparisonsText();
            generatePieChart();
            retryLevelBtn.setText("Retry " + wordList.toString());
        });
    }

    protected abstract void setLevelText();

    private void generateStatistics() {
        for (int i = 0; i < NUM_WORDS; i++) {
            if (words.get(i).equals(wordFirstAttempts.get(i))) {
                mastered++;
            } else if (words.get(i).equals(wordSecondAttempts.get(i))) {
                faulted++;
            } else {
                failed++;
            }
        }
    }

    private void setWordsCorrectText() {
        wordsSpeltCorrectlyText.setText("You mastered " + mastered + " words!");
    }

    private void setWordComparisonsText() {
        String text = "";
        StringBuilder stringBuilder = new StringBuilder(text);
        if (failed == 0) {
            stringBuilder.append("Nothing to correct!");
        } else {
            /* Only showing comparisons for failed words */
            for (int i = 0; i < NUM_WORDS; i++) {
                if (!words.get(i).equals(wordSecondAttempts.get(i))) {
                    stringBuilder.append("<strike>").append(wordFirstAttempts.get(i)).append("</strike> ");
                    stringBuilder.append("<strike>").append(wordSecondAttempts.get(i)).append("</strike> ");
                    stringBuilder.append(words.get(i)).append("\n");
                }
            }
        }
        text = stringBuilder.toString();
        wordComparisonView.setText(text);
    }

    private void generatePieChart() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Words Correct First Attempt", mastered),
                        new PieChart.Data("Words Correct Second Attempt", faulted),
                        new PieChart.Data("Words Incorrect", failed));
        pieChart.setData(pieChartData);
    }

    @FXML
    private void handleRetryLevelBtn(ActionEvent actionEvent) {
        Main.newSpellingQuiz(wordList.toString());
    }

    @FXML
    private void handleReturnToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }
}
