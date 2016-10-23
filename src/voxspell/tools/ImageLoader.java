package voxspell.tools;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Contains various methods for loading background/button etc images.
 *
 * @author Will Molloy
 */
public class ImageLoader {

    public void load40x40ImageForBtn(Button button, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        button.setGraphic(imageView);
    }
}
