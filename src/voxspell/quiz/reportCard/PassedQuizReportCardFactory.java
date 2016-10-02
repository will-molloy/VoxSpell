package voxspell.quiz.reportCard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import voxspell.Main;

/**
 * Concrete factory for getting the 'Passed Quiz Report Card' scene.
 * <p>
 * Also controller for Passed_Quiz_Report - breaks SRP but rather have 1 class than a 10 line one.
 *
 * @author Will Molloy.
 */
public class PassedQuizReportCardFactory extends ReportCardFactory {

    @Override
    public void showScene() {
        Main.showReportCard();
    }

    @FXML
    private void handleNextLevelBtn(ActionEvent actionEvent) {
        Main.newQuizLevel(++level);
    }


}
