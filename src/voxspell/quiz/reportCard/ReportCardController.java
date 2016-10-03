package voxspell.quiz.reportCard;

import voxspell.quiz.SpellingQuiz;

import java.util.ArrayList;

/**
 * Created by will on 3/10/16.
 */
public abstract class ReportCardController {

    protected int level;
    protected ArrayList words;
    protected ArrayList wordFirstAttempts;
    protected ArrayList wordSecondAttempts;
    protected SpellingQuiz spellingQuiz;

    public void setValues(SpellingQuiz spellingQuiz, ArrayList<String> words, ArrayList<String> wordFirstAttempts, ArrayList<String> wordSecondAttempts, int level) {
        this.spellingQuiz = spellingQuiz;
        this.words = words;
        this.wordFirstAttempts = wordFirstAttempts;
        this.wordSecondAttempts = wordSecondAttempts;
        this.level = level;
    }


    public abstract void generateScene();
}
