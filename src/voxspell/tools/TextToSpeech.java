package voxspell.tools;

import voxspell.quiz.SpellingQuizController;
import voxspell.settings.Voice;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Uses "festival --tts" calls to bash to read Strings using text to speech.
 *
 * @author Will Molloy
 */
public class TextToSpeech {

    // Singleton
    private static final TextToSpeech instance = new TextToSpeech();

    // voice
    private static String voice = "voice_kal_diphone";
    private boolean _continueSpellingQuiz = false;
    private boolean _slowerVoice = false;
    private SpellingQuizController _spellingQuizController;

    private TextToSpeech() {
    }

    public static TextToSpeech getInstance() {
        return instance;
    }

    /**
     * Sets the voice to be used in TextToSpeech.
     * Must be in the form "(voice_)"
     * E.g. "(voice_kal_diphone)" or "(voice_rab_diphone)"
     */
    public static void setVoice(Voice newVoice) {
        voice = newVoice.getScmCode();
    }

    /**
     * Reads a sentence and continues the spelling quiz once done.
     */
    public void readSentenceAndContinueSpellingQuiz(String sentence, SpellingQuizController spellingQuizController) {
        _continueSpellingQuiz = true;
        _spellingQuizController = spellingQuizController;
        readSentence(sentence);
    }

    /**
     * Reads out a sentence using text to speech.
     */
    public void readSentence(String sentence) {
        // Use the festival worker class to read the sentence on a background thread
        FestivalWorker festivalWorker = new FestivalWorker(sentence);
        festivalWorker.execute();
    }

    /**
     * Reads out a sentence slowly using text to speech.
     */
    public void readSentenceSlowly(String sentence) {
        _slowerVoice = true;
        readSentence(sentence);
    }

    /**
     * Reads out the letters of a word or sentence using text to speech.
     */
    public void readLetters(String word) {
        String sentence = "";
        char c;
        for (int i = 0; i < word.length(); i++) {
            c = word.charAt(i);
            sentence += c + " .. "; // .. causes the speech synthesis in festival --tts to pause for a sec
        }
        readSentence(sentence);
    }

    /**
     * SwingWorker class to process bash commands on a background thread.
     *
     * @author Will Molloy
     */
    private class FestivalWorker extends SwingWorker<Void, Void> {

        // hidden scm file to run festival - needed to change voice
        private static final String TEMP_FILE = ".ttsScript.scm";
        private File hiddenScmFile = new File(TEMP_FILE);
        private ProcessBuilder _processBuilder;
        private Process _process;
        private String _sentence;

        FestivalWorker(String sentence) {
            _sentence = sentence;
        }

        @Override
        protected Void doInBackground() throws Exception {
            loadFile();
            String runScmFile = "festival -b " + hiddenScmFile;
            runBashCommand(runScmFile);
            return null;
        }

        /**
         * Loads the scm file for festival to run using the appropriate settings.
         */
        private void loadFile() {
            try {
                hiddenScmFile.delete();
                hiddenScmFile.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(hiddenScmFile, false));
                writer.write(voice); // set voice - from settings
                writer.newLine();

                if (_slowerVoice) {
                    writer.write("(Parameter.set 'Duration_Stretch 2.2)");
                    writer.newLine();
                    _slowerVoice = false;
                }

                writer.write("(SayText \"" + _sentence + "\")");
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void runBashCommand(String command) throws IOException, InterruptedException {
            _processBuilder = new ProcessBuilder("bash", "-c", command);
            _process = _processBuilder.start();
            _process.waitFor();
        }

        /**
         * Continues the spelling quiz if specified.
         */
        @Override
        public void done() {
            if (_continueSpellingQuiz) {
                _spellingQuizController.continueSpellingQuiz();
                _continueSpellingQuiz = false;
            }
            hiddenScmFile.delete();
        }
    }

}
