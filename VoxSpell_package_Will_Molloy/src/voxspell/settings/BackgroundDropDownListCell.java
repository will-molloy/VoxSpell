package voxspell.settings;

import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

/**
 * List Cell for the Background dropdown within the settings scene.
 *
 * @author Will Molloy
 */
public class BackgroundDropDownListCell extends ListCell<MainMenuBackground> {
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