package voxspell.quiz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.dailyChallenges.ChallengeType;
import voxspell.dailyChallenges.DailyChallengeGUIController;
import voxspell.quiz.reportCard.FailedQuizReportCardFactory;
import voxspell.quiz.reportCard.PassedQuizReportCardFactory;
import voxspell.quiz.reportCard.ReportCardFactory;
import voxspell.quiz.reportCard.controller.ReportCardController;
import voxspell.statistics.StatisticsFileHandler;
import voxspell.tools.TextToSpeech;
import voxspell.wordlistEditor.Word;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

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

    // Game logic
    private static List<WordList> wordLists;
    private static WordList categoryWordList;
    // Reportcard shown after quiz
    private static ReportCardFactory reportCardFactory;
    private final double QUIZ_PASS_THRESHOLD = 0.8; // % required to pass quiz
    private int currentStreak;
    private int bestStreak;
    private long startTime;

    // StatisticsFileHandler object for saving stats
    private StatisticsFileHandler statisticsFileHandler = new StatisticsFileHandler();
    // DailyChallenge - for updating any challenges
    private DailyChallengeGUIController dailyChallengeGUIController = new DailyChallengeGUIController();
    @FXML
    private ProgressBar quizProgressBar;
    private List<Word> quizWordList;
    private int wordNumber;
    private int wordsCorrect;
    private int quizSize;
    private Word word;
    // Tools
    private TextToSpeech textToSpeech = TextToSpeech.getInstance();

    // FXML
    @FXML
    private Text categoryText;
    @FXML
    private TextField wordEntryField;
    private List<String> quizWordListCopy;
    private List<String> wordFirstAttempts;

    public String promptUserForInitialLevel() {
        // Get word lists from editor.
        wordLists = WordListEditorController.getWordLists();
        if (!(wordLists.size() > 0)) {
            showNoWordListsDialog();
            return null;
        }
        // Get word list/category names (wordlist.toString() does this)
        List<String> options = wordLists.stream().map(WordList::toString).collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Choose a category");
        dialog.setHeaderText("Please choose a starting category.");
        dialog.setContentText("Options: ");

        Optional<String> result = dialog.showAndWait();
        String option = null;
        if (result.isPresent()) {
            option = result.get();
        }

        return option;
    }

    private void showNoWordListsDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Categories");
        alert.setHeaderText("VoxSpell currently has no categories");
        alert.setContentText("Go to the word list editor and create word lists.");

        alert.showAndWait();
    }

    public void newQuiz(String category) {
        resetFields();
        // Get chosen word list
        for (WordList wordList : wordLists) {
            if (wordList.toString().equals(category)) {
                categoryWordList = wordList;
                break;
            }
        }
        // Get 10 (or word list size) random words from the word list for a quiz
        quizWordList = new ArrayList<>(categoryWordList.wordList());
        Collections.shuffle(quizWordList);
        quizSize = Math.min(10, quizWordList.size());
        quizWordList = new ArrayList<>(quizWordList.subList(0, quizSize));
        quizWordListCopy = quizWordList.stream().map(Word::toString).collect(Collectors.toList());

        categoryText.setText(categoryWordList.toString());
        continueSpellingQuiz();
    }

    public void nextQuiz() {
        if (categoryWordList.hasNext()) {
            newQuiz(categoryWordList.next().toString());
        }
    }

    private void resetFields() {
        clearGUI();
        resetGameLogic();
        startTime = System.currentTimeMillis();
    }

    private void clearGUI() {
        // Reset text views
        categoryText.setText("");
        quizProgressBar.setProgress(0);
    }

    private void resetGameLogic() {
        wordsCorrect = 0;
        wordFirstAttempts = new ArrayList<>();
        bestStreak = 0;
        currentStreak = 0;
    }

    public void continueSpellingQuiz() {
        // Quiz is finished when the quizWordList is empty
        if (quizWordList.size() > 0) {
            word = quizWordList.get(0);
            System.out.println(word);
            wordNumber = quizSize + 1 - quizWordList.size();

            textToSpeech.readSentence("Please spell ... " + word);

        } else { /* Quiz Completed */
            if (wordsCorrect >= QUIZ_PASS_THRESHOLD * quizSize) {
                /* Passed */
                reportCardFactory = new PassedQuizReportCardFactory();
            } else {
                /* Failed */
                reportCardFactory = new FailedQuizReportCardFactory();
            }
            /*
             * ContinueSpellingQuiz() is called by a SwingWorker (not from a JavaFX thread)
             * and need to run this code on an FX thread. Platform.runLater() achieves this.
             */
            Platform.runLater(() -> {
                ReportCardController controller = reportCardFactory.getControllerAndShowScene();
                final long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                controller.setValues(quizWordListCopy, wordFirstAttempts, categoryWordList, elapsedTime, Math.max(currentStreak, bestStreak));
                controller.generateScene();
            });
            clearGUI();
            // Update the 'quiz completed' challenge
            dailyChallengeGUIController.updateChallenge(ChallengeType.QUIZES_COMPLETED, 1);
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

        if (quizWordList.size() > 0) {

            if (attempt.equals(word.toString())) { /* Correct */

                currentStreak++;
                wordFirstAttempts.add(attempt);
                wordsCorrect++;
                textToSpeech.readSentenceAndContinueSpellingQuiz("Correct", this);

                statisticsFileHandler.writeStatistic(word.toString(), true, categoryText.getText());

            } else { /* Incorrect */
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak;
                }
                currentStreak = 0;
                wordFirstAttempts.add(attempt);
                textToSpeech.readSentenceAndContinueSpellingQuiz("Incorrect", this);

                statisticsFileHandler.writeStatistic(word.toString(), false, categoryText.getText());
            }
            quizProgressBar.setProgress(wordNumber * 1.0 / quizSize);
            quizWordList.remove(word);
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


    public void handleSettingsBtn(ActionEvent actionEvent) {
        Main.showSettingsPopup();
    }
}
