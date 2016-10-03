package voxspell.quiz.reportCard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import voxspell.Main;

/**
 * Represents a report card once finishing a spelling quiz.
 *
 * @author Will Molloy
 */
public abstract class ReportCardFactory {

    public final ReportCardController getControllerAndShowScene() {
        FXMLLoader loader = new FXMLLoader();
        Parent root = getRootAndLoadLoader(loader);
        ReportCardController controller = loader.getController();

        Scene reportCard = new Scene(root);
        Main.setAndShowScene(reportCard);

        return controller;
    }

    protected abstract Parent getRootAndLoadLoader(FXMLLoader loader);
}
