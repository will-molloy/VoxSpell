package voxspell.test;

import org.junit.Before;
import org.junit.Test;
import voxspell.statistics.StatisticsRetriever;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests the StatisticsFileHandler class and whether it writes statistics to files correctly or not.
 * <p>
 *
 * @author Will Molloy
 */
public class TestStatisticsFileHandlerAndRetriever {

    private static final String ACTUAL_FILE_NAME = ".ACTUAL";
    private static final String TEST_DATE = "2016-10-18";
    private StatisticsRetriever statisticsFileHandler;
    private File actualFile = new File(ACTUAL_FILE_NAME);
    private BufferedWriter bufferedWriter;

    @Before
    public void setUp() {
        actualFile.delete();
        try {
            actualFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        statisticsFileHandler = new StatisticsRetriever();
        statisticsFileHandler.setDate(TEST_DATE);         // Static date so tests work in the future
        statisticsFileHandler.setFileName(ACTUAL_FILE_NAME); // different file, so tests don't mess with actual statistics
    }

    private List<String> readFileLinesIntoList(File file) {
        List<String> strings = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    private void appendToActualFile(String s) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(actualFile, true));
            bufferedWriter.write(s);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendToActualFile(List<String> fileOutput) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(actualFile, true));
            for (String s : fileOutput) {
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNoStatisticsCorrectWord() {
        // ACTUAL file is empty
        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("word\t1\t0\tcategory");

        statisticsFileHandler.writeStatistic("word", true, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void testNoStatisticsIncorrectWord() {
        // ACTUAL file is empty
        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("word\t0\t1\tcategory");

        statisticsFileHandler.writeStatistic("word", false, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void updateStatisticCorrectWord() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + TEST_DATE);
        appendToActualFile("word\t100\t2\tcategory");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("word\t101\t2\tcategory");

        statisticsFileHandler.writeStatistic("word", true, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void updateStatisticIncorrectWord() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + TEST_DATE);
        appendToActualFile("word\t100\t2\tcategory");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("word\t100\t3\tcategory");

        statisticsFileHandler.writeStatistic("word", false, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void wordHasBeenRecordedButDifCategory() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + TEST_DATE);
        appendToActualFile("word\t100\t2\tcategory");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("word\t1\t0\tnewCategory"); // insert of word with new category
        expected.add("word\t100\t2\tcategory");

        statisticsFileHandler.writeStatistic("word", true, "newCategory");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void wordHasBeenRecordedButDifDay() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + TEST_DATE);
        appendToActualFile("word\t100\t2\tcategory");

        appendToActualFile("date\t1990-02-02");
        appendToActualFile("old word\t10000\t3\told category");


        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("old word\t1\t0\told category"); // insert same word from previous date
        expected.add("word\t100\t2\tcategory");

        expected.add("date\t1990-02-02");
        expected.add("old word\t10000\t3\told category");

        statisticsFileHandler.writeStatistic("old word", true, "old category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }


    @Test
    public void noStatsForToday() {
        // ADD some stats to actual file
        appendToActualFile("date\t1990-02-02");
        appendToActualFile("old word\t10000\t3\told category");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + TEST_DATE);
        expected.add("old word\t0\t1\told category"); // insert same word from previous date
        expected.add("word\t1\t0\tcategory");

        expected.add("date\t1990-02-02");
        expected.add("old word\t10000\t3\told category");

        statisticsFileHandler.writeStatistic("word", true, "category");
        statisticsFileHandler.writeStatistic("old word", false, "old category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void getCategoryStats() {
        // ADD some stats to actual file
        appendToActualFile("date\t2000-02-02");
        appendToActualFile("word0\t3\t2\tfish");
        appendToActualFile("date\t1990-02-02");
        appendToActualFile("word\t1000\t3\tfish");
        appendToActualFile("word2\t11\t0\tfish");
        appendToActualFile("apple\t12\t10\tfruit");

        int[] actual = statisticsFileHandler.getStatsForCategory("fish");
        assertEquals("1014 5", actual[0] + " " + actual[1] + "");
    }

    @Test
    public void lifeTimeStats() {
        // ADD some stats to actual file
        for (int i = 0; i < 12; i++) {
            appendToActualFile("date\t2000-02-02");
            appendToActualFile("word0\t3\t2\tfish");
        }

        int[] actual = statisticsFileHandler.getLifeTimeStats();
        assertEquals("36 24", actual[0] + " " + actual[1]);
    }

    @Test
    public void getPrev12DaysExactly12DaysNoGaps() {
        int x = 18;
        List<String> fileOutput = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String day = x < 10 ? "0" + x : x + "";
            fileOutput.add("date\t2016-10-" + day);
            fileOutput.add("word\t3\t2\tfish");
            x--;
        }
        appendToActualFile(fileOutput);
        List<String[]> actual = statisticsFileHandler.getPrevDayStats(12);
        List<String> expected = new ArrayList<>();
        int day = 18;
        for (int i = 0; i < 12; i++) {
            String dayFormatted = day < 10 ? "0" + day : day + "";
            day--;
            expected.add("3\t2\t2016-10-" + dayFormatted);
        }
        assertEquals(expected, stringArraysToString(actual));
    }

    private List<String> stringArraysToString(List<String[]> expected) {
        List<String> output = new ArrayList<>();
        for (String[] old : expected) {
            String temp = "";
            for (int i = 0; i < old.length; i++) {
                temp += old[i] + "\t";
            }
            temp = temp.substring(0, temp.length() - 1);
            output.add(temp);
        }
        return output;
    }

    /**
     * Test for retrieving the prev 12 days of statistics - where the user used the application everyday.
     */
    @Test
    public void getPrev12DaysAllGaps() {
        int x = 18;
        List<String> fileOutput = new ArrayList<>();
        List<String> expected = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            String day = x < 10 ? "0" + x : x + "";
            fileOutput.add("date\t2016-10-" + day);
            fileOutput.add("word\t3\t2\tfish");
            expected.add("3\t2\t2016-10-" + day);
            x--;
        }
        appendToActualFile(fileOutput);
        List<String[]> actual = statisticsFileHandler.getPrevDayStats(12);
        assertEquals(expected, stringArraysToString(actual));
    }

    /**
     * Retrieve the previous 12 days of statistics -
     * where the user has not used the application for 3 days.
     */
    @Test
    public void getEndGaps() {
        int x = 15;
        List<String> fileOutput = new ArrayList<>();
        for (int i = 0; i < 12; i++, x--) {
            String day = x < 10 ? "0" + x : x + "";
            fileOutput.add("date\t2016-10-" + day);
            fileOutput.add("word\t3\t2\tfish");
        }
        appendToActualFile(fileOutput);
        List<String[]> actual = statisticsFileHandler.getPrevDayStats(12);
        List<String> expected = new ArrayList<>();
        int day = 18;
        for (int i = 0; i < 3; i++, day--) {
            String dayFormatted = day < 10 ? "0" + day : day + "";
            expected.add("0\t0\t2016-10-" + dayFormatted); // empty statistics, but days are still included
        }
        for (int i = 0; i < 9; i++, day--) {
            String dayFormatted = day < 10 ? "0" + day : day + "";
            expected.add("3\t2\t2016-10-" + dayFormatted);
        }
        assertEquals(expected, stringArraysToString(actual));
    }

    /**
     * Retrieve the prev 12 days of statistics -
     * where the user didn't use the application for the first 3 days
     */
    @Test
    public void getStartGaps() {
        int x = 18;
        List<String> fileOutput = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            String day = x < 10 ? "0" + x : x + "";
            fileOutput.add("date\t2016-10-" + day);
            fileOutput.add("word\t3\t2\tfish");
            x--;
        }
        appendToActualFile(fileOutput);
        List<String[]> actual = statisticsFileHandler.getPrevDayStats(12);
        List<String> expected = new ArrayList<>();
        int day = 18;
        for (int i = 0; i < 9; i++, day--) {
            String dayFormatted = day < 10 ? "0" + day : day + "";
            expected.add("3\t2\t2016-10-" + dayFormatted);
        }
        for (int i = 0; i < 3; i++, day--) {
            String dayFormatted = day < 10 ? "0" + day : day + "";
            expected.add("0\t0\t2016-10-" + dayFormatted); // empty statistics, but days are still included
        }
        assertEquals(expected, stringArraysToString(actual));
    }

    /**
     * Retrieve prev 12 days of statistics -
     * where the user alternated from using/not using the application every 2 days.
     */
    @Test
    public void getMiddleGaps() {
        int x = 18;
        List<String> fileOutput = new ArrayList<>();
        // User used the program the 5th,6th .. 9,10,  13,14,  17,18
        for (int i = 0; i < 4; i++, x -= 4) {
            fileOutput.addAll(recordTwoDaysOfStats(x));
        }
        appendToActualFile(fileOutput);


        List<String[]> actual = statisticsFileHandler.getPrevDayStats(12);
        List<String> expected = new ArrayList<>();
        expected.add("3\t2\t2016-10-18");
        expected.add("3\t2\t2016-10-17");

        expected.add("0\t0\t2016-10-16"); // blank
        expected.add("0\t0\t2016-10-15");

        expected.add("3\t2\t2016-10-14");
        expected.add("3\t2\t2016-10-13");

        expected.add("0\t0\t2016-10-12"); // blank
        expected.add("0\t0\t2016-10-11");

        expected.add("3\t2\t2016-10-10");
        expected.add("3\t2\t2016-10-09");

        expected.add("0\t0\t2016-10-08"); // blank
        expected.add("0\t0\t2016-10-07");
        assertEquals(expected, stringArraysToString(actual));
    }

    private List<String> recordTwoDaysOfStats(int x) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < 2; i++, x--) {
            String day = x < 10 ? "0" + x : x + "";
            out.add("date\t2016-10-" + day);
            out.add("word\t3\t2\tfish");
        }
        return out;
    }
}
