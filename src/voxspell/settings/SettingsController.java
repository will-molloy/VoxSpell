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





}
