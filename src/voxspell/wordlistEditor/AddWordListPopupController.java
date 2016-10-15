package voxspell.wordlistEditor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.Main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the popup view when the user wants to add a word list in the word list editor.
 *
 * Extends WordListEditorController for access to @FXML components. (The Help Btn)
 *
 * @author Will Molloy
 */
public class AddWordListPopupController extends WordListEditorController implements Initializable {

    private static WordListEditorController wordListEditorInstance;
    @FXML
    private Button addOrUpdateListBtn;
    private ObservableList<Word> data;
    @FXML
    private TextField categoryNameField, wordField, definitionField;
    @FXML
    private TableView<Word> wordListTableView;
    private boolean modify;

    static void setWordListEditorInstance(WordListEditorController wordListEditorInstance) {
        AddWordListPopupController.wordListEditorInstance = wordListEditorInstance;
    }

    public void setData(TitledPane expandedPane, boolean modify) {
        this.modify = modify;
        TableView selectedTable = (TableView) expandedPane.getContent();
        List<Word> tableData = new ArrayList<>(selectedTable.getItems());
        categoryNameField.setText(expandedPane.getText());
        categoryNameField.setEditable(!modify);
        data.addAll(tableData);

        if (modify){
            addOrUpdateListBtn.setText("Update List");
            categoryNameField.setEditable(false);
            Platform.runLater(() -> wordField.requestFocus()); // Need to run requestFocus() after controller initialises
        } else {
            addOrUpdateListBtn.setText("Add List");
            categoryNameField.requestFocus();
        }
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
        } else {
            if (!categoryIsValid(category) && !modify) {
                showCategoryAlreadyExistsPopup(category);
            } else {
                List<Word> words = new ArrayList<>(data);
                wordListEditorInstance.addCategory(category, words);
                // ERROR if no words in list OR category name is blank
                Main.hidePopup();
            }
        }
    }

    private void showCategoryFieldIsEmptyPopup() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Category Name Not Set");
        alert.setHeaderText("Please set a category name.");
        alert.showAndWait();
    }

    /**
     * Determines if a given category is valid.
     * A category is valid if it doesn't already exist.
     */
    private boolean categoryIsValid(String candidate) {
        for (WordList wordList : WordListEditorController.getWordLists()) {
            if (wordList.toString().equals(candidate)) {
                return false;
            }
        }
        return true;
    }

    private void showCategoryAlreadyExistsPopup(String category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Category Already Exists");
        alert.setHeaderText(category + " already exists as a word category.");
        alert.setContentText("Overwrite " + category + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            modify = true; // switch to modifying mode
            handleAddListBtn(null);
        }
    }

    @FXML
    private void handleCancelBtn(ActionEvent actionEvent) {
        Main.hidePopup();
    }

    private void showWordFieldIsEmptyPopup() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Word Field Is Empty");
        alert.setHeaderText("Cannot add an empty word.");
        alert.showAndWait();
    }

    @FXML
    private void handleCategoryNameField(ActionEvent actionEvent) {
        if (categoryNameField.getText().trim().equals("")) {
            showCategoryFieldIsEmptyPopup();
        } else {
            wordField.requestFocus();
        }
    }

    @FXML
    private void handleWordField(ActionEvent actionEvent) {
        if (wordFieldIsValid()) {
            definitionField.requestFocus();
        } else {
            showWordFieldIsEmptyPopup();
        }
    }

    private boolean wordFieldIsValid() {
        return !wordField.getText().trim().equals("");
    }

    @FXML
    private void handleDefField(ActionEvent actionEvent) {
        if (wordFieldIsValid()) {
            String wordName = wordField.getText().trim();
            String wordDef = definitionField.getText().trim();
            Word word = new Word(wordName, wordDef);
            data.add(word);

            wordField.clear();
            definitionField.clear();

            wordField.requestFocus();
        } else {
            showWordFieldIsEmptyPopup();
        }
    }

    @FXML
    private void handleDelBtn(ActionEvent actionEvent) {
        Word highlightedWord = wordListTableView.getSelectionModel().getSelectedItem();
        data.remove(highlightedWord);
    }
}
