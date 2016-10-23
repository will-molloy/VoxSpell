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

    public void loadSquareImageForBtn(Button button, Image image, int size) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        button.setGraphic(imageView);
    }
}
