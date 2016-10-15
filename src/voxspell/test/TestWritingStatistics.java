package voxspell.test;

import org.junit.Before;
import org.junit.Test;
import voxspell.statistics.StatisticsFileHandler;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests the StatisticsFileHandler class and whether it writes statistics to files correctly or not.
 * <p>
 * Problem with these tests: asserts the current date so assuming Date class is correct.
 *
 * @author Will Molloy
 */
public class TestWritingStatistics {

    private StatisticsFileHandler statisticsFileHandler;
    private static final String ACTUAL_FILE_NAME = ".ACTUAL";
    private File actualFile = new File(ACTUAL_FILE_NAME);
    private String currentDate;
    private BufferedWriter bufferedWriter;

    @Before
    public void setUp() {
        actualFile.delete();
        try {
            actualFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        statisticsFileHandler = new StatisticsFileHandler(ACTUAL_FILE_NAME);
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

    private void appendToActualFile(String s){
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(actualFile, true));
            bufferedWriter.write(s);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNoStatisticsCorrectWord() {
        // ACTUAL file is empty
        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("word\t1\t0\tcategory");

        statisticsFileHandler.writeStatistic("word", true, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void testNoStatisticsIncorrectWord() {
        // ACTUAL file is empty
        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("word\t0\t1\tcategory");

        statisticsFileHandler.writeStatistic("word", false, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void updateStatisticCorrectWord() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + currentDate);
        appendToActualFile("word\t100\t2\tcategory");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("word\t101\t2\tcategory");

        statisticsFileHandler.writeStatistic("word", true, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void updateStatisticIncorrectWord() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + currentDate);
        appendToActualFile("word\t100\t2\tcategory");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("word\t100\t3\tcategory");

        statisticsFileHandler.writeStatistic("word", false, "category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void wordHasBeenRecordedButDifCategory() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + currentDate);
        appendToActualFile("word\t100\t2\tcategory");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("word\t1\t0\tnewCategory"); // insert of word with new category
        expected.add("word\t100\t2\tcategory");

        statisticsFileHandler.writeStatistic("word", true, "newCategory");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

    @Test
    public void wordHasBeenRecordedButDifDay() {
        // ADD some stats to actual file
        appendToActualFile("date\t" + currentDate);
        appendToActualFile("word\t100\t2\tcategory");

        appendToActualFile("date\t1990-02-02");
        appendToActualFile("old word\t10000\t3\told category");


        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("old word\t1\t0\told category"); // insert same word from previous date
        expected.add("word\t100\t2\tcategory");

        expected.add("date\t1990-02-02");
        expected.add("old word\t10000\t3\told category");

        statisticsFileHandler.writeStatistic("old word", true, "old category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }


    @Test
    public void noStatsForToday(){
        // ADD some stats to actual file
        appendToActualFile("date\t1990-02-02");
        appendToActualFile("old word\t10000\t3\told category");

        List<String> expected = new ArrayList<>();
        expected.add("date\t" + currentDate);
        expected.add("old word\t0\t1\told category"); // insert same word from previous date
        expected.add("word\t1\t0\tcategory");

        expected.add("date\t1990-02-02");
        expected.add("old word\t10000\t3\told category");

        statisticsFileHandler.writeStatistic("word", true, "category");
        statisticsFileHandler.writeStatistic("old word", false, "old category");
        assertEquals(expected, readFileLinesIntoList(actualFile));
    }

}
