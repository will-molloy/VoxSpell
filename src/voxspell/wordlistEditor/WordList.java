package voxspell.wordlistEditor;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

/**
 * A WordList contains a list of words and points to the next word list (to maintain a level system).
 *
 * @author Will Molloy
 */
public class WordList implements Iterator {

    private List<Word> words = new ArrayList<>();
    private String name;
    private WordList nextList;

    public WordList(String name) {
        this.name = name;
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void setNextList(WordList wordList) {
        this.nextList = wordList;
    }

    public int size() {
        return words.size();
    }

    @Override
    public String toString() {
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

    public List<Word> wordList() {
        return words;
    }

}
