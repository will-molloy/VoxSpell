package voxspell.statistics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the line graph of over time statistics.
 *
 * @author Will Molloy
 */
public class OverTimeGraphController extends StatisticsController implements Initializable  {

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

        for (String[] dayStats : prev12DaysStats) {
            int correct = Integer.parseInt(dayStats[0]);
            int incorrect = Integer.parseInt(dayStats[1]);
            int totalWords = correct+incorrect;
            String date = dayStats[2].substring(5);
            double accuracy = totalWords > 0 ? (correct * 100.0) / (incorrect + correct) : 0; // if no words spelt, 0% accuracy
            XYChart.Data data = new XYChart.Data<>(date, accuracy);
            data.setNode(new LineGraphNodeHoverPane("Worlds spelt: " + totalWords +"\nAccuracy: " + formatAccuracy(accuracy)));
            accuracySeries.getData().add(data);
        }
        linePlot.getData().add(accuracySeries);

    }


}
