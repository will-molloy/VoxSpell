package voxspell.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import voxspell.Main;
import voxspell.tools.TextToSpeech;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Settings popup.
 *
 * @author Will Molloy
 */
public class SettingsController implements Initializable {

    @FXML
    private ComboBox<Voice> voiceDropDown;
    @FXML
    private ComboBox<MainMenuBackground> mainMenuBackgroundDropDown;

    private SettingsFileHandler settingsFileHandler = new SettingsFileHandler();

    @FXML
    private void handleConfirmBtn(ActionEvent actionEvent) {
        Voice voice = voiceDropDown.getSelectionModel().getSelectedItem();
        TextToSpeech.setVoice(voice);

        MainMenuBackground background = mainMenuBackgroundDropDown.getSelectionModel().getSelectedItem();
        Main.setBackground(background);

        settingsFileHandler.saveSettings(voice, background);
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
        ObservableList<Voice> options =
                FXCollections.observableArrayList(
                        Voice.US,
                        Voice.UK,
                        Voice.NZ
                );
        voiceDropDown.setItems(options);
        voiceDropDown.getSelectionModel().select(settingsFileHandler.getSettingsVoice());

        voiceDropDown.setCellFactory(c -> new VoiceDropDownListCell());
        voiceDropDown.setButtonCell(new VoiceDropDownListCell());
    }

    private void createBackgroundDropDown() {
        ObservableList<MainMenuBackground> options =
                FXCollections.observableArrayList(
                        MainMenuBackground.AUTUMN,
                        MainMenuBackground.SUMMER,
                        MainMenuBackground.SPRING,
                        MainMenuBackground.WINTER
                );
        mainMenuBackgroundDropDown.setItems(options);
        mainMenuBackgroundDropDown.getSelectionModel().select(settingsFileHandler.getSettingsBackGround());

        mainMenuBackgroundDropDown.setCellFactory(c -> new BackgroundDropDownListCell());
        mainMenuBackgroundDropDown.setButtonCell(new BackgroundDropDownListCell());
    }

    /**
     * The ListCell for the voice drop down within the settings scene.
     */
    private class VoiceDropDownListCell extends ListCell<Voice> {

        private Image UK_Icon = new Image(Main.class.getResourceAsStream("media/images/Gbr_Flag.png"));
        private Image US_Icon = new Image(Main.class.getResourceAsStream("media/images/US_Flag.png"));
        private Image NZ_Icon = new Image(Main.class.getResourceAsStream("media/images/US_Flag.png"));

        protected void updateItem(Voice item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if (item != null) {
                ImageView imageView = new ImageView(getImageForItem(item));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                setGraphic(imageView);
                setText(item.getDisplay());
                setFont(Font.font(this.getFont().getName(), 32)); // Font size
            }
        }

        private Image getImageForItem(Voice item) {
            switch (item) {
                case US:
                    return US_Icon;
                case UK:
                    return UK_Icon;
                case NZ:
                    return NZ_Icon;
            }
            return null;
        }
    }

    /**
     * List Cell for the Background dropdown within the settings scene.
     */
    private class BackgroundDropDownListCell extends ListCell<MainMenuBackground> {
        protected void updateItem(MainMenuBackground item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if (item != null) {
                setText(item.getDisplay());
                setFont(Font.font(this.getFont().getName(), 32)); // Font size
            }
        }
    }

}
