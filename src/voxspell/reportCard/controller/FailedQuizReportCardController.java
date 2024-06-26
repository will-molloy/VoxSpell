package voxspell.reportCard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import voxspell.Main;

import java.io.IOException;

/**
 * Controller for the report card shown when the user fails a quiz.
 * Extends the abstract ReportCardController class for common values with the PassedQuizReport to be set.
 *
 * @author Will Molloy
 */
public class FailedQuizReportCardController extends ReportCardController {

    @FXML
    private Button viewMistakesBtn, retryLevelBtn;
    @FXML
    private Pane failurePane;
    private Node textOnlyRoot, textAndCorrectionsRoot;
    private boolean textOnlyView;

    private Image
            repeatIcon = new Image(Main.class.getResourceAsStream("media/images/quiz/repeat_icon.png")),
            viewIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/show_icon.png")),
            hideIcon = new Image(Main.class.getResourceAsStream("media/images/report_card/show_less_icon.png"));

    /**
     * Implemented hook method, loads the failed quiz report specific elements.
     */
    @Override
    public void createSubClassGUI() {
        loadSubScenes();
        failurePane.getChildren().add(textOnlyRoot);
        retryLevelBtn.setText("Retry " + wordList.toString());
        textOnlyView = true;
        imageLoader.loadSquareImageForBtn(retryLevelBtn, repeatIcon, 40);
        imageLoader.loadSquareImageForBtn(viewMistakesBtn, viewIcon, 40);
    }

    /**
     * Loads the sub scenes accessible via the toggle button:
     * Text view and Corrections view, where the user can toggle between hiding and viewing their mistakes.
     */
    private void loadSubScenes() {
        try {
            // Load 'text only' view initially displayed
            FXMLLoader textLoader = new FXMLLoader(Main.class.getResource("reportCard/fxml/Failed_Text.fxml"));
            textOnlyRoot = textLoader.load();
            FailedTextController failedTextController = textLoader.getController();

            // Load 'corrections' view displayed if user presses 'view mistakes' btn
            FXMLLoader correctionsLoader = new FXMLLoader(Main.class.getResource("reportCard/fxml/Failed_Text_And_Corrections.fxml"));
            textAndCorrectionsRoot = correctionsLoader.load();
            FailedTextAndCorrectionsController failedTextAndCorrectionsController = correctionsLoader.getController();

            // Ensure both views have the same text
            failedTextAndCorrectionsController.setDataAndShowGUI(failedTextController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the toggle button:
     * switches between hiding and viewing the users mistakes.
     */
    @FXML
    private void handleMistakesBtn(ActionEvent actionEvent) {
        failurePane.getChildren().removeAll(failurePane.getChildren());
        if (textOnlyView) {
            failurePane.getChildren().add(textAndCorrectionsRoot);
            viewMistakesBtn.setText("Hide Mistakes");
            accuracyTextView.setVisible(false);
            imageLoader.loadSquareImageForBtn(viewMistakesBtn, hideIcon, 40);
        } else {
            failurePane.getChildren().add(textOnlyRoot);
            viewMistakesBtn.setText("View Mistakes");
            accuracyTextView.setVisible(true);
            imageLoader.loadSquareImageForBtn(viewMistakesBtn, viewIcon, 40);
        }
        textOnlyView = !textOnlyView;
    }


}
