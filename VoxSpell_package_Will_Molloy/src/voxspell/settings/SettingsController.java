package voxspell.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

    /**
     * Save the users settings on confirmation and set the new settings.
     */
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

    /**
     * Init the scene - create the dropdowns.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createVoiceDropDown();
        createBackgroundDropDown();
    }

    /**
     * Creates the voice drop down using the enumerated Voice type along with Voice list cell to display country flags.
     */
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

    /**
     * Creates the background drop down using the background enumerated type and list cell for larger font size.
     */
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


}
