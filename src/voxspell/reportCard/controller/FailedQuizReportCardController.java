package voxspell.reportCard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Controller for the report card shown when the user fails a quiz.
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

    @Override
    public void createSubClassGUI() {
        loadSubScenes();
        failurePane.getChildren().add(textOnlyRoot);
        retryLevelBtn.setText("Retry " + wordList.toString());
        textOnlyView = true;
    }

    private void loadSubScenes() {
        try {
            // Load 'text only' view initially displayed
            FXMLLoader textLoader = new FXMLLoader(getClass().getResource("../fxml/Failed_Text.fxml"));
            textOnlyRoot = textLoader.load();
            FailedTextController failedTextController = textLoader.getController();

            // Load 'corrections' view displayed if user presses 'view mistakes' btn
            FXMLLoader correctionsLoader = new FXMLLoader(getClass().getResource("../fxml/Failed_Text_And_Corrections.fxml"));
            textAndCorrectionsRoot = correctionsLoader.load();
            FailedTextAndCorrectionsController failedTextAndCorrectionsController = correctionsLoader.getController();

            // Ensure both views have the same text
            failedTextAndCorrectionsController.setDataAndShowGUI(failedTextController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMistakesBtn(ActionEvent actionEvent) {
        failurePane.getChildren().removeAll(failurePane.getChildren());
        if (textOnlyView) {
            failurePane.getChildren().add(textAndCorrectionsRoot);
            viewMistakesBtn.setText("Hide Mistakes");
        } else {
            failurePane.getChildren().add(textOnlyRoot);
            viewMistakesBtn.setText("View Mistakes");
        }
        textOnlyView = !textOnlyView;
    }


}
