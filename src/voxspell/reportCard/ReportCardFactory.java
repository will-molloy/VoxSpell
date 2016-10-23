package voxspell.reportCard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import voxspell.Main;
import voxspell.reportCard.controller.ReportCardController;

import java.io.IOException;

/**
 * Loads the FXML for the ReportCard shown after completing a quiz, also retrieves the controller from the FXML.
 *
 * @author Will Molloy
 */
public abstract class ReportCardFactory {

    public final ReportCardController getControllerAndShowScene() {
        FXMLLoader loader = new FXMLLoader();
        showSceneAndLoadFXML(loader); // reference to loader passed
        return loader.getController();
    }

    private void showSceneAndLoadFXML(FXMLLoader loader) {
        String fxmlFileName = getFXML();
        Parent root = null;
        try {
            root = loader.load(getClass().getResource(fxmlFileName).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene reportCard = new Scene(root);
        Main.setAndShowScene(reportCard);
    }

    abstract String getFXML();

}
