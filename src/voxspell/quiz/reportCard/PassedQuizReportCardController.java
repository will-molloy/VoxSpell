package voxspell.quiz.reportCard;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import voxspell.Main;

/**
 * Created by will on 3/10/16.
 */
public class PassedQuizReportCardController extends ReportCardController {

    @FXML
    protected Text wordComparisonsText, passedLevelText, wordsSpeltCorrectlyText;
    @FXML
    protected PieChart pieChart;

    private int mastered, faulted, failed;

    public void generateScene() {
        passedLevelText.setText("Passed level " + level + "!");

        for (int i = 0; i < 1; i++) {
            if (words.get(0).equals(wordFirstAttempts.get(0))) {
                mastered++;
            } else if (words.get(0).equals(wordSecondAttempts.get(0))) {
                faulted++;
            } else {
                failed++;
            }
        }

        wordsSpeltCorrectlyText.setText("You spelt " + mastered + " words correct on the first attempt.");

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
    private void handleNextLevelBtn(ActionEvent actionEvent) {
        Main.newQuizLevel(++level);
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
