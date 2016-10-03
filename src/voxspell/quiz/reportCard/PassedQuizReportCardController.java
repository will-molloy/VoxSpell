package voxspell.quiz.reportCard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import voxspell.Main;

/**
 * Created by will on 3/10/16.
 */
public class PassedQuizReportCardController extends ReportCardController {


    public void setLevelText() {
        passedOrFailedLevelText.setText("Passed level " + level + "!");
    }

    @FXML
    private void handleNextLevelBtn(ActionEvent actionEvent) {
        Main.newQuizLevel(++level);
    }


}
