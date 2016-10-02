package Voxspell;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the SpellingQuiz.
 *
 * @author Will Molloy
 */
public class SpellingQuiz implements Initializable {

    @FXML
    private Text wordsToSpellText, levelText;
    @FXML
    private Button backBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
