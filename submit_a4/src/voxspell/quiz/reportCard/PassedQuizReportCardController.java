package voxspell.reportCard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the report card shown when the user passes a spelling quiz.
 */
public class PassedQuizReportCardController extends ReportCardController {

    @FXML
    public Button viewVideoBtn;
    @FXML
    private Text wellDoneTextView;

    public void createGUI() {
        List<String> congratulations = new ArrayList<>();
        congratulations.add("Good Job!");
        congratulations.add("Well Done!");
        congratulations.add("Congratulations!");
        Collections.shuffle(congratulations);
        wellDoneTextView.setText(congratulations.get(0));
    }


}
