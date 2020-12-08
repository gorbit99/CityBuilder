package me.petercsala.NagyHazi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main menu of the game
 */
public class MainMenu {
    /**
     * The root node in the scene
     */
    @FXML
    private Node root;

    /**
     * Ran, when the player chooses the quit button
     */
    @FXML
    private void quit() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    /**
     * Ran, when the player chooses the play button
     */
    @FXML
    private void playButton() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("./fxml/game.fxml"));
            loader.load();
            Parent gameRoot = loader.getRoot();
            Scene game = new Scene(gameRoot);
            ((Stage) root.getScene().getWindow()).setScene(game);
            ((Game) loader.getController()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ran, when the player chooses the "Add Building" button
     */
    @FXML
    private void addBuildingButton() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("./fxml/addBuilding.fxml"));
            loader.load();
            Parent addBuildingRoot = loader.getRoot();
            Scene addBuilding = new Scene(addBuildingRoot);
            ((Stage) root.getScene().getWindow()).setScene(addBuilding);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
