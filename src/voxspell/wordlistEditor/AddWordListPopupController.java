package voxspell.wordlistEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.Main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the popup view when the user wants to add a word list in the word list editor.
 *
 * @author Will Molloy
 */
public class AddWordListPopupController implements Initializable {

    private static String categoryText;
    private static ObservableList<Word> data;
    private static WordListEditorController wordListEditorInstance;
    @FXML
    private TextField wordField;
    @FXML
    private TextField definitionField;
    @FXML
    private TextField categoryNameField;
    @FXML
    private TableView<Word> wordListTableView;

    static void setWordListEditorInstance(WordListEditorController wordListEditorInstance) {
        AddWordListPopupController.wordListEditorInstance = wordListEditorInstance;
    }

    /**
     * Bind data in table to an ObservableList
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data = FXCollections.observableArrayList();

        TableColumn<Word, String> wordCol = new TableColumn<>("Word");
        wordCol.setMinWidth(100);
        wordCol.setSortable(false);
        wordCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Word, String> defCol = new TableColumn<>("Definition");
        defCol.setMinWidth(300);
        defCol.setSortable(false);
        defCol.setCellValueFactory(new PropertyValueFactory<>("definition"));

        wordListTableView.setItems(data);
        wordListTableView.getColumns().addAll(wordCol, defCol);
    }

    @FXML
    private void handleAddListBtn(ActionEvent actionEvent) {
        String category = categoryNameField.getText();
        List<Word> words = new ArrayList<>(data);
        wordListEditorInstance.addWordList(category, words);
        // TODO error if list is less than 10 in size
        Main.hidePopup();
    }


    @FXML
    private void handleCancelBtn(ActionEvent actionEvent) {
        Main.hidePopup();
    }

    @FXML
    private void handleAddWordBtn(ActionEvent actionEvent) {
        if (wordField.getText().trim().equals("")) {
            // TODO Let user know no word is entered.. Fine is definition is blank
        } else {
            String name = wordField.getText();
            String definition = definitionField.getText();

            Word word = new Word(name, definition);
            data.add(word);
        }

        wordField.clear();
        definitionField.clear();
    }

    @FXML
    private void handleRemoveWordBtn(ActionEvent actionEvent) {
        Word highlightedWord = wordListTableView.getSelectionModel().getSelectedItem();
        data.remove(highlightedWord);
    }

    public void setData(TitledPane expandedPane) {
        TableView selectedTable = (TableView) expandedPane.getContent();
        List<Word> tableData = new ArrayList<>(selectedTable.getItems());
        categoryNameField.setText(expandedPane.getText());
        data.addAll(tableData);
    }
}
