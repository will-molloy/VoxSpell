package voxspell.statistics;

import voxspell.tools.CustomFileReader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Contains methods for writing to the hidden statistics file.
 * Writes statistics for a word: correct/incorrect count, associated category and today's date.
 * <p>
 * The statistic file format works as follows:
 * date (tab) 2016-10-15 (earliest date)
 * word (tab) correctSpellings (tab) incorrectSpellings (tab) category
 * word ..
 * ..
 * date (tab) 2016-10-14 (next date)
 * word ..
 * ..
 * <p>
 * The quiz stats file works as follows:
 * category (tab) best streak (tab) best elapsed time
 *
 * @author Will Molloy
 */
public class StatisticsFileHandler extends CustomFileReader {

    private static final String TEMP_FILE_NAME = ".tempStats", DATE_ID = "date";
    // default file name
    private static final String wordStatsFileName = ".wordStats", quizStatsFileName = ".quizStats";
    protected static String todaysDate;
    private static File wordStatsFile;
    protected static File quizStatsFile;
    protected String line;
    private File tempFile = new File(TEMP_FILE_NAME);
    private String word;
    private boolean correct;
    private String category;

    /**
     * Instantiates the statistics file handler - loading the hidden file and current date
     */
    public StatisticsFileHandler() {
        wordStatsFile = new File(wordStatsFileName);
        quizStatsFile = new File(quizStatsFileName);
        if (!wordStatsFile.exists()) {
            makeHiddenFile(wordStatsFile);
        }
        if (!quizStatsFile.exists()) {
            makeHiddenFile(quizStatsFile);
        }
        todaysDate = getCurrentDate();
    }

    /**
     * Method to change the hidden statistics file name - for testing
     */
    public void setFileName(String fileName) {
        wordStatsFile = new File(fileName);
        makeHiddenFile(wordStatsFile);
    }

    private void makeHiddenFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to change the date - for testing
     */
    public void setDate(String date) {
        todaysDate = date;
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

    /**
     * Records a new word that hasn't been tested today.
     */
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

    /**
     * Updates a word that has already been tested today.
     */
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

    /**
     * Returns a scanner for the hidden statistics file
     */
    protected Scanner getScannerForStatFile() {
        try {
            return new Scanner(new FileReader(wordStatsFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes the statistics for a word that hasn't been tested yet today.
     */
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

    private String getDateFormatForFile() {
        return "date\t" + todaysDate + "\n";
    }

    private String getNewStatistic() {
        int correct = this.correct ? 1 : 0;
        int incorrect = !this.correct ? 1 : 0;
        return getStatisticFileFormat(word, correct, incorrect, category);
    }

    private String getStatisticFileFormat(String word, int correct, int incorrect, String category) {
        return word + "\t" + correct + "\t" + incorrect + "\t" + category + "\n";
    }

    /**
     * Writes what's left in the bufferedReader - i.e. rest of the statistics file
     */
    private void writeRestOfFile() throws IOException {
        while ((line = scannerReadLine()) != null) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();

        // Rename file
        tempFile.renameTo(wordStatsFile);
    }

    /**
     * Writes the statistics for a quiz i.e. best elapsed time and best streak.
     */
    public void writeQuizStatistic(String category, String streak, String timeInSeconds) {
        boolean categoryExists = false;
        try {
            scanner = new Scanner(new FileReader(quizStatsFile));
            while ((line = scannerReadLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens.length == 3 && tokens[0].equals(category)) {
                    categoryExists = true;
                    String currentStreak = tokens[1];
                    String currentTime = tokens[2];

                    // Check if better streak is found
                    if (parseInt(currentStreak) < parseInt(streak)) {
                        updateStreak(category, streak);
                    }

                    // Check if better time is found
                    if (currentTime.equals("dnf") || parseInt(timeInSeconds) < parseInt(currentTime)) {
                        updateTime(category, timeInSeconds);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!categoryExists){
            createStatisticsForNewCategory(category, streak, timeInSeconds);
        }
    }

    /**
     * Updates the best streak for a given category within the hidden quiz stats file.
     */
    private void updateStreak(String category, String streak) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            scanner = new Scanner(new FileReader(quizStatsFile));
            while ((line = scannerReadLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens.length == 3 && tokens[0].equals(category)) {
                    bufferedWriter.write(category + "\t" + streak + "\t" + tokens[2]);
                } else {
                    bufferedWriter.write(line);
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            tempFile.renameTo(quizStatsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the best time for a given category within the hidden quiz stats file.
     */
    private void updateTime(String category, String timeInSeconds) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            scanner = new Scanner(new FileReader(quizStatsFile));
            while ((line = scannerReadLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens.length == 3 && tokens[0].equals(category)) {
                    bufferedWriter.write(category + "\t" + tokens[1] + "\t" + timeInSeconds);
                } else {
                    bufferedWriter.write(line);
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            tempFile.renameTo(quizStatsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new statistic for a category that hasn't been recorded yet.
     */
    private void createStatisticsForNewCategory(String category, String streak, String timeInSeconds) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            scanner = new Scanner(new FileReader(quizStatsFile));
            bufferedWriter.write(category+"\t"+streak+"\t"+timeInSeconds);
            bufferedWriter.newLine();
            while ((line = scannerReadLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            tempFile.renameTo(quizStatsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the hidden statistic files
     */
    public void deleteStatistics() {
        quizStatsFile.delete();
        wordStatsFile.delete();
        makeHiddenFile(quizStatsFile);
        makeHiddenFile(wordStatsFile);
    }
}
