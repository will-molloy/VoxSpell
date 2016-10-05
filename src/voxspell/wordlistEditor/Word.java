package voxspell.wordlistEditor;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a Word.
 * Words have a definition and a Name.
 *
 * @author Will Molloy
 */
public class Word implements Comparable<Word> {

    private SimpleStringProperty name;
    private SimpleStringProperty definition;

    public Word(String name) {
        this.name = new SimpleStringProperty(name);
        this.definition = new SimpleStringProperty("");
    }

    @Override
    public String toString() {
        return name.get();
    }

    /* Getters and Setters are for adapting to the Tables shown in the Word List Editor */

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDefinition() {
        return definition.get();
    }

    public void setDefinition(String definition) {
        this.definition.set(definition);
    }

    @Override
    public int compareTo(Word o) {
        return name.get().toLowerCase().compareTo(o.name.get().toLowerCase());
    }
}
