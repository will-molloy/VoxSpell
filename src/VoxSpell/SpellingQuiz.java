package VoxSpell;

import VoxSpell.tools.CustomFileReader;
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
    private Text levelText;

    // Game logic
    private int level, wordsCorrectFirstAttempt, wordAttempt;
    private boolean firstAttempt;
    private List<String> wordList;

    // Tools
    private CustomFileReader fileReader = new CustomFileReader();

    public void newQuiz() {
        level = promptUserForInitialLevel();
        levelText.setText("Level " + level);

        resetFields();
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
        wordsCorrectFirstAttempt = 0;
        wordAttempt = 0;
        firstAttempt = true;
    }

    private void readWordsFromFile() {
        wordList = fileReader.getWordList(level);
    }

    private void continueSpellingQuiz() {
    }

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    private void handleDefinitionBtn(ActionEvent actionEvent) {
    }

    @FXML
    private void handleRepeatWordBtn(ActionEvent actionEvent) {
    }

    @FXML
    private void handleEnterWordBtn(ActionEvent actionEvent) {
    }


}
