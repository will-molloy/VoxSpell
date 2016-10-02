package voxspell.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Contains a few methods to read files for statistics/words from the wordlist.
 *
 * @author Karim Cisse
 * @author Will Molloy
 */
public class CustomFileReader {

    private static final String WORD_LIST_FILE_NAME = "NZCER-spelling-lists.txt";

    private BufferedReader bufferedReader;

    /**
     * Reads a set of 10 words from the wordlist file based on the level provided.
     *
     * @author Karim Cisse
     */
    public ArrayList<String> getWordList(int level) {
        ArrayList<String> wordList = null;
        try {
            wordList = readInWords(level);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> returnWords = new ArrayList<>();
        int wordsNum = wordList.size();

        // the condition in the for loop allows for 10 words or less than 10 if
        // there are less than 10 words in the list
        for (int i = 0; i < (wordsNum) && (i < 10); i++) {
            int randomNum = (int) (Math.random() * wordList.size());
            returnWords.add(wordList.get(randomNum));
            wordList.remove(randomNum);
        }
        return returnWords;
    }

    /**
     * Read words in from wordlist file.
     *
     * @author Karim Cisse - implemented logic
     * @author Will Molloy - changed to user buffered readers
     */
    private ArrayList<String> readInWords(int level) throws IOException {

        ArrayList<String> wordList = new ArrayList<>();
        String levelID = "%Level " + level;

        // The file is read into standard in
        String temp;
        bufferedReader = new BufferedReader(new FileReader(WORD_LIST_FILE_NAME));

        while ((temp = bufferedReader.readLine()) != null) {

            // a check is performed to see whether the desired level words have
            // been reached
            if (temp.equals(levelID)) {
                levelID = "%Level " + (level + 1);
                while ((temp = bufferedReader.readLine()) != null) {

                    // when the the next level words in the list is reached it
                    // stops adding words to the list
                    if (temp.equals(levelID)) {
                        break;
                    }
                    wordList.add(temp);
                }
            }
        }
        bufferedReader.close();

        return wordList;
    }

    /**
     * Appends a word to a file.
     *
     * @author Will Molloy
     */
    public void appendWordToFile(String word, File file) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(word);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads words line by line from a file into a HashSet.
     *
     * @author Will Molloy
     */
    public void readFileByLineIntoSet(File file, HashSet<String> words) {
        String word;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((word = bufferedReader.readLine()) != null) {
                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a count of lines containing 'word' in 'file'
     *
     * @author Will Molloy
     */
    public int getWordCountFromFile(String word, File file) {
        String temp;
        int count = 0;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((temp = bufferedReader.readLine()) != null) {
                if (temp.equals(word)) {
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

}
