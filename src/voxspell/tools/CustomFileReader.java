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

    private Scanner scanner;
    private BufferedWriter bufferedWriter;

    public List<WordList> readWordListFileIntoList(File file) {
        List<WordList> wordLists = new ArrayList<>();
        try {
            scanner = new Scanner(new FileReader(file)); // need scanner hasNext()
            String line;
            WordList wordList = null;

            while ((line = scannerReadLine()) != null) {
                // word and definition are seperated by a tab within the hidden file.
                String[] wordAndDef = line.split("\\t+");

                // Found next category OR eof, copy over the wordlist
                if (line.startsWith("%") || !scanner.hasNextLine()) {
                    if (wordList != null) { // null check for first iteration
                        wordLists.add(wordList);
                    }
                    wordList = new WordList(line.substring(1, line.length())); // set name of wordList
                } else {
                    Word word = new Word(wordAndDef[0]);
                    if (wordAndDef.length > 1) { // definition detected
                        word.setDefinition(wordAndDef[1]);
                    }
                    wordList.addWord(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordLists;
    }

    private String scannerReadLine() {
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
            while ((line = scannerReadLine()) != null) {
                readCategory:
                {
                    if (line.equals("%" + wordListTitle)) {
                        // Found category to remove, don't copy it to the temp file
                        while ((line = scannerReadLine()) != null) {
                            // Found next category OR eof, can stop not copying
                            if (line.startsWith("%") || !scanner.hasNextLine()) {
                                break readCategory;
                            }
                        }
                    }
                }
                // Copy over lines from original file to temp file
                bufferedWriter.write(line);
                bufferedWriter.newLine();
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
}
