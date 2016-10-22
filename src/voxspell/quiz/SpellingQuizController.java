package voxspell.quiz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.dailyChallenges.DailyChallengeGUIController;
import voxspell.reportCard.FailedQuizReportCardFactory;
import voxspell.reportCard.PassedQuizReportCardFactory;
import voxspell.reportCard.ReportCardFactory;
import voxspell.reportCard.controller.ReportCardController;
import voxspell.statistics.StatisticsFileHandler;
import voxspell.tools.TextToSpeech;
import voxspell.wordlistEditor.Word;
import voxspell.wordlistEditor.WordList;
import voxspell.wordlistEditor.WordListEditorController;

import java.io.IOException;
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

    // StatisticsFileHandler object for saving stats
    private StatisticsFileHandler statisticsFileHandler = new StatisticsFileHandler();
    // DailyChallenge - for updating any challenges
    private DailyChallengeGUIController dailyChallengeGUIController = new DailyChallengeGUIController();
    @FXML
    private ProgressBar quizProgressBar;
    private List<Word> quizWordList;
    private int wordNumber;
    private int wordsCorrectFirstAttempt;
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

    // Images
    @FXML
  //  private Parent imageHBox;

  //  private List<ImageView> images = new ArrayList<>();

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
        } else {
            /* final quiz  */
        }
    }

    private void resetFields() {
        clearGUI();
        resetGameLogic();
    }

    private void clearGUI() {
        // Add ImageViews inside imageHBox to the ArrayList of images
      //  images.addAll(imageHBox.getChildrenUnmodifiable().stream().filter(node -> node instanceof ImageView).map(node -> (ImageView) node).collect(Collectors.toList()));
        // Blank out all images
      //  for (ImageView imageView : images) {
      //      imageView.setImage(null);
      //  }
        // Reset text views
        categoryText.setText("");
        quizProgressBar.setProgress(0);
    }

    private void resetGameLogic() {
        wordsCorrectFirstAttempt = 0;
        wordFirstAttempts = new ArrayList<>();
    }

    public void continueSpellingQuiz() {
        // Quiz is finished when the quizWordList is empty
        if (quizWordList.size() > 0) {
            word = quizWordList.get(0);
            System.out.println(word);
            wordNumber = quizSize + 1 - quizWordList.size();

            textToSpeech.readSentence("Please spell ... " + word);

        } else { /* Quiz Completed */
            if (wordsCorrectFirstAttempt >= 9 || wordsCorrectFirstAttempt == quizSize) {
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
                controller.setValues(quizWordListCopy, wordFirstAttempts, categoryWordList);
                controller.generateScene();
            });
            clearGUI();
            dailyChallengeGUIController.updateChallenge(1,1);
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

            if (attempt.equals(word.toString())) { /* Correct */

                wordFirstAttempts.add(attempt);
                wordsCorrectFirstAttempt++;
            //    images.get(imageIndex).setImage(wordCorrect);

                textToSpeech.readSentenceAndContinueSpellingQuiz("Correct", this);

                statisticsFileHandler.writeStatistic(word.toString(), true, categoryText.getText());

            } else { /* Incorrect */
                wordFirstAttempts.add(attempt);
             //   images.get(imageIndex).setImage(wordIncorrect);
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
        try {
            Parent settingsRoot = FXMLLoader.load(Main.class.getResource("fxml/Settings.fxml"));
            Scene scene = new Scene(settingsRoot);
            Main.showPopup(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
