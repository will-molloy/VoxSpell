package voxspell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import voxspell.tools.TextToSpeech;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Settings popup.
 *
 * @author Will Molloy
 */
public class SettingsController implements Initializable {

    private Image UK_Icon = new Image(new File("src/media/images/Australia-Flag-icon.png").toURI().toString());
    private Image US_Icon = new Image(new File("src/media/images/Australia-Flag-icon.png").toURI().toString());
    @FXML
    private ComboBox<String> voiceDropDown;

    @FXML
    private void handleConfirmBtn(ActionEvent actionEvent) {
        String voice = voiceDropDown.getSelectionModel().getSelectedItem();
        TextToSpeech.setVoice(voice);
        Main.hidePopup();
    }

    @FXML
    private void handleCancelBtn(ActionEvent actionEvent) {
        Main.hidePopup();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "US English",
                        "UK English"
                );
        voiceDropDown.setItems(options);
        voiceDropDown.getSelectionModel().select(0);

        voiceDropDown.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setFont(this.getFont().font(this.getFont().getName(), 32)); // Font size
                            ImageView iconImageView = new ImageView(UK_Icon);
                            iconImageView.setFitHeight(32);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        });

    }
}
