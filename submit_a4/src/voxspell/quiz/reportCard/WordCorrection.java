package voxspell.reportCard;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents the correct spelling of a word and the users attempt.
 *
 * @author Will
 */
public class WordCorrection {
    private SimpleStringProperty word;
    private SimpleStringProperty attempt;

    public WordCorrection(String word, String attempt) {
        this.word = new SimpleStringProperty(word);
        this.attempt = new SimpleStringProperty(attempt);
    }

    public String getWord() {
        return word.get();
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public String getAttempt() {
        return attempt.get();
    }

    public void setAttempt(String attempt) {
        this.attempt.set(attempt);
    }
}

