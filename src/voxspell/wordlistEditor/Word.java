package voxspell.wordlistEditor;

/**
 * Represents a Word.
 * Words have a definition and a Name.
 *
 * @author Will Molloy
 */
public class Word {

    private String name;
    private String definition;

    public Word(String name, String definition){
        this.name = name;
        this.definition = definition;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getDefinition(){
        return definition;
    }

}
