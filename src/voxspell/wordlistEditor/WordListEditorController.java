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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import voxspell.Main;
import voxspell.tools.WordDefinitionFinder;

import java.io.*;
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

    @FXML
    private Accordion wordListsView;

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
        sortLists();
        createGUI();
        pointLists();

        System.out.println("WOW");
    }

    private void makeHiddenWordListFile() {
        List<String> lines = Collections.singletonList("");
        Path file = Paths.get(WORD_LIST_NAME);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readWordListFileIntoList() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(WORD_LIST_NAME));
            String line;
            WordList wordList = null;

            while ((line = bufferedReader.readLine()) != null) {
                String[] wordAndDef = line.split("\\t+"); // word and definition are seperated by a tab within the hidden file.

                if (line.startsWith("%")) { // TODO messes up if last list in file. . .
                    if (wordList != null) { // null check for first iteration
                        wordLists.add(wordList);
                    }
                    wordList = new WordList(line.substring(1, line.length())); // set name of wordList
                } else {
                    Word word = new Word(wordAndDef[0]);
                    if (wordAndDef.length > 1) { // definition detected
                        word.setDefinition(wordAndDef[1]);
                    }
                    wordList.addWord(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortLists() {
        for (WordList s : wordLists) {
            Collections.sort(s.wordList());
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

    private void pointLists() {
        for (int i = 0; i < wordLists.size() - 1; i++) {
            wordLists.get(i).setNextList(wordLists.get(i + 1));
        }
    }

    @FXML
    private void handleRmvBtn(ActionEvent actionEvent) {
        TitledPane expandedPane = wordListsView.getExpandedPane();
        TableView selectedTable = (TableView) expandedPane.getContent();
        List<Word> tableData = new ArrayList<>(selectedTable.getItems());

        // Remove from GUI
        wordListsView.getPanes().remove(expandedPane);

        // Remove from file
        removeWordListFromFile(expandedPane.getText());

        // Remove from data
        wordLists.remove(tableData);
    }

    private void removeWordListFromFile(String wordListTitle) { // TODO NEED TO MUTLITHREAD, fails if removing too quickly
        try {
            File tempFile = new File(".wordListCopy");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile, false));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(wordListFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                readCategory:
                {
                    if (line.equals("%" + wordListTitle)) {
                        // Found category to remove, don't copy it to the temp file
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.startsWith("%")) { // TODO fails if EOF .. (won't remove last list?) maybe it will .. . TEST
                                break readCategory;
                            }
                        }
                    }
                }
                // Copy over lines from original file to temp file
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();

            // Rename files
            tempFile.renameTo(wordListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBtn(ActionEvent actionEvent) {
        try {
            Parent addWordListPopupRoot = FXMLLoader.load(getClass().getResource("Add_Word_List.fxml"));
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
        File file = Main.showFileChooserAndReturnChosenFile(fileChooser);

        System.out.println(file.getName());

        //      File file = new File(chosenFile);

    }


    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
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
                } // TODO progress bar
                return null;
            }
        };

        Thread thread = new Thread(task);
        ProgressBar bar = new ProgressBar();
        bar.progressProperty().bind(task.progressProperty());
        thread.setDaemon(true);
        thread.start();
    }

    public void addWordList(String category, List<Word> wordList) {
        WordList newList = new WordList(category);
        newList.wordList().addAll(wordList);

        // Add list to file
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(wordListFile, true));
            bufferedWriter.write("%" + category);
            bufferedWriter.newLine();
            for (Word word : wordList) {
                bufferedWriter.write(word + "\t" + word.getDefinition());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add list to data
        wordLists.add(newList);

        // Add list to GUI
        addWordListToTableInTitledView(newList);
    }


}
