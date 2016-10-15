package voxspell.statistics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import voxspell.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Statistics Screen.
 *
 * @author Will Molloy
 */
public class StatisticsController implements Initializable {

    private static final String VIEW_CATEGORY = "View Category Statistics", VIEW_PLOT = "View Overtime Graph";
    @FXML
    private Pane statisticsViewPane;
    private boolean categoryStatsIsShown;
    private Node categoryStatsRoot, overtimeGraphRoot;
    @FXML
    private Text totalWordsSpeltText;
    @FXML
    private Text lifeTimeAccuracyText;
    @FXML
    private Button changeViewBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateAndShowLifeTimeStatistics();
        loadSubScenes();
        categoryStatsIsShown = true;
        updateStatisticsView(categoryStatsRoot);
        changeViewBtn.setText(VIEW_PLOT);
    }

    private void generateAndShowLifeTimeStatistics() {
    }

    private void updateStatisticsView(Node newView) {
        statisticsViewPane.getChildren().removeAll(statisticsViewPane.getChildren());
        statisticsViewPane.getChildren().add(newView);
    }

    private void loadSubScenes() {
        try {
            // Load 'category stats' view initially displayed
            FXMLLoader categoryStatsLoader = new FXMLLoader(getClass().getResource("fxml/Category_Stats.fxml"));
            categoryStatsRoot = categoryStatsLoader.load();

            // Load 'overtime graph' view displayed if user presses 'view overtime graph' btn
            FXMLLoader overtimeGraphLoader = new FXMLLoader(getClass().getResource("fxml/Overtime_Graph.fxml"));
            overtimeGraphRoot = overtimeGraphLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangeViewBtn(ActionEvent actionEvent) {
        if (categoryStatsIsShown){
            updateStatisticsView(overtimeGraphRoot);
            changeViewBtn.setText(VIEW_CATEGORY);
        } else {
            updateStatisticsView(categoryStatsRoot);
            changeViewBtn.setText(VIEW_PLOT);
        }
        categoryStatsIsShown = !categoryStatsIsShown;
    }
    @FXML
    private void handleClearStatsBtn(ActionEvent actionEvent) {
    }
    @FXML
    private void handleBackToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }


}
