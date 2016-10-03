package voxspell.quiz.reportCard;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import voxspell.Main;

import java.util.ArrayList;

/**
 * Created by will on 3/10/16.
 */
public abstract class ReportCardController {

    static final int NUMWORDS = 1;

    @FXML
    protected Text wordComparisonsText, passedOrFailedLevelText, wordsSpeltCorrectlyText;
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
        setLevelText();
        generateStatistics();
        setWordsCorrectText();
        setWordComparisonsText();
        generatePieChart();
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
        wordsSpeltCorrectlyText.setText("You spelt " + mastered + " words correct on the first attempt.");
    }

    private void setWordComparisonsText() {
        String text = "";
        if (failed == 0){
            text += ("Nothing to correct!");
        } else {
            /* Only showing comparisons for failed words */
            for (int i = 0; i < NUMWORDS; i++){
                if (!words.get(i).equals(wordSecondAttempts.get(i))){
                    text += wordFirstAttempts.get(i) + " " + wordSecondAttempts.get(i) + " " + words.get(i) + "\n";
                }
            }
        }
        wordComparisonsText.setText(text);
        wordComparisonsText.setStrikethrough(true);
    }

    private void generatePieChart() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Words correct first attempt", mastered),
                        new PieChart.Data("Words correct second attempt", faulted),
                        new PieChart.Data("Words incorrect", failed));
        Platform.runLater(() -> { // need to get off AWT thread - why not for setting text ?
            pieChart.setData(pieChartData);
            pieChart.setTitle("Your statistics");
        });
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
