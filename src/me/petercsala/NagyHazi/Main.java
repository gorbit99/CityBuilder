package me.petercsala.NagyHazi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of the game
 */
public class Main extends Application {
    /**
     * Start the window and the game
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("./fxml/mainMenu.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene mainMenu = new Scene(root);
        primaryStage.setScene(mainMenu);
        primaryStage.setTitle("City Builder");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * The main function of the game
     *
     * @param args The args of the executable
     */
    public static void main(String[] args) {
        launch(args);
    }
}
