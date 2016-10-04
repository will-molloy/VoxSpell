package voxspell.quiz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.quiz.reportCard.FailedQuizReportCardFactory;
import voxspell.quiz.reportCard.PassedQuizReportCardFactory;
import voxspell.quiz.reportCard.ReportCardController;
import voxspell.quiz.reportCard.ReportCardFactory;
import voxspell.tools.CustomFileReader;
import voxspell.tools.TextToSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the SpellingQuizController.
 *
 * @author Will Molloy
 */
public class SpellingQuizController {


    private static final int NUM_LEVELS = 11;
    // Game logic
    private static int _level;
    private int wordNumber;

    // Reportcard shown after quiz
    private static ReportCardFactory reportCardFactory;
    private int wordsCorrectFirstAttempt, wordAttempt;
    private boolean firstAttempt;
    private List<String> wordList;
    private String word;
    private TextToSpeech textToSpeech = TextToSpeech.getInstance();
    @FXML
    private Text levelText, wordsToSpellText;
    @FXML
    private TextField wordEntryField;
    // Tools
    private CustomFileReader fileReader = new CustomFileReader();
    private ArrayList<String> wordsCopy;
    private ArrayList<String> wordFirstAttempts;
    private ArrayList<String> wordSecondAttempts;

    @FXML
    private Parent imageHBox;
    private Image wordCorrect, wordIncorrect; // TODO
    private List<ImageView> images = new ArrayList<>();

    {
        images.addAll(imageHBox.getChildrenUnmodifiable().stream().filter(n -> n instanceof ImageView).map(n -> (ImageView) n).collect(Collectors.toList()));
    }


    public int promptUserForInitialLevel() {
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

    public void newQuiz(int level) {
        resetFields();

        _level = level;
        levelText.setText("Level " + level);
        readWordsFromFile();
        continueSpellingQuiz();
    }

    private void resetFields() {
        levelText.setText("Level ?");
        wordsToSpellText.setText("Spell word 1 of 10");
        wordsCorrectFirstAttempt = 0;
        wordAttempt = 0;
        firstAttempt = true;
        wordFirstAttempts = new ArrayList<>();
        wordSecondAttempts = new ArrayList<>();
    }

    private void readWordsFromFile() {
        wordList = fileReader.getWordList(_level);
        wordsCopy = new ArrayList<>(wordList);
    }

    public void continueSpellingQuiz() {
        // Quiz is finished when the wordlist is empty
        if (wordList.size() > 9) { // TODO
            word = wordList.get(0);
            System.out.println(word);
            wordNumber = 11 - wordList.size();
            String line;

            if (firstAttempt) {
                line = "Please spell ... " + word;
                wordsToSpellText.setText("Spell word " + wordNumber + " of 10");
                textToSpeech.readSentence(line);
            } else { /* Second Attempt */
                line = "Try once more. " + word + " ... " + word;
                textToSpeech.readSentence(line);
                firstAttempt = false;
            }
        } else { /* Quiz Completed */
            if (wordsCorrectFirstAttempt < 1) { // TODO
                /* Failed */
                reportCardFactory = new FailedQuizReportCardFactory();
            } else {
                /* Passed */
                reportCardFactory = new PassedQuizReportCardFactory();
            }
            /*
             * continueSpellingQuiz() is called by a SwingWorker (not from a JavaFX thread)
             * and need to run this code on an FX thread. Platform.runLater() achieves this.
             */
            Platform.runLater(() -> {
                ReportCardController controller = reportCardFactory.getControllerAndShowScene();
                controller.setValues(wordsCopy, wordFirstAttempts, wordSecondAttempts, _level);
                controller.generateScene();
            });
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

        if (wordList.size() > 0) {

            if (attempt.equals(word)) { /* Correct */
                textToSpeech.readSentenceAndContinueSpellingQuiz("Correct", this);
                wordList.remove(word);

                if (firstAttempt) {
                    /* First attempt correct */
                    wordFirstAttempts.add(attempt);
                    wordSecondAttempts.add(attempt); // adding word here too to maintain indexing
                    wordsCorrectFirstAttempt++;
                    images.get(wordNumber).setImage(wordCorrect);
                } else {
                    /* Second attempt correct */
                    wordSecondAttempts.add(attempt);
                    firstAttempt = true;
                }
                wordAttempt++;

            } else { /* Incorrect */

                textToSpeech.readSentenceAndContinueSpellingQuiz("Incorrect", this);

                if (firstAttempt) {
                    /* First attempt incorrect */
                    wordFirstAttempts.add(attempt);
                    firstAttempt = false;
                } else {
                    /* Second attempt incorrect */
                    wordSecondAttempts.add(attempt);
                    images.get(wordNumber).setImage(wordIncorrect);
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
        // TODO
    }

    @FXML
    private void handleRepeatWordBtn(ActionEvent actionEvent) {
        textToSpeech.readSentenceSlowly(word);
    }


}
