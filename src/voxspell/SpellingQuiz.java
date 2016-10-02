package voxspell;

import javafx.scene.control.TextField;
import voxspell.tools.CustomFileReader;
import voxspell.tools.TextToSpeech;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the SpellingQuiz.
 *
 * @author Will Molloy
 */
public class SpellingQuiz {

    private static final int NUM_LEVELS = 11;

    @FXML
    private Text levelText, wordsToSpellText;

    @FXML
    private TextField wordEntryField;

    // Game logic
    private int level, wordsCorrectFirstAttempt, wordAttempt;
    private boolean firstAttempt;
    private List<String> wordList;
    private String word;

    // Tools
    private CustomFileReader fileReader = new CustomFileReader();
    private TextToSpeech textToSpeech = TextToSpeech.getInstance();

    public void newQuiz() {
        resetFields();

        level = promptUserForInitialLevel();
        levelText.setText("Level " + level);
        readWordsFromFile();
        continueSpellingQuiz();
    }

    private int promptUserForInitialLevel() {
        List<Integer> levelOptions = new ArrayList<>();
        for (int i = 1; i <= NUM_LEVELS; i++) {
            levelOptions.add(i);
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, levelOptions);
        dialog.setTitle("Starting Level");
        dialog.setHeaderText("Please choose a starting level.");
        dialog.setContentText("Levels: ");

        Optional<Integer> result = dialog.showAndWait();
        int number = 1;
        if (result.isPresent()) {
            number = result.get();
        }

        return number;
    }

    private void resetFields() {
        levelText.setText("Level ?");
        wordsToSpellText.setText("Spell word 1 of 10");
        wordsCorrectFirstAttempt = 0;
        wordAttempt = 0;
        firstAttempt = true;
    }

    private void readWordsFromFile() {
        wordList = fileReader.getWordList(level);
    }

    public void continueSpellingQuiz() {
        // Quiz is finished when the wordlist is empty
        if (wordList.size() > 0){
            word = wordList.get(0);
            int wordNumber = 11 - wordList.size();
            String line;

            if (firstAttempt){
                line = "Please spell ... " + word;
                wordsToSpellText.setText("Spell word " + wordNumber + " of 10");
                textToSpeech.readSentence(line);
            } else { /* Second Attempt */
                line = "Try once more. " + word + " ... " + word;
                textToSpeech.readSentence(line);
                firstAttempt = false;
            }
        } else { /* Quiz Completed */
            if (wordsCorrectFirstAttempt < 9){
                /* Failed */
            } else {
                /* Passed */
            }
        }
    }

    /*
    * Handles both the enter word button AND pressing 'enter' within the word entry field.
    */
    @FXML
    private void handleEnterWordBtn(ActionEvent actionEvent) {
        checkInputWord();
    }

    private void checkInputWord() {
        String attempt = wordEntryField.getText();
        wordEntryField.setText("");

        if (wordList.size() > 0){

            if (attempt.equals(word)){ /* Correct */
                textToSpeech.readSentenceAndContinueSpellingQuiz("Correct", this);
                wordList.remove(word);

                if (firstAttempt){
                    /* First attempt correct */
                    wordsCorrectFirstAttempt++;
                } else {
                    /* Second attempt correct */
                    firstAttempt = true;
                }
                wordAttempt++;

            } else { /* Incorrect */

                textToSpeech.readSentenceAndContinueSpellingQuiz("Incorrect", this);

                if(firstAttempt){
                    /* First attempt incorrect */
                    firstAttempt = false;
                } else {
                    /* Second attempt incorrect */
                    wordList.remove(word);
                    firstAttempt = true;
                    wordAttempt++;
                }
            }
        }
    }

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    private void handleDefinitionBtn(ActionEvent actionEvent) {
    }

    @FXML
    private void handleRepeatWordBtn(ActionEvent actionEvent) { textToSpeech.readSentenceSlowly(word);
    }


}
