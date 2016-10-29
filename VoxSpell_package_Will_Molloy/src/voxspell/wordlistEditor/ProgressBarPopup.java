package voxspell.wordlistEditor;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Contains a ProgressBar for running background tasks.
 * <p>
 * Found the base code here: http://stackoverflow.com/a/29628430/6122976
 *
 * @author Will Molloy
 */
public class ProgressBarPopup {
    private final Stage popup;
    private final ProgressBar progressBar = new ProgressBar();
    private final ProgressIndicator indicator = new ProgressIndicator();

    public ProgressBarPopup() {
        // Popup GUI
        popup = new Stage();
        popup.initStyle(StageStyle.UTILITY);
        popup.setResizable(false);
        popup.initModality(Modality.APPLICATION_MODAL);
        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(progressBar, indicator);

        // Progress bar
        final Label label = new Label();
        label.setText("Generating definitions. . .");
        progressBar.setProgress(-1F);
        indicator.setProgress(-1F);

        // Show popup
        Scene scene = new Scene(hb);
        popup.setScene(scene);
    }

    public void activateProgressBar(final Task<?> task) {
        progressBar.progressProperty().bind(task.progressProperty());
        indicator.progressProperty().bind(task.progressProperty());
        popup.show();
    }

    public Stage getPopup() {
        return popup;
    }
}
