package voxspell.quiz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.dailyChallenges.ChallengeType;
import voxspell.dailyChallenges.DailyChallengeGUIController;
import voxspell.reportCard.FailedQuizReportCardFactory;
import voxspell.reportCard.PassedQuizReportCardFactory;
import voxspell.reportCard.ReportCardFactory;
import voxspell.reportCard.controller.ReportCardController;
import voxspell.statistics.StatisticsFileHandler;
import voxspell.tools.ImageLoader;
import voxspell.tools.TextToSpeech;
import voxspell.wordlistEditor.Word;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the SpellingQuizController.
 *
 * @author Karim Cisse - implemented Spelling Quiz logic
 * @author Will Molloy - convert to JavaFX, adding progress bar, using WordList/Word object.
 */
public class SpellingQuizController implements Initializable {

    // Game logic
    private static List<WordList> wordLists;
    private static WordList categoryWordList;
    // Reportcard shown after quiz
    private static ReportCardFactory reportCardFactory;
    private final double QUIZ_PASS_THRESHOLD = 0.8; // % required to pass quiz
    @FXML
    private Button definitionBtn, repeatBtn, backBtn, settingsBtn;
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
    private Image
            settingsIcon = new Image(Main.class.getResourceAsStream("media/images/main_menu/settings_icon.png")),
            definitionIcon = new Image(Main.class.getResourceAsStream("media/images/quiz/definition_icon.png")),
            repeatIcon = new Image(Main.class.getResourceAsStream("media/images/quiz/repeat_icon.png")),
            backIcon = new Image(Main.class.getResourceAsStream("media/images/quiz/back_icon.png"));

    /**
     * Loads a popup prompt for selecting a category/word list.
     * Returns the name of the chosen word list or null if cancelled.
     */
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

    /**
     * Begins a new quiz with the given category:
     * Loads a quiz of the categories size or size of 10 if the category size exceeds 10.
     * Generates random words and begins the quiz.
     */
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

    /**
     * Continues the spelling quiz:
     * Determines if the quiz is completed or not: if it is loads the appropriate report card
     * otherwise prompts the user to spell the next word.
     */
    public void continueSpellingQuiz() {
        // Quiz is finished when the quizWordList is empty
        if (quizWordList.size() > 0) {
            word = quizWordList.get(0);
            wordNumber = quizSize + 1 - quizWordList.size();

            textToSpeech.readSentence("Please spell ... " + word);

        } else { /* Quiz Completed */
            bestStreak = Math.max(currentStreak, bestStreak);
            final long elapsedTimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
            if (wordsCorrect >= QUIZ_PASS_THRESHOLD * quizSize) {
                /* Passed */
                reportCardFactory = new PassedQuizReportCardFactory();
                statisticsFileHandler.writeQuizStatistic(categoryText.getText(), bestStreak + "", elapsedTimeSeconds + "");
            } else {
                /* Failed */
                reportCardFactory = new FailedQuizReportCardFactory();
                // Elapsed time is only updated on completing a quiz - otherwise can be cheated
                statisticsFileHandler.writeQuizStatistic(categoryText.getText(), bestStreak + "", Integer.MAX_VALUE + "");
            }
            /*
             * ContinueSpellingQuiz() is called by a SwingWorker (not from a JavaFX thread)
             * and need to run this code on an FX thread. Platform.runLater() achieves this.
             */
            Platform.runLater(() -> {
                ReportCardController controller = reportCardFactory.getControllerAndShowScene();
                controller.setValues(quizWordListCopy, wordFirstAttempts, categoryWordList, elapsedTimeSeconds, bestStreak);
                controller.generateScene();
            });
            clearGUI();
            // Update the 'quiz completed' challenge
            dailyChallengeGUIController.updateChallenge(ChallengeType.QUIZES_COMPLETED, 1);
        }
    }

    /**
     * Handles both the enter word button AND pressing 'enter' within the word entry field.
     */
    @FXML
    private void handleEnterWordBtn(ActionEvent actionEvent) {
        checkInputWord();
    }

    /**
     * Checks the input of the users attempt versus the actual word.
     * Based on this updates the progress bar/texttospeech/statistics appropriately.
     */
    private void checkInputWord() {
        String attempt = wordEntryField.getText();
        wordEntryField.setText("");

        if (quizWordList.size() > 0) {

            if (attempt.equals(word.toString())) {
                /* Correct */
                currentStreak++;
                wordFirstAttempts.add(attempt);
                wordsCorrect++;
                textToSpeech.readSentenceAndContinueSpellingQuiz("Correct", this);
                Main.setSpellingQuizCssId("green-progress");

                statisticsFileHandler.writeStatistic(word.toString(), true, categoryText.getText());

            } else {
                /* Incorrect */
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak;
                }
                currentStreak = 0;
                wordFirstAttempts.add(attempt);
                textToSpeech.readSentenceAndContinueSpellingQuiz("Incorrect", this);
                Main.setSpellingQuizCssId("red-progress");

                statisticsFileHandler.writeStatistic(word.toString(), false, categoryText.getText());
            }
            quizProgressBar.setProgress(wordNumber * 1.0 / quizSize);
            quizWordList.remove(word);
        }
    }

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Abort Quiz");
        alert.setHeaderText("Return to the Main Menu?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            clearGUI();
            Main.showMainMenu();
        }
    }

    @FXML
    private void handleDefinitionBtn(ActionEvent actionEvent) {
        String definition = word.getDefinition();
        textToSpeech.readSentence(definition);
    }

    @FXML
    private void handleRepeatWordBtn(ActionEvent actionEvent) {
        textToSpeech.readSentenceSlowly(word.toString());
    }

    @FXML
    private void handleSettingsBtn(ActionEvent actionEvent) {
        Main.showSettingsPopup();
    }

    /**
     * Initialises the scene:
     * loads icons for the buttons.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.loadSquareImageForBtn(backBtn, backIcon, 40);
        imageLoader.loadSquareImageForBtn(repeatBtn, repeatIcon, 40);
        imageLoader.loadSquareImageForBtn(definitionBtn, definitionIcon, 40);
        imageLoader.loadSquareImageForBtn(settingsBtn, settingsIcon, 40);
    }
}
