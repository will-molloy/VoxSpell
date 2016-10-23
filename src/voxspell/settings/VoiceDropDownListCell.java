package voxspell.settings;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import voxspell.Main;

/**
 * The ListCell for the voice drop down within the settings scene.
 *
 * @author Will Molloy
 */
public class VoiceDropDownListCell extends ListCell<Voice> {

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
            setText(item.toString());
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