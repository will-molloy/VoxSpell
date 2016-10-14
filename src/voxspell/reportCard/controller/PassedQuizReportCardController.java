package voxspell.reportCard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import voxspell.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the report card shown when the user passes a spelling quiz.
 */
public class PassedQuizReportCardController extends ReportCardController {

    @FXML
    private Text wellDoneTextView;

    @FXML
    private Button proceedToLevelBtn;

    public void createGUI() {
        showWellDoneText();
        setProceedToLevelTextForBtn();
    }

    private void showWellDoneText() {
        List<String> congratulations = new ArrayList<>();
        congratulations.add("Good Job!");
        congratulations.add("Well Done!");
        congratulations.add("Congratulations!");
        Collections.shuffle(congratulations);
        wellDoneTextView.setText(congratulations.get(0));
    }

    private void setProceedToLevelTextForBtn() {
        proceedToLevelBtn.setText("Proceed to " + wordList.next().toString());
    }

    @FXML
    private void handleNextLevelBtn(ActionEvent actionEvent) {
        Main.newSpellingQuiz(wordList.next().toString());
    }


}
