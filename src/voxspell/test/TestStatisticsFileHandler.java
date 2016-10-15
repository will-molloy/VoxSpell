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
public class TestStatisticsFileHandler {

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

    @Test
    public void getCategoryStats(){
        // ADD some stats to actual file
        appendToActualFile("date\t2000-02-02");
        appendToActualFile("word0\t3\t2\tfish");
        appendToActualFile("date\t1990-02-02");
        appendToActualFile("word\t1000\t3\tfish");
        appendToActualFile("word2\t11\t0\tfish");
        appendToActualFile("apple\t12\t10\tfruit");

        int[] actual = statisticsFileHandler.getStatsForCategory("fish");
        assertEquals("1014 5", actual[0] + " "+ actual[1] + "");
    }

    @Test
    public void prev12DaysMoreThan12Days(){
        // ADD some stats to actual file
        List<int[]> expected = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            appendToActualFile("date\t2000-02-02");
            appendToActualFile("word0\t3\t2\tfish");

            expected.add(new int[] {3,2});
        }

        expected.remove(expected.get(12)); // remove 13th entry

        List<int[]> actual = statisticsFileHandler.get12PrevDayStats();
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i)[0] + " " + expected.get(i)[1], actual.get(i)[0] + " " + actual.get(i)[1]);
        }
    }

    @Test
    public void prev12DaysLessThan12Days(){
        // ADD some stats to actual file
        List<int[]> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            appendToActualFile("date\t2000-02-02");
            appendToActualFile("word0\t3\t2\tfish");

            expected.add(new int[] {3,2});
        }

        List<int[]> actual = statisticsFileHandler.get12PrevDayStats();
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i)[0] + " " + expected.get(i)[1], actual.get(i)[0] + " " + actual.get(i)[1]);
        }
    }

    @Test
    public void prev12DaysExactly12Days(){
        // ADD some stats to actual file
        List<int[]> expected = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            appendToActualFile("date\t2000-02-02");
            appendToActualFile("word0\t3\t2\tfish");

            expected.add(new int[] {3,2});
        }

        List<int[]> actual = statisticsFileHandler.get12PrevDayStats();
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i)[0] + " " + expected.get(i)[1], actual.get(i)[0] + " " + actual.get(i)[1]);
        }
    }

    @Test
    public void zzz(){
        appendToActualFile("date\t" +currentDate);
        appendToActualFile("of\t1\t0\tLevel 2");
        appendToActualFile("had\t1\t0\tLevel 2");
        appendToActualFile("there\t1\t0\tLevel 2");
        appendToActualFile("at\t1\t0\tLevel 2");
        appendToActualFile("on\t1\t0\tLevel 2");
        appendToActualFile("that\t1\t0\tLevel 2");
        appendToActualFile("then\t1\t0\tLevel 2");
        appendToActualFile("up\t1\t0\tLevel 2");
        appendToActualFile("for\t1\t0\tLevel 2");
        appendToActualFile("me\t1\t0\tLevel 2");
        appendToActualFile("he\t1\t0\tLevel 2");

        List<String> expected = new ArrayList<>();
        expected.add("date	" +currentDate);
        expected.add("of\t1\t0\tLevel 2");
        expected.add("had\t1\t0\tLevel 2");
        expected.add("there	1	0	Level 2");
        expected.add("at	1	0	Level 2");
        expected.add("on	1	0	Level 2");
        expected.add("that	1	0	Level 2");
        expected.add("then	2	0	Level 2");
        expected.add("up	1	0	Level 2");
        expected.add("for	1	0	Level 2");
        expected.add("me	1	0	Level 2");
        expected.add("he	1	0	Level 2");

        statisticsFileHandler.writeStatistic("then", true, "Level 2");
        assertEquals(expected, readFileLinesIntoList(actualFile));


    }

}
