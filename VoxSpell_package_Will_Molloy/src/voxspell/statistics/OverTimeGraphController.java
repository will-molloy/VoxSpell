package voxspell.statistics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
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

    /**
     * Initialise the scene, retrieve statistics, format them then create the plot with node labels.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /* Retrieve stats and create plot labels */
        List<String[]> prev12DaysStats = statisticsRetriever.getPrevDayStats(12);
        linePlot.getXAxis().setLabel("Date");
        linePlot.getYAxis().setLabel("Accuracy");

        linePlot.setTitle("Accuracy For The Previous " + prev12DaysStats.size() + " Days");

        XYChart.Series accuracySeries = new XYChart.Series();
        accuracySeries.setName("Accuracy");
        Collections.reverse(prev12DaysStats);

        /* Parse the statistics to determine upper and lower bounds */
        List<Integer> corrects = new ArrayList<>();
        List<Integer> incorrects = new ArrayList<>();
        List<Integer> totalWords = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<Double> accuracys = new ArrayList<>();

        for (String[] dayStats : prev12DaysStats) {
            int correct = Integer.parseInt(dayStats[0]);
            int incorrect = Integer.parseInt(dayStats[1]);

            corrects.add(correct);
            incorrects.add(incorrect);
            totalWords.add(correct + incorrect);

            dates.add(dayStats[2].substring(5));    // remove the 'yyyy' from the date

            // if no words spelt, 0% accuracy
            accuracys.add(correct > 0 ? (correct * 100.0) / (incorrect + correct) : 0);
        }

        /* Create plot and node labels */
        int dateCount = 0;

        // determine upper bound for placing label margin offset
        double maxTopThreshold = getMaxValue(accuracys) - 10;
        int minTopThreshold = 10;
        int minSideThreshold = 1;
        int maxSideThreshold = 10;
        for (String[] dayStats : prev12DaysStats) {
            XYChart.Data data = new XYChart.Data<>(dates.get(dateCount), accuracys.get(dateCount));

            // Determine if popup will be off the graph - if it is add in margins
            int topMargin = 0;
            if (accuracys.get(dateCount) <= minTopThreshold) {
                topMargin = -60;
            } else if (accuracys.get(dateCount) >= maxTopThreshold) {
                topMargin = 60;
            }

            int sideMargin = 0;
            if (dateCount <= minSideThreshold) {
                sideMargin = -135;
            } else if (dateCount >= maxSideThreshold) {
                sideMargin = 135;
            }

            // Create the hover label
            StackPane stackPane = new LineGraphNodeHoverPane(
                    "Worlds spelt: " + totalWords.get(dateCount) + "\nAccuracy: " + numberFormatter.formatAccuracy(accuracys.get(dateCount)),
                    topMargin,
                    sideMargin
            );
            data.setNode(stackPane);

            // add node to series
            accuracySeries.getData().add(data);
            dateCount++;
        }
        // add series to plot
        linePlot.setLegendVisible(false);
        linePlot.getData().add(accuracySeries);
    }

    /**
     * Returns the max value in a list of doubles.
     */
    private Double getMaxValue(List<Double> values) {
        if (values.size() > 0) {
            return Collections.max(values);
        } else {
            return (double) 0;
        }
    }

}
