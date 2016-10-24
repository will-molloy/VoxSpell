package voxspell.statistics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.tools.ImageLoader;
import voxspell.tools.NumberFormatter;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Statistics Scene.
 *
 * @author Will Molloy
 */
public class StatisticsController implements Initializable {

    private static final String VIEW_CATEGORY = "View Category Statistics", VIEW_PLOT = "View Overtime Graph";
    private static boolean categoryStatsIsShown = true;
    protected StatisticsRetriever statisticsRetriever = new StatisticsRetriever();
    protected NumberFormatter numberFormatter = new NumberFormatter();
    private ImageLoader imageLoader = new ImageLoader();
    @FXML
    private Pane statisticsViewPane;
    private Node categoryStatsRoot, overtimeGraphRoot;
    @FXML
    private Text totalWordsSpeltText;
    @FXML
    private Text lifeTimeAccuracyText;
    @FXML
    private Button changeViewBtn, backBtn, clearStatsBtn;
    private Image
            showGraphIcon = new Image(Main.class.getResourceAsStream("media/images/stats/line-chart.png")),
            showTableIcon = new Image(Main.class.getResourceAsStream("media/images/stats/table-grid.png")),
            backIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/logout_icon.png")),
            clearStatsIcon = new Image(Main.class.getResourceAsStream("media/images/stats/clear.png"));

    /**
     * Loads the scene: first loads sub scenes avaiable by the toggle button:
     * category table and line graph stats.
     * Then generates and shows life time stats.
     * Then loads icons for buttons.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSubScenes();

        // SHOW the same view the user left this scene on (also needed for clearing stats)
        if (categoryStatsIsShown) {
            showCategoryView();
        } else {
            showOverTimeGraph();
        }

        generateAndShowLifeTimeStatistics();
        loadBtnIcons();
    }

    private void generateAndShowLifeTimeStatistics() {
        int[] lifeTimeStats = statisticsRetriever.getLifeTimeStats();
        int correct = lifeTimeStats[0];
        int incorrect = lifeTimeStats[1];
        double accuracy = (correct * 100.0) / (incorrect + correct);

        totalWordsSpeltText.setText("Total Words Spelt: " + (correct + incorrect));
        lifeTimeAccuracyText.setText("Lifetime Accuracy: " + numberFormatter.formatAccuracy(accuracy));
    }

    private void loadBtnIcons() {
        imageLoader.loadSquareImageForBtn(backBtn, backIcon, 30);
        imageLoader.loadSquareImageForBtn(clearStatsBtn, clearStatsIcon, 30);
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

    /**
     * Handles the toggle button
     */
    @FXML
    private void handleChangeViewBtn(ActionEvent actionEvent) {
        if (categoryStatsIsShown) {
            showOverTimeGraph();
        } else {
            showCategoryView();
        }
    }

    private void showCategoryView() {
        updateStatisticsView(categoryStatsRoot);
        imageLoader.loadSquareImageForBtn(changeViewBtn, showGraphIcon, 30);
        changeViewBtn.setText(VIEW_PLOT);
        categoryStatsIsShown = true;
    }

    private void showOverTimeGraph() {
        updateStatisticsView(overtimeGraphRoot);
        imageLoader.loadSquareImageForBtn(changeViewBtn, showTableIcon, 30);
        changeViewBtn.setText(VIEW_CATEGORY);
        categoryStatsIsShown = false;
    }

    /**
     * Prompts the user on clearing stats and clears the hidden file.
     */
    @FXML
    private void handleClearStatsBtn(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Statistics");
        alert.setHeaderText("WARNING!\n" +
                "Clear All Statistics?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            statisticsRetriever.deleteStatistics();
            initialize(null, null); // reload scene
        }
    }

    @FXML
    private void handleBackToMainMenuBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }


}
