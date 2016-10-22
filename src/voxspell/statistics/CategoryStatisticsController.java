package voxspell.statistics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Generates the category stats shown in the statistics scene.
 *
 * @author Will Molloy
 */
public class CategoryStatisticsController implements Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private ScrollPane scrollPane;
    // Object to read stats file
    private StatisticsFileHandler statisticsFileHandler;
    // Object to get information on wordlists/categories
    private List<WordList> wordlists;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wordlists = WordListEditorController.getWordLists();
        statisticsFileHandler = new StatisticsFileHandler();

        generateAndShowStatsInScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }

    private void generateAndShowStatsInScrollPane() {
        ObservableList<CategoryStat> data = FXCollections.observableArrayList();
        for (WordList wl : wordlists) {
            String category = wl.toString();
            int[] extractedStat = statisticsFileHandler.getStatsForCategory(category);
            int totalSpelt = extractedStat[0] + extractedStat[1];
            double accuracy = extractedStat[0] * 100.0 / totalSpelt;
            if (totalSpelt > 0) {
                CategoryStat stat = new CategoryStat(category, totalSpelt + "", new DecimalFormat("####0.00").format(accuracy) + "%"); // TODO CONSTANT method to get this format?
                data.add(stat);
            }
        }

        TableColumn<CategoryStat, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setStyle("-fx-font-size: 18");
        categoryCol.setSortable(false);
        categoryCol.setMinWidth(200);
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<CategoryStat, String> totalSpeltCol = new TableColumn<>("Words Spelt");
        totalSpeltCol.setStyle("-fx-font-size: 18");
        totalSpeltCol.setSortable(false);
        totalSpeltCol.setMinWidth(200);
        totalSpeltCol.setCellValueFactory(new PropertyValueFactory<>("totalSpelt"));

        TableColumn<CategoryStat, String> accuracyCol = new TableColumn<>("Accuracy");
        accuracyCol.setStyle("-fx-font-size: 18");
        accuracyCol.setSortable(false);
        accuracyCol.setMinWidth(200);
        accuracyCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));

        Collections.sort(data);

        tableView.setItems(data);
        tableView.getColumns().addAll(categoryCol, totalSpeltCol, accuracyCol);
    }
}
