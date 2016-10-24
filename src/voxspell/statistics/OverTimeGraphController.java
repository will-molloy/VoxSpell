package voxspell.statistics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.*;

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

        List<String[]> prev12DaysStats = statisticsRetriever.getPrevDayStats(12);
        linePlot.getXAxis().setLabel("Date");
        linePlot.getYAxis().setLabel("Accuracy");

        linePlot.setTitle("Accuracy For The Previous " + prev12DaysStats.size() + " Days");

        XYChart.Series accuracySeries = new XYChart.Series();
        accuracySeries.setName("Accuracy");
        Collections.reverse(prev12DaysStats);

        List<Integer> correct = new ArrayList<>();
        List<Integer> incorrect = new ArrayList<>();
        List<Integer> totalWords = new ArrayList<>();
        List<String> date = new ArrayList<>();
        List<Double> accuracy = new ArrayList<>();
        for (String[] dayStats : prev12DaysStats) {
            correct.add(Integer.parseInt(dayStats[0]));
            incorrect.add(Integer.parseInt(dayStats[1]));
            totalWords.add(Integer.parseInt(dayStats[0]) + Integer.parseInt(dayStats[1]));
            date.add(dayStats[2].substring(5));
            // if no words spelt, 0% accuracy
            accuracy.add(Integer.parseInt(dayStats[0]) > 0 ? (Integer.parseInt(dayStats[0]) * 100.0) / (Integer.parseInt(dayStats[1]) + Integer.parseInt(dayStats[0])) : 0);
        }

        int dateCount = 0;
        int minTopThreshold = 10;
        double maxTopThreshold = getMaxValue(accuracy) -  10;
        int minSideThreshold = 1;
        int maxSideThreshold = 10;

        for (String[] dayStats : prev12DaysStats) {
            XYChart.Data data = new XYChart.Data<>(date.get(dateCount), accuracy.get(dateCount));


            // Determine if popup will be off the graph - if it is add in margins
            int topMargin = 0;
            if(accuracy.get(dateCount) <= minTopThreshold) {
                topMargin = -60;
            } else if (accuracy.get(dateCount) >= maxTopThreshold) {
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
                    "Worlds spelt: " + totalWords.get(dateCount) + "\nAccuracy: " + numberFormatter.formatAccuracy(accuracy.get(dateCount)),
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

    private Double getMaxValue(List<Double> values) {
        if (values.size() > 0) {
            return Collections.max(values);
        } else {
            return (double)0;
        }
    }

}
