package voxspell.statistics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Generates the category stats shown in the statistics scene.
 *
 * @author Will Molloy
 */
public class CategoryStatisticsController extends StatisticsController implements Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private ScrollPane scrollPane;
    // Object to read stats file
    // Object to get information on wordlists/categories
    private List<WordList> wordlists;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wordlists = WordListEditorController.getWordLists(); // ONLY gets wordlists that exist - not everything from stats
        statisticsRetriever = new StatisticsRetriever();

        generateAndShowStatsInScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }

    private void generateAndShowStatsInScrollPane() {
        ObservableList<CategoryStat> data = FXCollections.observableArrayList();
        for (WordList wl : wordlists) {
            String category = wl.toString();
            int[] extractedStat = statisticsRetriever.getStatsForCategory(category);
            int totalSpelt = extractedStat[0] + extractedStat[1];
            double accuracy = extractedStat[0] * 100.0 / totalSpelt;
            String bestStreak = extractedStat[2] + "";
            int bestTime = extractedStat[3];
            String formatBestTime;
            if (bestTime == Integer.MAX_VALUE){
                formatBestTime = "DNF";
            } else {
                formatBestTime = numberFormatter.formatTime(extractedStat[3]);
            }

            // Only adding in categories that the user has attempted.
            if (totalSpelt > 0) {
                CategoryStat stat = new CategoryStat(category, totalSpelt + "", numberFormatter.formatAccuracy(accuracy), bestStreak, formatBestTime);
                data.add(stat);
            }
        }

        TableColumn<CategoryStat, String> categoryCol = new TableColumn<>("Category");
        formatTableColumn(categoryCol, "category");

        TableColumn<CategoryStat, String> totalSpeltCol = new TableColumn<>("Words Spelt");
        formatTableColumn(totalSpeltCol, "totalSpelt");

        TableColumn<CategoryStat, String> accuracyCol = new TableColumn<>("Accuracy");
        formatTableColumn(accuracyCol, "accuracy");

        TableColumn<CategoryStat, String> streakCol = new TableColumn<>("Best Streak");
        formatTableColumn(streakCol, "bestStreak");

        TableColumn<CategoryStat, String> timeCol = new TableColumn<>("Best Time");
        formatTableColumn(timeCol, "bestTime");

        Collections.sort(data);

        tableView.setItems(data);
        tableView.getColumns().addAll(categoryCol, totalSpeltCol, accuracyCol, streakCol, timeCol);
    }

    private void formatTableColumn(TableColumn<CategoryStat, String> column, String propertyValue) {
        column.setStyle("-fx-font-size: 18");
        column.setSortable(false);
        column.setMinWidth(175);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyValue));
    }
}
