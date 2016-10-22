package voxspell.statistics;

import voxspell.tools.CustomFileReader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a statistics object,
 * writes statistics for a word: correct/incorrect count, associated category and todaysDate.
 * <p>
 * The statistic file format works as follows:
 * date (tab) 2016-10-15 (earliest date)
 * word (tab) correctSpellings (tab) incorrectSpellings (tab) category
 * word ..
 * ..
 * date (tab) 2016-10-14 (next date)
 * word ..
 * ..
 *
 * @author Will Molloy
 */
public class StatisticsFileHandler extends CustomFileReader {

    private static final String TEMP_FILE_NAME = ".tempStats", DATE_ID = "date";
    private static String todaysDate;
    private static File statsFile;
    private File tempFile = new File(TEMP_FILE_NAME);
    private String line;
    private String word;
    private boolean correct;
    private String category;
    private BufferedReader bufferedReader;
    // default file name
    private final String statsFileName = ".statistics";

    /**
     * Method to change the hidden statistics file name - for testing
     */
    public void setFileName(String fileName){
        statsFile = new File(fileName);
        makeHiddenFile(statsFile);
    }

    /**
     * Method to change the date - for testing
     */
    public void setDate(String date){
        todaysDate = date;
    }

    /**
     * Instantiates the statistics file handler - loading the hidden file and current date
     */
    public StatisticsFileHandler() {
        statsFile = new File(statsFileName);
        if (!statsFile.exists()) {
            makeHiddenFile(statsFile);
        }
        todaysDate = getCurrentDate();
    }

    private void makeHiddenFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * Records a statistic
     *
     * @param word     Word to be recorded
     * @param correct  true if word was correct, false if incorrect
     * @param category category the word belongs to
     */
    public void writeStatistic(String word, boolean correct, String category) {
        this.word = word;
        this.correct = correct;
        this.category = category;

        scanner = getScannerForStatFile();
        makeHiddenFile(tempFile);
        tempFile.deleteOnExit();

        if ((line = scannerReadLine()) == null) {
            /* statistic file has just been created */
            recordNewStatForToday();
        } else {
            do {
                String[] tokens = line.split("\\t");
                // read the stats file until a date is found
                if (tokens.length == 2 && tokens[0].equals(DATE_ID)) {

                    // if the date is today, find word
                    if (tokens[1].equals(todaysDate)) {
                        while ((line = scannerReadLine()) != null) {
                            tokens = line.split("\\t");

                            // found end of this days stats, word has not been recorded today
                            if (tokens[0].equals(DATE_ID)) {
                                recordNewWordForToday();
                                return;

                                // found word update its statistic
                            } else if (tokens[0].equals(word) && tokens[3].equals(category)) {
                                updateStatistic();
                                return;
                            }
                        }
                        // EOF reached without finding word
                        recordNewWordForToday();
                    } else {
                        /* no stats for today */
                        recordNewStatForToday();
                    }
                } else {
                    /* statistic file is empty - didn't begin with a date*/
                    recordNewStatForToday();
                }
            } while ((line = scannerReadLine()) != null);
        }
    }

    private void recordNewWordForToday() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile, false));
            scanner = getScannerForStatFile();

            // read over first line (todays date)
            scanner.nextLine();

            // Write new statistic
            writeNewStatisticAndRestOfFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeNewStatisticAndRestOfFile() throws IOException {
        // Write new statistic
        bufferedWriter.write(getDateFormatForFile());
        bufferedWriter.write(getNewStatistic());

        writeRestOfFile();
    }

    private String getNewStatistic() {
        int correct = this.correct ? 1 : 0;
        int incorrect = !this.correct ? 1 : 0;
        return getStatisticFileFormat(word, correct, incorrect, category);
    }

    private String getStatisticFileFormat(String word, int correct, int incorrect, String category) {
        return word + "\t" + correct + "\t" + incorrect + "\t" + category + "\n";
    }

    private String getDateFormatForFile() {
        return "date\t" + todaysDate + "\n";
    }

    private void writeRestOfFile() throws IOException {
        while ((line = scannerReadLine()) != null) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();

        // Rename file
        tempFile.renameTo(statsFile);
    }

    private void recordNewStatForToday() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile, false));
            scanner = getScannerForStatFile();

            // Write new statistic
            writeNewStatisticAndRestOfFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStatistic() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile, false));
            scanner = getScannerForStatFile();

            while ((line = scannerReadLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens[0].equals(word) && tokens[3].equals(category)) { // found word, update it

                    // extract existing statistic
                    int correct = parseInt(tokens[1]);
                    int incorrect = parseInt(tokens[2]);
                    if (this.correct) {
                        correct++;
                    } else {
                        incorrect++;
                    }

                    // update statistic
                    bufferedWriter.write(getStatisticFileFormat(word, correct, incorrect, category));
                    break;
                } else {
                    // write top of file
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
            writeRestOfFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Scanner getScannerForStatFile() {
        try {
            return new Scanner(new FileReader(statsFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the statistics for the given category.
     *
     * @return an integer array with 2 elements: [ correctCount, incorrectCount ] for the given category
     */
    public int[] getStatsForCategory(String category) {
        scanner = getScannerForStatFile();
        int correct = 0;
        int incorrect = 0;

        while ((line = scannerReadLine()) != null) {
            String[] tokens = line.split("\\t");
            // Tokens: word (tab) correctCount (tab) incorrectCount (tab) category
            if (tokens.length == 4 && tokens[3].equals(category)) {
                correct += parseInt(tokens[1]);
                incorrect += parseInt(tokens[2]);
            }
        }
        return new int[]{correct, incorrect};
    }

    private int parseInt(String s) {
        return Integer.parseInt(s);
    }

    /**
     * Returns the previous days of statistics (or all statistics if less than the given days..)
     *
     * @return a List of 2 element integer arrays: [ correctCount, incorrectCount] for each day of statistics
     * the List is ordered with the earliest day in index 0.
     * <p>
     * TODO: change so it gets previous days only if they preceed the dates by 1 day
     */
    public List<String[]> getPrevDayStats(int days) {
        List<String[]> stats = new ArrayList<>();
        scanner = getScannerForStatFile();
        int correct = 0;
        int incorrect = 0;
        String date;
        String[] tokens = scannerReadLine().split("\\t");
        if (tokens.length > 0) {
            date = tokens[1]; // get first date
            while ((line = scannerReadLine()) != null) {
                if (stats.size() == days) {
                    break; // request number of stats found
                }

                tokens = line.split("\\t");
                if (tokens.length == 4) {
                    correct += parseInt(tokens[1]);
                    incorrect += parseInt(tokens[2]);
                } else if (tokens.length == 2) {
                    stats.add(new String[]{correct + "", incorrect + "", date});    // record stat
                    correct = 0;
                    incorrect = 0;
                    date = tokens[1]; // get next date
                }
                if (!scanner.hasNextLine()) {
                    stats.add(new String[]{correct + "", incorrect + "", date});    // record stat
                    break; // EOF
                }
            }

        }

        return stats;
    }


    /**
     * Retrieves all of the statistics -
     * a 2d array of [ totalCorrect, totalIncorrect ] for all words/days/categories stored.
     */
    public int[] getLifeTimeStats() {
        int correct = 0;
        int incorrect = 0;
        scanner = getScannerForStatFile();
        while ((line = scannerReadLine()) != null) {
            String[] tokens = line.split("\\t");

            if (tokens.length == 4) {
                correct += parseInt(tokens[1]);
                incorrect += parseInt(tokens[2]);
            }
        }
        return new int[]{correct, incorrect};
    }
}
