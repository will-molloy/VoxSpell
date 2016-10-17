package voxspell.dailyChallenges;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SpinnerValueFactory;
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
 *
 * @author Will Molloy
 */
public class DailyChallengeGUIController implements Initializable {

    private final String fileName = ".dailyChallenges", tempFileName = ".dailyTemp";
    @FXML
    private ImageView image1, image2, image3;
    private Image
            unchecked = new Image(Main.class.getResourceAsStream("media/images/unchecked_box.png")),
            checked = new Image(Main.class.getResourceAsStream("media/images/checked_box.png"));
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
        image1.setImage(unchecked);
        image2.setImage(unchecked);
        image3.setImage(unchecked);
        createFiles();
        getDailyChallenges();
    }

    private void checkImage(int x){
        switch (x){
            case 1:
                image1.setImage(checked);
                break;
            case 2:
                image2.setImage(checked);
                break;
            case 3:
                image3.setImage(checked);
                break;
        }
    }

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

    private void updateDate(String currentDate) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            bufferedWriter.write("date\t" + currentDate);
            bufferedWriter.newLine();
            bufferedReader.readLine(); // skip first line
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

    private void readInTotalChallenges() throws IOException {
        String line;
        line = bufferedReader.readLine();
        String[] tokens = line.split("\\t");
        updateTotalChallenges(Integer.parseInt(tokens[1]));
    }

    private void updateTotalChallenges(int i) {
        totalChallengesText.setText("Total Challenges Completed: " + i);
    }

    private void updateChallengesProgresses() {
        String line;
        int i = 1;
        try {
            while((line = bufferedReader.readLine())!=null){
                String[] tokens = line.split("\\t");
                double progress = Double.parseDouble(tokens[1])/Double.parseDouble(tokens[2]);
                if (progress >=1){
                    checkImage(i);
                }
                updateChallengeProgress(i++,progress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateChallengeProgress(int challenge, double progressDone) {
        switch (challenge) {
            case 1:
                challengeProgress1.setProgress(progressDone); // 10 spelling quizes
                break;
            case 2:
                challengeProgress2.setProgress(progressDone); // accuracy
                break;
            case 3:
                challengeProgress3.setProgress(progressDone); // one word list
                break;
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

    private void incrementChallengeXProgress(int x, int progressDone) {
        resetBufferedReaderAndWriter();
        String line;
        try {
            reWriteXLinesInFileToTempFile(x + 1); // skip over date, total challenges and any previous challenges
            line = bufferedReader.readLine();
            String[] tokens = line.split("\\t");
            int currentProgress = Integer.parseInt(tokens[1]);
            // rewrite progress with incremented value
            bufferedWriter.write(tokens[0] + "\t" + (progressDone + currentProgress) + "\t" + tokens[2]);

            bufferedWriter.newLine();
            // Rewrite the rest
            rewriteRestOfFile();
            bufferedWriter.flush();
            tempFile.renameTo(dailyChallengeFile);
            if (progressDone+currentProgress>=Integer.parseInt(tokens[2])){
                challengeComplete(x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void challengeComplete(int challenge) throws IOException {
        incrementTotalChallenges();
    }

    private void incrementTotalChallenges() throws IOException {
        resetBufferedReaderAndWriter();
        bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        String line;
        reWriteXLinesInFileToTempFile(1);
        line=bufferedReader.readLine();
        String[] tokens = line.split("\\t");
        bufferedWriter.write("total_challenges\t"+(1+Integer.parseInt(tokens[1])));
        bufferedWriter.newLine();
        rewriteRestOfFile();
        bufferedWriter.flush();
        tempFile.renameTo(dailyChallengeFile);
    }

    private void reWriteXLinesInFileToTempFile(int x) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
        for (int i = 0; i < x; i++) {
            String line = bufferedReader.readLine();
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
    }

    private void rewriteRestOfFile() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
    }

    private void resetChallenges() {
        // reset in FILE
        resetBufferedReaderAndWriter();
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            reWriteXLinesInFileToTempFile(2);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\t");
                bufferedWriter.write(tokens[0] + '\t' + "0" + "\t" + tokens[2]);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            tempFile.renameTo(dailyChallengeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // reset in GUI
        for (int i = 1; i <= 3; i++) {
            updateChallengeProgress(i, 0);
        }
        image1.setImage(unchecked);
        image2.setImage(unchecked);
        image3.setImage(unchecked);
    }

    public void updateChallenge(int challenge, int progressDone) {
        createFiles();
        // update FILE - GUI will be updated on reload
        incrementChallengeXProgress(challenge, progressDone);
    }


}
