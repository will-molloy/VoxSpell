package voxspell.reportCard.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Failed Text Phrase shown within the Failed Quiz ReporCard
 *
 * @author Will Molloy
 */
public class FailedTextController extends FailedQuizReportCardController implements Initializable {

    @FXML
    private Text failedTextView;

    /**
     * Generates the failed text phrase.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> failed = new ArrayList<>();
        failed.add("That's Unfortunate");
        failed.add("Better Luck Next Time!");
        failed.add("You Have Been Unsuccessful");
        Collections.shuffle(failed);
        failedTextView.setText(failed.get(0));
    }

    /**
     * Returns the text phrase, required to sync with the view mistakes scene.
     */
    public String getText() {
        return failedTextView.getText();
    }

    /**
     * Returns the incorrectWords, required to sync with the view mistakes scene.
     */
    public List<String[]> getIncorrectWords() {
        return incorrectWords;
    }
}
