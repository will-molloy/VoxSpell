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

import java.util.ArrayList;

/**
 * Created by will on 3/10/16.
 */
public abstract class ReportCardController {

    private static final int NUMWORDS = 1;

    @FXML
    protected Text passedOrFailedLevelText, wordsSpeltCorrectlyText;

    @FXML
    protected Text wordComparisonView;

    @FXML
    protected Button retryLevelBtn, proceedToLevelBtn;

    @FXML
    protected PieChart pieChart;

    int level;
    private ArrayList words;
    private ArrayList wordFirstAttempts;
    private ArrayList wordSecondAttempts;
    private int mastered, faulted, failed;

    public final void setValues(ArrayList<String> words, ArrayList<String> wordFirstAttempts, ArrayList<String> wordSecondAttempts, int level) {
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordSecondAttempts = wordSecondAttempts;
        this.level = level;
    }

    public final void generateScene() {
        Platform.runLater(() -> {
            setLevelText();
            generateStatistics();
            setWordsCorrectText();
            setWordComparisonsText();
            generatePieChart();
            retryLevelBtn.setText("Retry Level " + level);
        });
    }

    protected abstract void setLevelText();

    private void generateStatistics() {
        for (int i = 0; i < NUMWORDS; i++) {
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
            for (int i = 0; i < NUMWORDS; i++) {
                if (!words.get(i).equals(wordSecondAttempts.get(i))) {
                    stringBuilder.append("<strike>").append(wordFirstAttempts.get(i)).append("</strike> ");
                    stringBuilder.append("<strike>").append(wordSecondAttempts.get(i)).append("</strike> ");
                    stringBuilder.append(words.get(i)).append("\n");
                }
            }
        }
        text = stringBuilder.toString();
        //     WebEngine webEngine = wordComparisonView.getEngine();
        //   webEngine.loadContent(text);
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
        Main.newQuizLevel(level);
    }

    @FXML
    private void handleReturnToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }
}
