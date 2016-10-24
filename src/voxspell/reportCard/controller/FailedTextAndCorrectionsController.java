package voxspell.reportCard.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import voxspell.tools.StringDifferenceFinder;

import java.util.List;

/**
 * Displayed when the user presses the 'View mistakes button'
 * This scene moves the 'failed' text slightly up and displays
 * corrections to the users word attempts.
 *
 * @author Will Molloy
 */
public class FailedTextAndCorrectionsController {

    @FXML
    private VBox correctionsVBox;
    @FXML
    private Text failedTextView;
    private FailedTextController failedTextController;

    private void clearGUI() {
        correctionsVBox.getChildren().removeAll(correctionsVBox.getChildren());
    }

    /**
     * Sets the data for the scene:
     * REQUIRED to sync the 'text only' view with this one.
     */
    public void setDataAndShowGUI(FailedTextController failedTextController) {
        this.failedTextController = failedTextController;
        setTextViewText();
        generateAndShowCorrections();
    }

    private void setTextViewText() {
        failedTextView.setText(failedTextController.getText());
    }

    /**
     * Generates and shows the table of corrections to the user.
     */
    private void generateAndShowCorrections() {
        // Clear existing text within the VBox
        clearGUI();

        List<String[]> incorrectWords = failedTextController.getIncorrectWords();
        for (int i = 0; i < incorrectWords.size(); i++) {
            HBox hBox = new HBox();
            hBox.setPrefHeight(40);
            hBox.setPrefWidth(878);

            String[] incorrectWord = incorrectWords.get(i);
            String correctSpelling = incorrectWord[0].trim();
            String usersAttempt = incorrectWord[1].trim();

            // Calculate difference between users attempt and actual spelling
            StringDifferenceFinder stringDifferenceFinder = new StringDifferenceFinder(correctSpelling, usersAttempt);

            // Extract common prefix and suffix
            String[] attemptStrings = stringDifferenceFinder.getPrefixSuffixAndDelta(false);
            String commonPrefix = attemptStrings[0];
            String commonSuffix = attemptStrings[1];

            // Extract wrong difference (what user input)
            String wrongDifference = attemptStrings[2];

            Text prefix = getPrefixOrSuffixText(commonPrefix);
            Text suffix = getPrefixOrSuffixText(commonSuffix);
            Text wrongDelta = getWrongDeltaText();
            wrongDelta.setText(wrongDifference);

            // Add the texts to the text flow
            TextFlow usersAttemptTxt = new TextFlow();
            usersAttemptTxt.getChildren().addAll(prefix, wrongDelta, suffix);
            usersAttemptTxt.setPrefWidth(439);
            usersAttemptTxt.setPrefHeight(40);

            // Extract correct difference (what user should have spelt)
            String[] correctStrings = stringDifferenceFinder.getPrefixSuffixAndDelta(true);
            String correctDifference = correctStrings[2];

            // (Need to recreate prefix/suffix for it to be displayed twice)
            Text prefix2 = getPrefixOrSuffixText(commonPrefix);
            Text suffix2 = getPrefixOrSuffixText(commonSuffix);

            // Set correction with actual spelling
            Text correctDelta = getCorrectDeltaText();
            correctDelta.setText(correctDifference);

            // Extra message if user spelt word correctly but incorrect grammar/case
            String message = "\t";
            if (removeSymbolsFromString(correctSpelling).equals(removeSymbolsFromString(usersAttempt))) {
                message += "- Close! Correct grammar required.";
            } else if (correctSpelling.toLowerCase().equals(usersAttempt.toLowerCase())) {
                message += "- Close! Correct case required.";
            }
            Text extraMsg = new Text(message);
            extraMsg.setFont(new Font(18));

            // Add the texts to the text flow
            TextFlow correctSpellingTxt = new TextFlow();
            correctSpellingTxt.getChildren().addAll(prefix2, correctDelta, suffix2, extraMsg);
            correctSpellingTxt.setPrefWidth(439);
            correctSpellingTxt.setPrefHeight(40);

            // Lastly add the two text flows to the hbox side by side
            hBox.getChildren().addAll(usersAttemptTxt, correctSpellingTxt);
            // Then add the hbox to the vbox, so hboxes will be stacked on top of each other within the scroll pane.
            correctionsVBox.getChildren().add(hBox);
        }
    }

    private Text getPrefixOrSuffixText(String commonPrefixOrSuffix) {
        Text text = new Text();
        text.setText(commonPrefixOrSuffix != null ? commonPrefixOrSuffix : "");
        text.setFont(new Font(22));
        return text;
    }

    private Text getWrongDeltaText() {
        Text text = new Text();
        text.setUnderline(true);
        text.setFill(Color.RED);
        text.setFont(new Font(22));
        return text;
    }

    private Text getCorrectDeltaText() {
        Text text = new Text();
        text.setUnderline(true);
        text.setFill(Color.GREEN);
        text.setFont(new Font(22));
        return text;
    }

    private String removeSymbolsFromString(String s) {
        return s.replaceAll("[-+.^:,'`\"]", "");
    }


}
