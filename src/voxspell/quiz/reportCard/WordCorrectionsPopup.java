package voxspell.quiz.reportCard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.Main;

import java.util.List;

/**
 * Popup shown when the user wants to see the correct spelling for words.
 *
 * @author Will Molloy
 */
public class WordCorrectionsPopup {

    @FXML
    private ScrollPane scrollPane;

    public void handleDoneBtn(ActionEvent actionEvent) {
        Main.hidePopup();
    }

    public void createTable(List<WordCorrection> corrections) {
        TableView tableView = new TableView();

        ObservableList<WordCorrection> data = FXCollections.observableArrayList(corrections);

        TableColumn<WordCorrection, String> attemptCol = new TableColumn<>("Your Attempt");
        attemptCol.setSortable(false);
        attemptCol.setMinWidth(240);
        attemptCol.setCellValueFactory(new PropertyValueFactory<>("attempt"));

        TableColumn<WordCorrection, String> wordCol = new TableColumn<>("Correct Spelling");
        wordCol.setSortable(false);
        wordCol.setMinWidth(240);
        wordCol.setCellValueFactory(new PropertyValueFactory<>("word"));

        // no horizontal scroll
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        tableView.setItems(data);
        tableView.getColumns().addAll(attemptCol, wordCol);

        scrollPane.setContent(tableView);
    }
}
