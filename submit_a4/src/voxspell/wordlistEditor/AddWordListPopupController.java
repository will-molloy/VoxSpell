package voxspell.wordlistEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    private static WordListEditorController wordListEditorInstance;
    private ObservableList<Word> data;
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

    public void setData(TitledPane expandedPane, boolean modify) {
        TableView selectedTable = (TableView) expandedPane.getContent();
        List<Word> tableData = new ArrayList<>(selectedTable.getItems());
        categoryNameField.setText(expandedPane.getText());
        categoryNameField.setEditable(!modify);
        data.addAll(tableData);
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
        String category = categoryNameField.getText().trim();
        if (category.equals("")) {
            showCategoryFieldIsEmptyPopup();
            return;
        }
        List<Word> words = new ArrayList<>(data);
        wordListEditorInstance.addCategory(category, words);
        // ERROR if no words in list OR category name is blank
        Main.hidePopup();
    }

    private void showCategoryFieldIsEmptyPopup() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Category Name Not Set");
        alert.setHeaderText("Please set a category name.");
        alert.showAndWait();
    }

    @FXML
    private void handleCancelBtn(ActionEvent actionEvent) {
        Main.hidePopup();
    }

    @FXML
    private void handleAddWordBtn(ActionEvent actionEvent) {
        String name = wordField.getText().trim();
        if (name.equals("")) {
            showWordFieldIsEmptyPopup();
            return;
        } else {
            String definition = definitionField.getText().trim();
            Word word = new Word(name, definition);
            data.add(word);
        }
        wordField.clear();
        definitionField.clear();
    }

    private void showWordFieldIsEmptyPopup() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Word Field Is Empty");
        alert.setHeaderText("Cannot add an empty word.");
        alert.showAndWait();
    }

    @FXML
    private void handleRemoveWordBtn(ActionEvent actionEvent) {
        Word highlightedWord = wordListTableView.getSelectionModel().getSelectedItem();
        data.remove(highlightedWord);
    }


}
