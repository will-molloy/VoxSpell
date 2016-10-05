package voxspell.wordlistEditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import voxspell.Main;

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
public class WordListEditorController implements Initializable{

    private static final String WORD_LIST_NAME = ".wordlist";
    private static final File wordListFile = new File(WORD_LIST_NAME);

    private List<WordList> wordLists = new ArrayList<>();

    /**
     * Parse hidden wordlist file and add words to view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!wordListFile.exists()){
            makeHiddenWordListFile();
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
}
