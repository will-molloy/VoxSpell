package voxspell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private Image UK_Icon = new Image(new File("src/media/images/Gbr_Flag.png").toURI().toString());
    private Image US_Icon = new Image(new File("src/media/images/US_Flag.png").toURI().toString());

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

        voiceDropDown.setCellFactory(c -> new DropDownListcell());
        voiceDropDown.setButtonCell(new DropDownListcell());
    }

    class DropDownListcell extends ListCell<String> {
        protected void updateItem(String item, boolean empty){
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if(item!=null){
                ImageView imageView = new ImageView(getImageForItem(item));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                setGraphic(imageView);
                setText(item);
                setFont(this.getFont().font(this.getFont().getName(), 32)); // Font size
            }
        }
    }

    private Image getImageForItem(String item) {
        switch (item){
            case "US English":
                return US_Icon;
            case "UK English":
                return UK_Icon;
        }
        return null; //?
    }
}