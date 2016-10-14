package voxspell.reportCard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the report card shown when the user fails a quiz.
 *
 * @author Will Molloy
 */
public class FailedQuizReportCardController extends ReportCardController {
    @FXML
    private Text failedTextView;

    @Override
    public void createGUI() {
        List<String> failed = new ArrayList<>();
        failed.add("That's Unfortunate");
        failed.add("Better Luck Next Time!");
        failed.add("You Have Been Unsuccessful");
        Collections.shuffle(failed);
        failedTextView.setText(failed.get(0));
    }

    @FXML
    final void handleMistakesBtn(ActionEvent actionEvent) {

    }



}
