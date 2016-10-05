package voxspell.wordlistEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import voxspell.Main;
import voxspell.tools.WordDefinitionFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Accordion wordListsView;


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

    private void sortLists() {
        for (WordList s : wordLists){
            Collections.sort(s.wordList());
        }
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

                if (line.startsWith("%")) {
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

    private void createGUI() {
        final TableView[] tableViews = new TableView[wordLists.size()];
        final TitledPane[] tiltedPanes = new TitledPane[wordLists.size()];

        for (int i = 0; i < wordLists.size(); i++) {
            tableViews[i] = new TableView();
            tableViews[i].setEditable(true);
            tiltedPanes[i] = new TitledPane();

            final ObservableList<Word> data = FXCollections.observableArrayList(wordLists.get(i).wordList());

            TableColumn<Word, String> wordCol = new TableColumn<>("Word");
            wordCol.setSortable(false);
            wordCol.setMinWidth(150);
            wordCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Word, String> defCol = new TableColumn<>("Definition");
            defCol.setSortable(false);
            defCol.setMinWidth(450);
            defCol.setCellValueFactory(new PropertyValueFactory<>("definition"));

            tableViews[i].setItems(data);
            tableViews[i].getColumns().addAll(wordCol, defCol);

            tiltedPanes[i].setText(wordLists.get(i).toString());
            tiltedPanes[i].setContent(tableViews[i]);
        }

        wordListsView.getPanes().addAll(tiltedPanes);
        wordListsView.setExpandedPane(tiltedPanes[0]);
    }

    private void pointLists() {
        for (int i = 0; i < wordLists.size()-1; i++){
            wordLists.get(i).setNextList(wordLists.get(i+1));
        }
    }

    @FXML
    private void handleRmvBtn(ActionEvent actionEvent) {
    }

    @FXML
    private void handleAddBtn(ActionEvent actionEvent) {
    }

    @FXML
    private void handleImportFileBtn(ActionEvent actionEvent) {
    }

    @FXML
    private void handleBackBtn(ActionEvent actionEvent) {
        Main.showMainMenu();
    }

    @FXML
    private void handleGenerateDefBtn(ActionEvent actionEvent) {
        for (WordList wordList : wordLists){
            System.out.println("List: " + wordList.toString());
            for (Word word : wordList.wordList()){
                if (word.getDefinition().equals("")) {
                    word.setDefinition(WordDefinitionFinder.getDefinition(word.toString())); // TODO MUTLI THREAD IT
                }
                System.out.println("Word: " + word + " Def: " + word.getDefinition());
            }
        }
    }
}
