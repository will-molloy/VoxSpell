package voxspell.wordlistEditor;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import voxspell.Main;
import voxspell.tools.CustomFileReader;
import voxspell.tools.WordDefinitionFinder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controls the Word List Editor.
 *
 * @author Will Molloy
 */
public class WordListEditorController implements Initializable {

    private static final String WORD_LIST_NAME = ".wordList";
    private static final File wordListFile = new File(WORD_LIST_NAME);
    private static List<WordList> wordLists = new ArrayList<>();

    @FXML
    private Text categoriesTextField;
    private CustomFileReader fileReader = new CustomFileReader();
    @FXML
    private ImageView imageView;
    private Image imageOfAScroll = new Image(Main.class.getResourceAsStream("media/images/scroll.png"));
    @FXML
    private Button importFileBtn, generateDefBtn;
    @FXML
    private Accordion wordListsView;
    private Thread thread;

    public static List<WordList> getWordLists() {
        return wordLists;
    }

    /**
     * Parse hidden wordList file and add words to view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!wordListFile.exists()) {
            makeHiddenWordListFile();
        }
        readWordListFileIntoList();
        sortAndPointLists();
        createGUI();
        // Open first category by default
        if (wordListsView.getPanes().size() > 0) {
            wordListsView.setExpandedPane(wordListsView.getPanes().get(0));
        } else {
            setNoWordListsText();
        }
        imageView.setImage(imageOfAScroll);
        addButtonToolTips();
    }

    private void setNoWordListsText() {
        categoriesTextField.setText("VoxSpell currently has no categories.");
    }

    private void setThereAreWordListsText() {
        categoriesTextField.setText("VoxSpell has loaded these categories:");
    }

    private void addButtonToolTips() {
        importFileBtn.setTooltip(new Tooltip("Import a txt file containing the following format:\n" +
                "Begin new categories with '%'.\n" +
                "Begin words on a new line, separating definition by a tab."));
        generateDefBtn.setTooltip(new Tooltip("You must have sdcv installed for this to work, may take a few moments."));
    }

    private void makeHiddenWordListFile() {
        try {
            wordListFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readWordListFileIntoList() {
        wordLists = fileReader.readWordListFileIntoList(wordListFile);
    }

    private void sortAndPointLists() {
        if (wordLists.size() > 0) {
            sortLists();
            pointLists();
        }
    }

    private void sortLists() {
        for (WordList wordList : wordLists) {
            Collections.sort(wordList.wordList());
        }
    }

    private void pointLists() {
        for (int i = 0; i < wordLists.size() - 1; i++) {
            wordLists.get(i).setNextList(wordLists.get(i + 1));
        }
        wordLists.get(wordLists.size() - 1).setNextList(wordLists.get(0)); // wrap back to beginning
    }

    private void createGUI() {
        wordLists.forEach(this::addWordListToTableInTitledView);
        resizeWordListView();
    }

    private void resizeWordListView() {
        int minSize = wordListsView.getPanes().size() * 48 + 30;
        if (minSize < 300) {
            minSize = 300;      //  number of headers * 2 * font size + header size
        }
        wordListsView.setPrefHeight(minSize);
    }

    private void addWordListToTableInTitledView(WordList wordList) {
        TableView tableView = new TableView();
        TitledPane titledPane = new TitledPane();

        ObservableList<Word> data = FXCollections.observableArrayList(wordList.wordList());

        TableColumn<Word, String> wordCol = new TableColumn<>("Word");
        wordCol.setSortable(false);
        wordCol.setMinWidth(150);
        wordCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Word, String> defCol = new TableColumn<>("Definition");
        defCol.setSortable(false);
        defCol.setMinWidth(430);
        defCol.setCellValueFactory(new PropertyValueFactory<>("definition"));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // no horizontal scroll

        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(30));

        tableView.setItems(data);
        tableView.getColumns().addAll(wordCol, defCol);

        titledPane.setText(wordList.toString());
        titledPane.setContent(tableView);

        wordListsView.getPanes().add(titledPane);

        // Set explanation text
        setThereAreWordListsText();
    }

    @FXML
    private void handleRmvBtn(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Category?");
        alert.setHeaderText("Remove the expanded category?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            TitledPane expandedPane = wordListsView.getExpandedPane();
            removeListFromDataGUIAndFile(expandedPane);
            resizeWordListView();
        }
    }

    private void removeListFromDataGUIAndFile(TitledPane wordListShownInGUI) {
        // Remove from data
        wordLists.remove(wordListsView.getPanes().indexOf(wordListShownInGUI));

        // Remove from GUI
        wordListsView.getPanes().remove(wordListShownInGUI);

        // Remove from file
        removeWordListFromFile(wordListShownInGUI.getText());

        // Set explanation text
        if (wordLists.size() == 0) {
            setNoWordListsText();
        }
    }

    private void removeWordListFromFile(String wordListTitle) {
        // Need to run on background thread, otherwise it doesn't work if user rapidly deletes lists.
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                fileReader.removeWordListFromFile(wordListTitle, wordListFile);
                return null;
            }
        };
        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleRmvAllBtn(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Categories?");
        alert.setHeaderText("Remove all categories?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            wordLists = new ArrayList<>(); // delete data
            wordListsView.getPanes().removeAll(wordListsView.getPanes()); // delete panes
            wordListFile.delete(); // remake file
            resizeWordListView();
            makeHiddenWordListFile();
        }
        setNoWordListsText();
    }

    @FXML
    private void handleAddBtn(ActionEvent actionEvent) {
        getAddOrModifyListPopup(false);
    }

    @FXML
    private void handleModifyBtn(ActionEvent actionEvent) {
        getAddOrModifyListPopup(true);
    }

    private void getAddOrModifyListPopup(boolean modify) {
        TitledPane expandedPane = null;
        if (modify) {
            expandedPane = wordListsView.getExpandedPane();
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent addWordListPopupRoot = loader.load(getClass().getResource("fxml/Add_Word_List.fxml").openStream());
            AddWordListPopupController controller = loader.getController();
            controller.setData(expandedPane, modify);
            AddWordListPopupController.setWordListEditorInstance(this);
            Scene scene = new Scene(addWordListPopupRoot);
            Main.showPopup(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImportFileBtn(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a word list");
        File chosenFile = Main.showFileChooserAndReturnChosenFile(fileChooser);

        // Read in new lists
        List<WordList> newLists = fileReader.readWordListFileIntoList(chosenFile);

        // Append to file
        newLists.forEach(this::addWordListToDataGUIAndFile);
        resizeWordListView();
    }

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    /**
     * Adds the given Category and associated word list.
     */
    public void addCategory(String category, List<Word> wordList) {
        WordList newList = new WordList(category);
        newList.wordList().addAll(wordList);
        addWordListToDataGUIAndFile(newList);
    }

    /**
     * Adds the given word list to the Data, GUI and File.
     * If the category already exists, it overwrites.
     */
    private void addWordListToDataGUIAndFile(WordList wordList) {
        /*
         * Check category does not already exist
         */
        if (overWriteExistingWordList(wordList)) {
            return;
        }
        // Add to data
        wordLists.add(wordList);
        sortAndPointLists();

        // GUI
        addWordListToTableInTitledView(wordList);

        // File
        fileReader.addWordListToFile(wordList, wordListFile);
    }

    /**
     * Overwrite existing word list
     */
    private boolean overWriteExistingWordList(WordList wordList) {
        for (WordList wl : wordLists) {
            if (wl.toString().equals(wordList.toString())) {
                // Change data
                wl.wordList().removeAll(wl.wordList());
                wl.wordList().addAll(wordList.wordList());

                // recreate GUI - need to do this so order of panes is maintained
                wordListsView.getPanes().removeAll(wordListsView.getPanes());
                createGUI();

                // need to rewrite entire file, no way to insert lines into files
                fileReader.syncWordListDataWithFile(wordLists, wordListFile);
                return true;
            }
        }
        return false;
    }

    /**
     * Generate definitions for all words on a background thread.
     */
    @FXML
    private void handleGenerateDefBtn(ActionEvent actionEvent) {
        ProgressBarPopup progressBar = new ProgressBarPopup();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int current = 0;
                int max = 0;
                for (WordList wL : wordLists) {
                    max += wL.size();
                }

                for (WordList wordList : wordLists) {
                    for (Word word : wordList.wordList()) {
                        if (word.getDefinition().equals("")) {
                            word.setDefinition(WordDefinitionFinder.getDefinition(word.toString()));
                        }
                        updateProgress(++current, max);
                    }
                }
                // Sync wordlist data with word list file (add in definitions)
                fileReader.syncWordListDataWithFile(wordLists, wordListFile);
                return null;
            }
        };

        progressBar.activateProgressBar(task);

        task.setOnSucceeded(event -> {
            progressBar.getPopup().close();
        });
        progressBar.getPopup().show();

        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        wordListsView.setExpandedPane(wordListsView.getPanes().get(0));
    }

    @FXML
    private void handleHelpBtn(ActionEvent actionEvent) {
    }
}
