package voxspell.statistics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the line graph of over time statistics.
 *
 * @author Will Molloy
 */
public class OverTimeGraphController extends StatisticsController implements Initializable {

    @FXML
    private LineChart linePlot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int minTopThreshold = 10;
        int maxTopThreshold = 90;
        int minSideThreshold = 1;
        int maxSideThreshold = 10;

        List<String[]> prev12DaysStats = statisticsRetriever.getPrevDayStats(12);
        linePlot.getXAxis().setLabel("Date");
        linePlot.getYAxis().setLabel("Accuracy");
        linePlot.setTitle("Accuracy For The Previous " + prev12DaysStats.size() + " Days");

        XYChart.Series accuracySeries = new XYChart.Series();
        accuracySeries.setName("Accuracy");
        Collections.reverse(prev12DaysStats);

        int dateCount = 0;
        for (String[] dayStats : prev12DaysStats) {
            int correct = Integer.parseInt(dayStats[0]);
            int incorrect = Integer.parseInt(dayStats[1]);
            int totalWords = correct + incorrect;
            String date = dayStats[2].substring(5);
            double accuracy = totalWords > 0 ? (correct * 100.0) / (incorrect + correct) : 0; // if no words spelt, 0% accuracy
            XYChart.Data data = new XYChart.Data<>(date, accuracy);

            // Determine if popup will be off the graph - if it is add in margins
            int topMargin = 0;
            if(accuracy <= minTopThreshold) {
                topMargin = -60;
            } else if (accuracy >= maxTopThreshold) {
                topMargin = 60;
            }

            int sideMargin = 0;
            if (dateCount <= minSideThreshold){
                sideMargin = -135;
            } else if (dateCount >= maxSideThreshold){
                sideMargin = 135;
            }

            // Create the hover label
            StackPane stackPane = new LineGraphNodeHoverPane(
                    "Worlds spelt: " + totalWords + "\nAccuracy: " + numberFormatter.formatAccuracy(accuracy),
                    topMargin,
                    sideMargin
            );
            data.setNode(stackPane);

            accuracySeries.getData().add(data);
            dateCount++;
        }
        linePlot.setLegendVisible(false);
        linePlot.getData().add(accuracySeries);

    }
}
