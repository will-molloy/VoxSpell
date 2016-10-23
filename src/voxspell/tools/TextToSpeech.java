package voxspell.tools;

import voxspell.settings.Voice;
import voxspell.quiz.SpellingQuizController;

import javax.swing.*;
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
    private FestivalWorker festivalWorker;
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
        voice = newVoice.getCode();
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
        festivalWorker = new FestivalWorker(sentence);
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
        private static final String hiddenScmFile = ".ttsScript.scm";
        private ProcessBuilder _processBuilder;
        private Process _process;
        private String _sentence;

        public FestivalWorker(String sentence) {
            _sentence = sentence;
        }

        @Override
        protected Void doInBackground() throws Exception {

            // delete scm file - if the program crashed while this thread was running it will contain previous words
            String deleteScmFile = "rm -f " + hiddenScmFile;
            runBashCommand(deleteScmFile);

            String appendVoiceToScmFile = "echo \"" + voice + "\" >> " + hiddenScmFile;
            runBashCommand(appendVoiceToScmFile);

            if (_slowerVoice) {
                String slowDown = "echo \"(Parameter.set 'Duration_Stretch 2.2)\" >> " + hiddenScmFile;
                runBashCommand(slowDown);
                _slowerVoice = false;
            }

            String sayText = "echo \"(SayText \\\"" + _sentence + "\\\")\" >> " + hiddenScmFile;
            runBashCommand(sayText);

            String runScmFile = "festival -b " + hiddenScmFile;
            runBashCommand(runScmFile);

            runBashCommand(deleteScmFile);
            return null;
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
        }
    }

}
