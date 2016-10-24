package voxspell.statistics;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Uses mouse listeners to display a 'HoverPane' when the user places their mouse over a node on a linegraph.
 * <p>
 * Base code from here: https://gist.github.com/jewelsea/4681797
 *
 * Has been edited so corner nodes are visible.
 *
 * @author Will Molloy
 */
public class LineGraphNodeHoverPane extends StackPane {

    LineGraphNodeHoverPane(String text, int topMargin, int sideMargin) {
        setPrefSize(15, 15);

        final Label label = createDataThresholdLabel(text);

        setOnMouseEntered(mouseEvent -> {
            getChildren().setAll(label);
            setCursor(Cursor.NONE);
            toFront();
            setMargin(label, new Insets(topMargin,sideMargin,0,0)); // Margin added to label to make 'corner' nodes visible
        });
        setOnMouseExited(mouseEvent -> {
            getChildren().clear();
            setCursor(Cursor.CROSSHAIR);
        });
    }

    private Label createDataThresholdLabel(String text) {
        final Label label = new Label(text);
        label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
        label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        label.setTextFill(Color.BLACK);
        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }


}
