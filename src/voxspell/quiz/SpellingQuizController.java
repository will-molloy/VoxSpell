package voxspell.quiz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import voxspell.tools.TextToSpeech;
import voxspell.wordlistEditor.Word;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the SpellingQuizController.
 *
 * @author Karim Cisse - implemented Spelling Quiz logic
 * @author Will Molloy - convert to JavaFX, adding ImageViews, using WordList/Word object.
 */
public class SpellingQuizController {
    private static final int NUM_LEVELS = 11;

    // Game logic
    private static List<WordList> wordLists = WordListEditorController.getWordLists();
    private static WordList entireWordListForCategory;
    // Reportcard shown after quiz
    private static ReportCardFactory reportCardFactory;
    private List<Word> quizWordList;
    private int wordNumber;
    private int wordsCorrectFirstAttempt;
    private boolean firstAttempt;
    private Word word;
    private TextToSpeech textToSpeech = TextToSpeech.getInstance();
    @FXML
    private Text categoryText, wordsToSpellText;
    @FXML
    private TextField wordEntryField;
    private List<String> quizWordListCopy;
    private List<String> wordFirstAttempts;
    private List<String> wordSecondAttempts;

    // Images
    @FXML
    private Parent imageHBox;
    private Image wordCorrect = new Image(new File("src/media/images/tick_80.jpg").toURI().toString());
    private Image wordIncorrect = new Image(new File("src/media/images/cross_80.jpg").toURI().toString());
    private Image wordFaulted = new Image(new File("src/media/images/square_80.jpg").toURI().toString());
    private List<ImageView> images = new ArrayList<>();

    public String promptUserForInitialLevel() {
        // Get wordlist names
        List<String> options = wordLists.stream().map(WordList::toString).collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Choose a category");
        dialog.setHeaderText("Please choose a word list.");
        dialog.setContentText("Options: ");

        Optional<String> result = dialog.showAndWait();
        String option = null;
        if (result.isPresent()) {
            option = result.get();
        }

        return option;
    }

    public void newQuiz(String category) {
        resetFields();
        // Get chosen wordlist
        for (WordList wordList : wordLists) {
            if (wordList.toString().equals(category)) {
                entireWordListForCategory = wordList;
                break;
            }
        }
        // Get 10 random words from the wordlist for a quiz
        quizWordList = new ArrayList<>(entireWordListForCategory.wordList());
        Collections.shuffle(quizWordList);
        quizWordList = new ArrayList<>(quizWordList.subList(0, 10));
        quizWordListCopy = quizWordList.stream().map(Word::toString).collect(Collectors.toList());
        categoryText.setText(entireWordListForCategory.toString());
        continueSpellingQuiz();
    }

    public void nextQuiz() {
        if (entireWordListForCategory.hasNext()) {
            newQuiz(entireWordListForCategory.next().toString());
        } else {
            /* final quiz CHOSE A NEW CATEGORY? */
        }
    }

    private void resetFields() {

        // Add ImageViews inside imageHBox to the ArrayList images
        images.addAll(imageHBox.getChildrenUnmodifiable().stream().filter(node -> node instanceof ImageView).map(node -> (ImageView) node).collect(Collectors.toList()));
        // Blank out all images
        for (ImageView imageView : images) {
            imageView.setImage(null);
        }
        // Reset text views
        categoryText.setText("Level ?");
        wordsToSpellText.setText("Spell word 1 of 10");
        // Reset game logic
        wordsCorrectFirstAttempt = 0;
        firstAttempt = true;
        wordFirstAttempts = new ArrayList<>();
        wordSecondAttempts = new ArrayList<>();
    }

    public void continueSpellingQuiz() {
        // Quiz is finished when the quizWordList is empty
        if (quizWordList.size() > 0) {
            word = quizWordList.get(0);
            System.out.println(word);
            wordNumber = 11 - quizWordList.size();
            String line;

            if (firstAttempt) {
                line = "Please spell ... " + word;
                wordsToSpellText.setText("Spell word " + wordNumber + " of 10");
                textToSpeech.readSentence(line);
            } else { /* Second Attempt */
                line = "Try once more. " + word + ". ... " + word;
                textToSpeech.readSentence(line);
                firstAttempt = false;
            }
        } else { /* Quiz Completed */
            if (wordsCorrectFirstAttempt < 9) {
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
                controller.setValues(quizWordListCopy, wordFirstAttempts, wordSecondAttempts, entireWordListForCategory);
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
        int imageIndex = wordNumber - 1;
        String attempt = wordEntryField.getText();
        wordEntryField.setText("");

        if (quizWordList.size() > 0) {

            /*********************** DEBUG **********************/
            if (attempt.equals("skip_")) {
                ReportCardController.setNumWords(wordNumber - 1); // avoid array index oob
                reportCardFactory = new PassedQuizReportCardFactory();
                Platform.runLater(() -> {
                    ReportCardController controller = reportCardFactory.getControllerAndShowScene();
                    controller.setValues(quizWordListCopy, wordFirstAttempts, wordSecondAttempts, entireWordListForCategory);
                    controller.generateScene();
                });
                return;
            }
            /*********************** DEBUG **********************/


            if (attempt.equals(word.toString())) { /* Correct */

                if (firstAttempt) {
                    /* First attempt correct */
                    wordFirstAttempts.add(attempt);
                    wordSecondAttempts.add(attempt); // adding word here too to maintain indexing
                    wordsCorrectFirstAttempt++;
                    images.get(imageIndex).setImage(wordCorrect);
                } else {
                    /* Second attempt correct */
                    wordSecondAttempts.add(attempt);
                    firstAttempt = true;
                    images.get(imageIndex).setImage(wordCorrect);
                }
                quizWordList.remove(word);
                textToSpeech.readSentenceAndContinueSpellingQuiz("Correct", this);

            } else { /* Incorrect */

                if (firstAttempt) {
                    /* First attempt incorrect */
                    wordFirstAttempts.add(attempt);
                    images.get(imageIndex).setImage(wordFaulted);
                    firstAttempt = false;
                } else {
                    /* Second attempt incorrect */
                    wordSecondAttempts.add(attempt);
                    images.get(imageIndex).setImage(wordIncorrect);
                    quizWordList.remove(word);
                    firstAttempt = true;
                }
                textToSpeech.readSentenceAndContinueSpellingQuiz("Incorrect", this);
            }
        }
    }

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    private void handleDefinitionBtn(ActionEvent actionEvent) {
        String definition = word.getDefinition();
        System.out.println(definition);
        textToSpeech.readSentence(definition);
    }

    @FXML
    private void handleRepeatWordBtn(ActionEvent actionEvent) {
        textToSpeech.readSentenceSlowly(word.toString());
    }


}
