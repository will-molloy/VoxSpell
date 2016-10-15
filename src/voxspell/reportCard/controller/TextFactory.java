package voxspell.reportCard.controller;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Returns various Text views with different styles.
 *
 * @author Will Molloy
 */
public class TextFactory {

    public static Text getPrefixOrSuffixText(String commonPrefixOrSuffix) {
        Text text = new Text();
        text.setText(commonPrefixOrSuffix != null? commonPrefixOrSuffix : "");
        text.setFont(new Font(22));
        return text;
    }

    public static Text getDeltaText(){
        Text text = new Text();
        text.setUnderline(true);
        text.setFill(Color.RED);
        text.setFont(new Font(22));
        return text;
    }
}
