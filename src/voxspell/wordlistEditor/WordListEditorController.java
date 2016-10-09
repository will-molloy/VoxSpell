package voxspell.wordlistEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import voxspell.Main;
import voxspell.tools.CustomFileReader;
import voxspell.tools.WordDefinitionFinder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls the Word List Editor.
 *
 * @author Will Molloy
 */
public class WordListEditorController implements Initializable {

    private static final String WORD_LIST_NAME = ".wordList";
    private static final File wordListFile = new File(WORD_LIST_NAME);
    private static List<WordList> wordLists = new ArrayList<>();

    private CustomFileReader fileReader = new CustomFileReader();

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

        System.out.println("WOW");
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

    private void sortAndPointLists(){
        sortLists();
        pointLists();
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
    }

    private void createGUI() {
        wordLists.forEach(this::addWordListToTableInTitledView);
    }

    private void addWordListToTableInTitledView(WordList wordList) {
        TableView tableView = new TableView();
        TitledPane titledPane = new TitledPane();

        final ObservableList<Word> data = FXCollections.observableArrayList(wordList.wordList());

        TableColumn<Word, String> wordCol = new TableColumn<>("Word");
        wordCol.setSortable(false);
        wordCol.setMinWidth(150);
        wordCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Word, String> defCol = new TableColumn<>("Definition");
        defCol.setSortable(false);
        defCol.setMinWidth(450);
        defCol.setCellValueFactory(new PropertyValueFactory<>("definition"));

        tableView.setItems(data);
        tableView.getColumns().addAll(wordCol, defCol);

        titledPane.setText(wordList.toString());
        titledPane.setContent(tableView);

        wordListsView.getPanes().add(titledPane);
    }

    @FXML
    private void handleRmvBtn(ActionEvent actionEvent) {
        TitledPane expandedPane = wordListsView.getExpandedPane();
        removeListFromDataGUIAndFile(expandedPane);
    }

    private void removeListFromDataGUIAndFile(TitledPane wordListShownInGUI){
        // Remove from data
        wordLists.remove(wordListsView.getPanes().indexOf(wordListShownInGUI));

        // Remove from GUI
        wordListsView.getPanes().remove(wordListShownInGUI);

        // Remove from file
        removeWordListFromFile(wordListShownInGUI.getText());
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
    private void handleRmvAllBtn(ActionEvent actionEvent){
        wordLists = new ArrayList<>(); // delete data
        wordListsView.getPanes().removeAll(wordListsView.getPanes()); // delete panes
        wordListFile.delete(); // remake file
        makeHiddenWordListFile();
    }

    @FXML
    private void handleAddBtn(ActionEvent actionEvent) {
        try {
            Parent addWordListPopupRoot = FXMLLoader.load(getClass().getResource("../fxml/Add_Word_List.fxml"));
            AddWordListPopupController.setWordListEditorInstance(this);
            Scene scene = new Scene(addWordListPopupRoot);
            Main.showPopup(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifyBtn(ActionEvent actionEvent) {
        TitledPane expandedPane = wordListsView.getExpandedPane();
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent addWordListPopupRoot = loader.load(getClass().getResource("Add_Word_List.fxml").openStream());
            AddWordListPopupController controller = loader.getController();
            controller.setData(expandedPane);
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
    }


    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    public void addWordList(String category, List<Word> wordList) {
        WordList newList = new WordList(category);
        newList.wordList().addAll(wordList);

        addWordListToDataGUIAndFile(newList);
    }

    private void addWordListToDataGUIAndFile(WordList wordList){
        // Data
        wordLists.add(wordList);
        sortAndPointLists();

        // GUI
        addWordListToTableInTitledView(wordList);

        // File
        fileReader.addWordListToFile(wordList, wordListFile);
    }

    /*
 * Generate definitions for all words on a background thread.
 */
    @FXML
    private void handleGenerateDefBtn(ActionEvent actionEvent) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int max = 0;
                int current = 0;
                for (WordList wordList : wordLists) {
                    max += wordList.size();
                }
                for (WordList wordList : wordLists) {
                    System.out.println("List: " + wordList.toString());
                    for (Word word : wordList.wordList()) {
                        if (isCancelled()) {
                            break;
                        }
                        if (word.getDefinition().equals("")) {
                            word.setDefinition(WordDefinitionFinder.getDefinition(word.toString()));
                        }
                        updateProgress(++current, max);
                        System.out.println("Word: " + word + " Def: " + word.getDefinition());
                    }
                } // TODO fix ?progress bar

                // Sync wordlist data with word list file (add in defintions)
                fileReader.syncWordListDataWithFile(wordLists, wordListFile);
                return null;
            }
        };
        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }



}
