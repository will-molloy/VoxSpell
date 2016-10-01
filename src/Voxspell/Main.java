package Voxspell;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;

        Parent mainMenu = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));


        window.setTitle("VoxSpell");
        window.setScene(new Scene(mainMenu, 640, 480));
        window.show();
    }
/*
    private void makeButtons() {
        spellingQuizBtn = new Button("Spelling Quiz");
        spellingQuizBtn.setOnAction(e -> window.setScene(spellingQuiz));

        viewStatsBtn = new Button("View Statistics");
        viewStatsBtn.setOnAction(e -> window.setScene(viewHistoryStats));

        returnToMainMenuBtn = new Button("Return to the Main Menu");
        returnToMainMenuBtn.setOnAction(e -> window.setScene(mainMenu));
    }
*/

    public static void main(String[] args) {
        launch(args);
    }
}
