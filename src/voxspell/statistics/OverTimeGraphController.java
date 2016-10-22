package voxspell.statistics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the line graph of over time statistics.
 */
public class OverTimeGraphController implements Initializable {

    @FXML
    private LineChart linePlot;

    private StatisticsFileHandler statisticsFileHandler = new StatisticsFileHandler();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String[]> prev12DaysStats = statisticsFileHandler.getPrevDayStats(12);
        linePlot.getXAxis().setLabel("Date");
        linePlot.getYAxis().setLabel("Accuracy");
        linePlot.setTitle("Accuracy For The Previous " + prev12DaysStats.size() + " Days");

        XYChart.Series accuracySeries = new XYChart.Series();
        accuracySeries.setName("Accuracy");
        Collections.reverse(prev12DaysStats);

        for (String[] dayStats : prev12DaysStats) {
            int correct = Integer.parseInt(dayStats[0]);
            int incorrect = Integer.parseInt(dayStats[1]);
            String date = dayStats[2].substring(5);

            double accuracy = (correct * 100.0) / (incorrect + correct);
            accuracySeries.getData().add(new XYChart.Data(date, accuracy));
        }
        linePlot.getData().add(accuracySeries);

    }
}
