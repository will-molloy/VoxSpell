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
import voxspell.dailyChallenges.ChallengeType;
import voxspell.dailyChallenges.DailyChallengeGUIController;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the popup view when the user wants to add a word list in the word list editor.
 * <p>
 * Extends WordListEditorController for access to @FXML components.
 *
 * @author Will Molloy
 */
public class AddWordListPopupController extends WordListEditorController implements Initializable {

    private static WordListEditorController wordListEditorInstance;
    @FXML
    private Button addBtnPU, removeBtnPU;
    @FXML
    private Button addOrUpdateListBtn;
    private ObservableList<Word> data;
    @FXML
    private TextField categoryNameField, wordField, definitionField;
    @FXML
    private TableView<Word> wordListTableView;
    private boolean modify;

    /**
     * Sets the wordlist editor controller instance to save word lists from this popup.
     */
    static void setWordListEditorInstance(WordListEditorController wordListEditorInstance) {
        AddWordListPopupController.wordListEditorInstance = wordListEditorInstance;
    }

    /**
     * Sets the data of the expanded pane within the word list editor scene.
     */
    public void setData(TitledPane expandedPane, boolean modify) {
        this.modify = modify;
        if (expandedPane != null) {
            TableView selectedTable = (TableView) expandedPane.getContent();
            List<Word> tableData = new ArrayList<>(selectedTable.getItems());
            categoryNameField.setText(expandedPane.getText());
            categoryNameField.setEditable(!modify);
            data.addAll(tableData);
        }
        // Components are only available after the controller initialises, need runLater() for this code
        Platform.runLater(() -> {
            if (modify) {
                addOrUpdateListBtn.setText("Update List");
                categoryNameField.setEditable(false);
                wordField.requestFocus();
            } else {
                addOrUpdateListBtn.setText("Add List");
                categoryNameField.requestFocus();
            }
        });
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

        loadBtnIcons();
    }

    @FXML
    private void handleAddListBtn(ActionEvent actionEvent) {
        String category = categoryNameField.getText().trim();
        if (category.equals("")) {
            showCategoryFieldIsEmptyPopup();
        } else if (data.size() == 0) {
            showEmptyListPopup();
        } else {
            if (!categoryIsValid(category) && !modify) {
                showCategoryAlreadyExistsPopup(category);
            } else {
                List<Word> words = new ArrayList<>(data);
                wordListEditorInstance.addCategory(category, words);
                DailyChallengeGUIController d = new DailyChallengeGUIController();
                d.updateChallenge(ChallengeType.WORD_LISTS_CREATED, 1);

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

    private void showEmptyListPopup() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Empty List");
        alert.setHeaderText("Cannot add an empty list.");
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

    private void loadBtnIcons() {
        imageLoader.loadSquareImageForBtn(addBtnPU, addIcon, 30);
        imageLoader.loadSquareImageForBtn(removeBtnPU, removeIcon, 30);
    }
}
