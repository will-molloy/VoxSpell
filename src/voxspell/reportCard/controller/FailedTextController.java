package voxspell.reportCard.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by will on 14/10/16.
 */
public class FailedTextController implements Initializable {

    @FXML
    private Text failedTextView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> failed = new ArrayList<>();
        failed.add("That's Unfortunate");
        failed.add("Better Luck Next Time!");
        failed.add("You Have Been Unsuccessful");
        Collections.shuffle(failed);
        failedTextView.setText(failed.get(0));
    }

    public String getText() {
        return failedTextView.getText();
    }
}
