package voxspell.settings;

import java.io.*;

/**
 * Reads and writes to the hidden settings file for saving users settings on exit.
 *
 * @author Will Molloy
 */
public class SettingsFileHandler {

    private final String VOICE_ID = "voice", BACKGROUND_ID = "background";
    private File settingsFile = new File(".settings");
    private BufferedReader reader;
    private BufferedWriter writer;

    public SettingsFileHandler() {
        createFileReaderAndWriter();
    }

    private void createFileReaderAndWriter() {
        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader = new BufferedReader(new FileReader(settingsFile));
            writer = new BufferedWriter(new FileWriter(settingsFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Voice getSettingsVoice() {
        createFileReaderAndWriter();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens[0].equals(VOICE_ID)) {
                    return Voice.valueOf(tokens[1]); // tokens[1] = NZ, UK, US
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Voice.US; // DEFAULT
    }

    public MainMenuBackground getSettingsBackGround() {
        createFileReaderAndWriter();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens[0].equals(BACKGROUND_ID)) {
                    return MainMenuBackground.valueOf(tokens[1]); // tokens[1] = AUTUMN, SPRING, SUMMER, WINTER
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MainMenuBackground.AUTUMN; // DEFAULT
    }

    public void saveSettings(Voice voice, MainMenuBackground background) {
        settingsFile.delete(); // delete file contents
        createFileReaderAndWriter();
        try {
            writer.write(VOICE_ID + "\t" + voice);
            writer.newLine();
            writer.write(BACKGROUND_ID + "\t" + background);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
