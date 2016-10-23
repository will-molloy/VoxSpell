package voxspell.dailyChallenges;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import voxspell.Main;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for the DailyChallenges scene. (GUI)
 * Also reads and writes to the hidden daily challenge file.
 *
 * @author Will Molloy
 */
public class DailyChallengeGUIController implements Initializable {

    private final String fileName = ".dailyChallenges", tempFileName = ".dailyTemp";
    @FXML
    private ImageView image1, image2, image3;
    private Image
            uncheckedBoxImg = new Image(Main.class.getResourceAsStream("media/images/unchecked_box.png")),
            checkedBoxImg = new Image(Main.class.getResourceAsStream("media/images/checked_box.png"));
    @FXML
    private Text totalChallengesText;
    @FXML
    private ProgressBar challengeProgress1, challengeProgress2, challengeProgress3;
    private File dailyChallengeFile;
    private File tempFile;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    private void handleResetBtn(ActionEvent actionEvent) {
        resetChallenges();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        image1.setImage(uncheckedBoxImg);
        image2.setImage(uncheckedBoxImg);
        image3.setImage(uncheckedBoxImg);
        createFiles();
        getDailyChallenges();
    }

    /**
     * Make sure the hidden daily challenge and temp file exist.
     */
    private void createFiles() {
        dailyChallengeFile = new File(fileName);
        if (!dailyChallengeFile.exists()) {
            makeHiddenFile(dailyChallengeFile);
        }

        tempFile = new File(tempFileName);
        if (!tempFile.exists()) {
            makeHiddenFile(tempFile);
        }
    }

    private void makeHiddenFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetBufferedReaderAndWriter() {
        try {
            bufferedReader = new BufferedReader(new FileReader(dailyChallengeFile));
            bufferedWriter = new BufferedWriter(new FileWriter(dailyChallengeFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the daily challenges from the hidden file.
     */
    private void getDailyChallenges() {
        resetBufferedReaderAndWriter();
        try {
            readInNewDate();
            readInTotalChallenges();
            updateChallengesProgresses();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current date and compares it to the previous date stored in the hidden file,
     * if they differ resets the challenges.
     */
    private void readInNewDate() throws IOException {
        String currentDate = getCurrentDate();
        String line;
        line = bufferedReader.readLine();
        String[] tokens = line.split("\\t");
        String prevDate = tokens[1];
        // determine if new day or not
        if (!currentDate.equals(prevDate)) {
            updateDate(currentDate);
            resetChallenges();
        }
    }

    /**
     * Updates the date in the hidden challenges file.
     */
    private void updateDate(String currentDate) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            bufferedWriter.write("date\t" + currentDate);
            bufferedWriter.newLine();
            rewriteRestOfFile();
            bufferedWriter.flush();
            tempFile.renameTo(dailyChallengeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * Reads in the total challenges completed from the hidden file and updates the Text View for total challenges.
     */
    private void readInTotalChallenges() throws IOException {
        String line;
        resetBufferedReaderAndWriter();
        bufferedReader.readLine();  // skip first line containing the date
        line = bufferedReader.readLine();
        String[] tokens = line.split("\\t");
        updateTotalChallenges(Integer.parseInt(tokens[1]));
    }

    private void updateTotalChallenges(int i) {
        totalChallengesText.setText("Total Challenges Completed: " + i);
    }

    /**
     * Updates the progress bar for each challenge.
     */
    private void updateChallengesProgresses() {
        String line;
        int i = 0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens.length >= 3) { // may read empty line
                    double progress = Double.parseDouble(tokens[1]) / Double.parseDouble(tokens[2]);
                    if (progress >= 1) {
                        setChallengeImgToChecked(ChallengeType.values()[i]);
                    }
                    updateChallengeProgress(ChallengeType.values()[i++], progress);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a respectful challenges image to a ticked check box.
     */
    private void setChallengeImgToChecked(ChallengeType challenge) {
        switch (challenge) {
            case QUIZES_COMPLETED:
                image1.setImage(checkedBoxImg);
                break;
            case QUIZ_ACCURACY:
                image2.setImage(checkedBoxImg);
                break;
            case WORD_LISTS_CREATED:
                image3.setImage(checkedBoxImg);
                break;
        }
    }

    /**
     * Sets the value of a respectful challenges progress bar.
     */
    private void updateChallengeProgress(ChallengeType challenge, double progressDone) {
        switch (challenge) {
            case QUIZES_COMPLETED:
                challengeProgress1.setProgress(progressDone);
                break;
            case QUIZ_ACCURACY:
                challengeProgress2.setProgress(progressDone);
                break;
            case WORD_LISTS_CREATED:
                challengeProgress3.setProgress(progressDone);
                break;
        }
    }

    /**
     * Writes the given amount of lines to the temp file (whatever is in the BufferedReader
     */
    private void reWriteXLinesInFileToTempFile(int x) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        for (int i = 0; i < x; i++) {
            String line = bufferedReader.readLine();
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
    }

    /**
     * Writes whatever's left in the BufferedReader
     */
    private void rewriteRestOfFile() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
    }

    /**
     * Sets the progress of all challenges to 0 within the hidden challenge file.
     */
    private void resetChallenges() {
        resetBufferedReaderAndWriter();
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            // Skip over date and total challenges complete
            reWriteXLinesInFileToTempFile(2);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\t");

                // Rewrite challenge with zero progress
                bufferedWriter.write(tokens[0] + '\t' + "0" + "\t" + tokens[2]);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            tempFile.renameTo(dailyChallengeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // reset GUI - if user pressed the reset button this is needed for dynamic view update
        for (int i = 0; i < 3; i++) {
            updateChallengeProgress(ChallengeType.values()[i], 0);
        }
        image1.setImage(uncheckedBoxImg);
        image2.setImage(uncheckedBoxImg);
        image3.setImage(uncheckedBoxImg);
    }


    /**
     * Updates the specified challenge.
     * Note: this does not interact with the GUI in anyway
     * - the GUI is only updated when the scene is reloaded (from the hidden file) OR user presses reset button.
     *
     * @param challenge    challenge to update.
     * @param progressDone progress done e.g. one quiz completed.
     */
    public void updateChallenge(ChallengeType challenge, int progressDone) {
        createFiles();
        // update FILE - GUI will be updated on reload
        incrementChallengeXProgress(challenge, progressDone);
    }

    /**
     * Increments the progress done to the specified challenge within the hidden file.
     *
     * @param challenge    - challenge to update
     * @param progressDone - progress to add in
     */
    private void incrementChallengeXProgress(ChallengeType challenge, int progressDone) {
        resetBufferedReaderAndWriter();
        String line;
        try {
            // Rewrite the beginning of the file - date + total challenges + any previous challenges
            reWriteXLinesInFileToTempFile(challenge.ordinal() + 2);

            // Read in challenge to increment
            line = bufferedReader.readLine();
            String[] tokens = line.split("\\t");
            int currentProgress = Integer.parseInt(tokens[1]);

            // Rewrite progress with incremented value
            bufferedWriter.write(tokens[0] + "\t" + (progressDone + currentProgress) + "\t" + tokens[2]);
            bufferedWriter.newLine();

            // Rewrite the rest of the file (any more challenges)
            rewriteRestOfFile();
            bufferedWriter.flush();
            tempFile.renameTo(dailyChallengeFile);

            // Check if the now incremented challenge is completed or not (== otherwise challenge will be achieved multiple times)
            if (progressDone + currentProgress == Integer.parseInt(tokens[2])) {
                incrementTotalChallenges(); // if it is add to the total challenges
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Increments the total challenges completed by one (within the hidden file)
     */
    private void incrementTotalChallenges() throws IOException {
        resetBufferedReaderAndWriter();
        bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        String line;

        // Rewrite the date
        reWriteXLinesInFileToTempFile(1);

        line = bufferedReader.readLine();
        String[] tokens = line.split("\\t");

        // Increment total challenges
        bufferedWriter.write("total_challenges\t" + (1 + Integer.parseInt(tokens[1])));
        bufferedWriter.newLine();

        // Write the other challenges (rest of file)
        rewriteRestOfFile();
        bufferedWriter.flush();

        tempFile.renameTo(dailyChallengeFile);
    }
}
