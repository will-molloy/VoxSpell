package voxspell.quiz.reportCard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import voxspell.Main;

/**
 * Created by will on 3/10/16.
 */
public class PassedQuizReportCardController extends ReportCardController {

    public void setLevelText() {
        passedOrFailedLevelText.setText("Passed " + wordList.toString() + " !");
        proceedToLevelBtn.setText("Proceed to " + wordList.next().toString());
    }

    @FXML
    private void handleNextLevelBtn(ActionEvent actionEvent) {
        Main.newQuizLevel(wordList.next().toString());
    }

}
