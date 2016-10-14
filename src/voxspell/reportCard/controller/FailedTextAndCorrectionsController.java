package voxspell.reportCard.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Displayed when the user presses the 'View mistakes button'
 * This scene moves the 'failed' text slightly up and displays
 * corrections to the users word attempts.
 *
 * @author Will Molloy
 */
public class FailedTextAndCorrectionsController {

    @FXML
    private Text failedTextView;

    protected void setTextViewText(String failedText) {
        failedTextView.setText(failedText);
    }


}
