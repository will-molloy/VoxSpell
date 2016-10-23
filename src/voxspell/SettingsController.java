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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Settings popup.
 *
 * @author Will Molloy
 */
public class SettingsController implements Initializable {


    private Image UK_Icon = new Image(Main.class.getResourceAsStream("media/images/Gbr_Flag.png"));
    private Image US_Icon = new Image(Main.class.getResourceAsStream("media/images/US_Flag.png"));
    private Image NZ_Icon = new Image(Main.class.getResourceAsStream("media/images/US_Flag.png"));

    @FXML
    private ComboBox<String> voiceDropDown;
    @FXML
    private ComboBox<MainMenuBackground> mainMenuBackgroundDropDown;

    @FXML
    private void handleConfirmBtn(ActionEvent actionEvent) {
        String voice = voiceDropDown.getSelectionModel().getSelectedItem();
        TextToSpeech.setVoice(voice);

        MainMenuBackground background = mainMenuBackgroundDropDown.getSelectionModel().getSelectedItem();
        Main.setBackground(background);
        Main.hidePopup();
    }

    @FXML
    private void handleCancelBtn(ActionEvent actionEvent) {
        Main.hidePopup();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createVoiceDropDown();
        createBackgroundDropDown();
    }

    private void createVoiceDropDown() {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "US English",
                        "UK English",
                        "NZ English"
                );
        voiceDropDown.setItems(options);
        voiceDropDown.getSelectionModel().select(0); // select from hidden file.

        voiceDropDown.setCellFactory(c -> new DropDownListcell());
        voiceDropDown.setButtonCell(new DropDownListcell());
    }

    private Image getImageForItem(String item) {
        switch (item) {
            case "US English":
                return US_Icon;
            case "UK English":
                return UK_Icon;
            case "NZ English":
                return NZ_Icon;
        }
        return null;
    }

    class DropDownListcell extends ListCell<String> {
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if (item != null) {
                ImageView imageView = new ImageView(getImageForItem(item));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                setGraphic(imageView);
                setText(item);
                setFont(this.getFont().font(this.getFont().getName(), 32)); // Font size
            }
        }
    }

    private void createBackgroundDropDown() {
        ObservableList<MainMenuBackground> options =
                FXCollections.observableArrayList(
                        MainMenuBackground.Autumn,
                        MainMenuBackground.Summer,
                        MainMenuBackground.Spring,
                        MainMenuBackground.Winter
                );
        mainMenuBackgroundDropDown.setItems(options);
        mainMenuBackgroundDropDown.getSelectionModel().select(0); // select from hidden file.
    }
}
