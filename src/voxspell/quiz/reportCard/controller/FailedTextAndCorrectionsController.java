package voxspell.quiz.reportCard.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import voxspell.tools.StringDifferenceFinder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Displayed when the user presses the 'View mistakes button'
 * This scene moves the 'failed' text slightly up and displays
 * corrections to the users word attempts.
 *
 * @author Will Molloy
 */
public class FailedTextAndCorrectionsController implements Initializable {

    @FXML
    private Parent wordAttemptsBox, wordCorrectionsBox;
    @FXML
    private Text failedTextView;
    private FailedTextController failedTextController;
    private List<TextFlow> wordAttemptsTexts, wordCorrectionsTexts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wordAttemptsTexts = new ArrayList<>();
        wordCorrectionsTexts = new ArrayList<>();

        // Add all text flows from the FXML hBox 'wordAttemptsBox' to the list 'wordAttemptsText'
        wordAttemptsTexts.addAll(wordAttemptsBox.getChildrenUnmodifiable().stream().filter(node -> node instanceof TextFlow).map(node -> (TextFlow) node).collect(Collectors.toList()));

        // Add all text flows from the FXML hBox 'wordCorrectionsBox' to the list 'wordCorrectionsTexts'
        wordCorrectionsTexts.addAll(wordCorrectionsBox.getChildrenUnmodifiable().stream().filter(node -> node instanceof TextFlow).map(node -> (TextFlow) node).collect(Collectors.toList()));
    }


    public void setDataAndShowGUI(FailedTextController failedTextController) {
        this.failedTextController = failedTextController;
        setTextViewText();
        generateAndShowCorrections();
    }

    private void setTextViewText() {
        failedTextView.setText(failedTextController.getText());
    }

    private void generateAndShowCorrections() {
        // Need to remove the placeholder text I used to help position textflows within scene builder
        removePlaceHolderTexts();

        List<String[]> incorrectWords = failedTextController.getIncorrectWords();
        for (int i = 0; i < incorrectWords.size(); i++) {
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

            // Add the texts to the text flow in the i'th row of the word attempts hbox
            wordAttemptsTexts.get(i).getChildren().addAll(prefix, wrongDelta, suffix);


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

            // Add the texts to the text flow in the i'th row of the word corrections hbox
            wordCorrectionsTexts.get(i).getChildren().addAll(prefix2, correctDelta, suffix2, extraMsg);
        }
    }

    private void removePlaceHolderTexts() {
        removePlaceHolderTextsFor(wordAttemptsTexts);
        removePlaceHolderTextsFor(wordCorrectionsTexts);
    }

    private void removePlaceHolderTextsFor(List<TextFlow> textFlows) {
        for (TextFlow textFlow : textFlows) {
            textFlow.getChildren().removeAll(textFlow.getChildren());
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
