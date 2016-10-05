package voxspell.wordlistEditor;

import java.util.Iterator;
import java.util.List;

/**
 * A WordList contains a list of words and points to the next word list (to maintain a level system).
 *
 * @author Will Molloy
 */
public class WordList implements Iterator{

    private List<Word> words;
    private String name;
    private WordList nextList;

    public WordList(List<Word> words, String name, WordList nextList){
        this.words = words;
        this.name = name;
        this.nextList = nextList;
    }

    public int size(){
        return words.size();
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean hasNext() {
        return nextList != null;
    }

    @Override
    public WordList next() {
        return nextList;
    }
}
