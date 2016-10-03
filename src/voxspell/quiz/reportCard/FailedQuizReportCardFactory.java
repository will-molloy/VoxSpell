package voxspell.quiz.reportCard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by will on 3/10/16.
 */
public class FailedQuizReportCardFactory extends ReportCardFactory {

    @Override
    protected Parent getRootAndLoadLoader(FXMLLoader loader) {
        Parent root = null;
        try {
            root = loader.load(getClass().getResource("voxspell/quiz/reportCard/Failed_Quiz_Report.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }


}
