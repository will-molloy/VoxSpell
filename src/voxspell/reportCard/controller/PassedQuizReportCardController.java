package voxspell.reportCard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import voxspell.Main;
import voxspell.tools.VideoPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the report card shown when the user passes a spelling quiz.
 * <p>
 * Extends the abstract report card controller class for implementation of the common elements between
 * failed and passed report cards.
 *
 * @author Will Molloy
 */
public class PassedQuizReportCardController extends ReportCardController {

    @FXML
    private Text wellDoneTextView;
    @FXML
    private Button proceedToNxtLevelBtn, rewardVideoBtn;

    /**
     * Implemented hook method: loads the images specific to this report card scene.
     */
    public void createSubClassGUI() {
        showWellDoneText();
        setProceedToLevelTextForBtn();
        imageLoader.loadSquareImageForBtn(rewardVideoBtn, videoIcon, 40);
        imageLoader.loadSquareImageForBtn(proceedToNxtLevelBtn, nextIcon, 40);
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
        proceedToNxtLevelBtn.setText("Proceed to " + wordList.next().toString());
    }

    @FXML
    private void handleProceedToNxtLevlBtn(ActionEvent actionEvent) {
        Main.newSpellingQuiz(wordList.next().toString());
    }

    @FXML
    private void handleRewardVideoBtn(ActionEvent actionEvent) {
        VideoPlayer.showVideo();
    }

}
