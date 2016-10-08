package voxspell.quiz.reportCard;

/**
 * Created by will on 3/10/16.
 */
public class FailedQuizReportCardController extends ReportCardController {

    @Override
    protected void setLevelText() {
        passedOrFailedLevelText.setText("Failed " + wordList.toString());
    }
}
