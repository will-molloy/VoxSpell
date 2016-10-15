package voxspell.reportCard.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import voxspell.tools.StringDifferenceFinder;

/**
 * Displayed when the user presses the 'View mistakes button'
 * This scene moves the 'failed' text slightly up and displays
 * corrections to the users word attempts.
 *
 * @author Will Molloy
 */
public class FailedTextAndCorrectionsController{

    @FXML
    private Text failedTextView;
    @FXML
    private TextFlow correctionsTextFlow, attemptsTextFlow;
    private FailedTextController failedTextController;

    public void setDataAndShowGUI(FailedTextController failedTextController) {
        this.failedTextController = failedTextController;
        setTextViewText();
        generateAndShowCorrections();
    }

    private void setTextViewText() {
        failedTextView.setText(failedTextController.getText());
    }

    private void generateAndShowCorrections() {
        for (String[] incorrectWord : failedTextController.getIncorrectWords()){
            String correctSpelling = incorrectWord[0];
            String usersAttempt = incorrectWord[1];
            StringDifferenceFinder stringDifferenceFinder = new StringDifferenceFinder(correctSpelling, usersAttempt);
            String[] strings = stringDifferenceFinder.getPrefixSuffixAndDelta();
            String commonPrefix = strings[0];
            String commonSuffix = strings[1];
            String difference = strings[2];

            Text prefix = TextFactory.getPrefixOrSuffixText(commonPrefix);

            Text suffix = TextFactory.getPrefixOrSuffixText(commonSuffix);

            Text delta = TextFactory.getDeltaText();
            delta.setText(difference);

            attemptsTextFlow.getChildren().addAll(prefix, delta, suffix);
            correctionsTextFlow.getChildren().addAll(TextFactory.getPrefixOrSuffixText(correctSpelling)); // need same style
        }
    }



}
