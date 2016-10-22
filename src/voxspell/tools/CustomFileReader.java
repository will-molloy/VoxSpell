package voxspell.tools;

import voxspell.wordlistEditor.Word;
import voxspell.wordlistEditor.WordList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Contains a few methods to read word list / statistic files.
 *
 * @author Karim Cisse
 * @author Will Molloy
 */
public class CustomFileReader {

    protected Scanner scanner;
    protected BufferedWriter bufferedWriter;

    public List<WordList> readWordListFileIntoList(File file) {
        List<WordList> wordLists = new ArrayList<>();
        WordList wordList = null;
        try {
            scanner = new Scanner(new FileReader(file)); // need scanner hasNext()
            String line;

            while ((line = scannerReadLine()) != null) {
                // word and definition are seperated by a tab within the hidden file.
                String[] wordAndDef = line.split("\\t+");

                // Found next category OR eof, copy over the wordlist
                if (line.startsWith("%")) {
                    if (wordList != null) { // null check for first iteration
                        wordLists.add(wordList);
                    }
                    wordList = new WordList(line.substring(1, line.length())); // set name of wordList
                } else {
                    Word word = createWord(wordAndDef);
                    wordList.addWord(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wordList != null) { // null check if file is empty
            wordLists.add(wordList); // add final list
        }
        return wordLists;
    }

    private Word createWord(String[] wordAndDef) {
        Word word = new Word(wordAndDef[0]);
        if (wordAndDef.length > 1) { // definition detected
            word.setDefinition(wordAndDef[1]);
        }
        return word;
    }

    protected String scannerReadLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    public void removeWordListFromFile(String wordListTitle, File wordListFile) {
        try {
            File tempFile = new File(".wordListCopy");
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile, false));
            scanner = new Scanner(new FileReader(wordListFile));
            String line;
            outer:
            {
                while ((line = scannerReadLine()) != null) {
                    if (line.equals("%" + wordListTitle)) {
                        // Found category to remove, don't copy it to the temp file
                        while ((line = scannerReadLine()) != null) {
                            // Found next category OR eof, can stop not copying
                            if (line.startsWith("%")) {
                                break;
                            } else if (!scanner.hasNext()) {
                                break outer; // EOF, nothing to write to file
                            }
                        }
                    }
                    // Copy over lines from original file to temp file
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.flush();

            // Rename files
            tempFile.renameTo(wordListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWordListToFile(WordList wordList, File file) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write("%" + wordList.toString());
            bufferedWriter.newLine();
            printWordList(wordList);

            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printWordList(WordList wordList) throws IOException {
        for (Word word : wordList.wordList()) {
            bufferedWriter.write(word + "\t" + word.getDefinition());
            bufferedWriter.newLine();
        }
    }

    public void syncWordListDataWithFile(List<WordList> wordLists, File wordListFile) {
        try {
            // Overwrite wordlist file with data
            bufferedWriter = new BufferedWriter(new FileWriter(wordListFile, false));
            for (WordList wordList : wordLists) {
                bufferedWriter.write("%" + wordList.toString());
                bufferedWriter.newLine();
                printWordList(wordList);
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected int parseInt(String s) {
        return Integer.parseInt(s);
    }

}
