package me.petercsala.NagyHazi;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.Point;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The game class
 */
public class Game implements Initializable {
    /**
     * The image representing the happiness of the population
     */
    @FXML
    private ImageView happinessImage;
    /**
     * The root node in the scene
     */
    @FXML
    private Node root;
    /**
     * The canvas the game is drawn to
     */
    @FXML
    private Canvas canvas;
    /**
     * The pone holding the canvas object
     */
    @FXML
    private AnchorPane canvasHolder;
    /**
     * The panel holding the game controls
     */
    @FXML
    private AnchorPane buildPanel;
    /**
     * The panel golding the buildings
     */
    @FXML
    private TilePane buildingContainer;
    /**
     * The panel holding the roads
     */
    @FXML
    private TilePane roadContainer;
    /**
     * The label representing the date
     */
    @FXML
    private Label dateLabel;
    /**
     * The label displaying the population
     */
    @FXML
    private Label populationLabel;
    /**
     * The label displaying the money the player has
     */
    @FXML
    private Label moneyLabel;

    /**
     * The list of possible placeables
     */
    static List<Placeable> placeables;
    /**
     * The game map
     */
    Map map;
    /**
     * The game camera
     */
    Camera camera;
    /**
     * The game loop timer
     */
    AnimationTimer gameLoopTimer;
    /**
     * The graphics context
     */
    GraphicsContext ctx;
    /**
     * The input system
     */
    Input input;
    /**
     * The id of the selected placeable
     */
    int selectedId = -1;
    /**
     * The current game time
     */
    GameDate gameDate = new GameDate();
    /**
     * The possible images for representing population happiness
     */
    Image[] happinessImages;

    /**
     * The animation of the build panel
     */
    TranslateTransition buildPanelSlide = new TranslateTransition();

    /**
     * The movement speed of the camera
     */
    final float cameraSpeed = 400;

    /**
     * Initialize the scene
     *
     * @param url            ignored
     * @param resourceBundle ignored
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Pane pane = (Pane) canvas.getParent();
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        placeables = Placeable.loadPlaceables(Objects.requireNonNull(getClass().getClassLoader().getResource("userResources/placeables.xml")).getPath());
        if (placeables == null) {
            throw new RuntimeException("Couldn't load placeables!");
        }

        for (int i = 0; i < placeables.size(); i++) {
            Placeable placeable = placeables.get(i);
            Button button = new Button();
            button.setGraphic(placeable.getButtonGraphic());
            button.setMaxSize(16, 16);
            button.setPadding(new Insets(0));
            int finalI = i;
            button.setOnAction(actionEvent -> selectedId = finalI);
            if (placeable.isRoad()) {
                roadContainer.getChildren().add(button);
            } else {
                buildingContainer.getChildren().add(button);
            }
        }

        map = new Map(100, 100);
        camera = new Camera(map, canvasHolder);

        gameLoopTimer = new AnimationTimer() {
            long previousTime = System.nanoTime();

            @Override
            public void handle(long time) {
                float elapsedTime = (time - previousTime) / 1e9f;
                update(elapsedTime);
                render();
                previousTime = time;
            }
        };

        ctx = canvas.getGraphicsContext2D();

        Rectangle buildPanelClipRect = new Rectangle(0, 0, 0, 0);
        buildPanelClipRect.widthProperty().bind(buildPanel.widthProperty());
        buildPanelClipRect.heightProperty().bind(buildPanel.heightProperty());
        buildPanel.getParent().setClip(buildPanelClipRect);

        buildPanelSlide.setNode(buildPanel);
        buildPanelSlide.setFromX(0);
        buildPanelSlide.toXProperty().bind(buildPanel.widthProperty().negate());
        happinessImages = new Image[]{
                new Image(String.valueOf(getClass().getResource("fxml/images/veryunhappy.png"))),
                new Image(String.valueOf(getClass().getResource("fxml/images/happy.png"))),
                new Image(String.valueOf(getClass().getResource("fxml/images/neutral.png"))),
                new Image(String.valueOf(getClass().getResource("fxml/images/happy.png"))),
                new Image(String.valueOf(getClass().getResource("fxml/images/veryhappy.png"))),
        };

        gameDate.registerEvent(() -> map.collectTaxes());
    }

    /**
     * Get a placeable with a given name
     *
     * @param name The name of the placeable
     * @return The found placeable or null
     */
    public static Placeable getPlaceable(String name) {
        for (Placeable placeable : placeables) {
            if (placeable.name.equals(name)) {
                return placeable;
            }
        }
        return null;
    }

    /**
     * Start the game
     */
    public void start() {
        input = new Input(root.getScene(), canvas);
        gameLoopTimer.start();
    }

    /**
     * The update function
     *
     * @param elapsedTime The time since the last frame
     */
    private void update(float elapsedTime) {
        float x = 0;
        float y = 0;
        if (input.getKey(KeyCode.W).held) {
            y = -elapsedTime * cameraSpeed;
        }
        if (input.getKey(KeyCode.S).held) {
            y += elapsedTime * cameraSpeed;
        }
        if (input.getKey(KeyCode.A).held) {
            x = -elapsedTime * cameraSpeed;
        }
        if (input.getKey(KeyCode.D).held) {
            x += elapsedTime * cameraSpeed;
        }
        camera.move(new Vec2(x, y));

        if (selectedId != -1) {
            Point windowSpace = input.getMousePos();
            Point cameraSpace = camera.windowToCameraSpace(windowSpace);
            Point worldSpace = camera.cameraToWorldSpace(cameraSpace);
            TilePos tileSpace = map.worldToTileSpace(worldSpace);
            if (input.getButton(MouseButton.PRIMARY).held) {
                if (map.canPlace(placeables.get(selectedId), tileSpace)) {
                    map.place(placeables.get(selectedId), tileSpace);
                }
            }
            if (input.getButton(MouseButton.SECONDARY).held) {
                map.remove(tileSpace);
            }
            if (input.getButton(MouseButton.MIDDLE).held) {
                camera.move(new Vec2(-input.getMouseDelta().x, -input.getMouseDelta().y));
            }
        } else {
            if (input.getButton(MouseButton.PRIMARY).held || input.getButton(MouseButton.MIDDLE).held) {
                camera.move(new Vec2(-input.getMouseDelta().x, -input.getMouseDelta().y));
            }
        }

        gameDate.advance(elapsedTime);

        if (input.getKey(KeyCode.A).pressed) {
            System.out.println("Hello");
            ObjectOutputStream outputStream;
            try {
                outputStream = new ObjectOutputStream(new FileOutputStream("test.dat"));
                outputStream.writeObject(map);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (input.getKey(KeyCode.B).pressed) {
            System.out.println("Load");
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("test.dat"));
                map = (Map) inputStream.readObject();
                inputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        input.reset();
    }

    /**
     * The render function
     *
     */
    private void render() {
        ctx.setFill(Color.rgb(5, 77, 0));
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        map.drawSelf(ctx, camera);
        dateLabel.setText(gameDate.getDateString());
        int happinessId = (int) (map.getHappiness() * 6);
        if (happinessId == 5) {
            happinessId = 4;
        }
        happinessImage.setImage(happinessImages[happinessId]);
        populationLabel.setText(String.valueOf(map.getPopulation()));
        moneyLabel.setText(String.valueOf(map.getMoney()));
    }

    /**
     * Ran, when the player pressed the build button
     */
    public void buildPressed() {
        buildPanelSlide.setRate(-buildPanelSlide.getRate());
        buildPanelSlide.play();
    }

    /**
     * Ran, when the player pressed the camera movement button
     */
    public void panPressed() {
        selectedId = -1;
        if (buildPanelSlide.getRate() == -1) {
            buildPanelSlide.setRate(1);
            buildPanelSlide.play();
        }
    }

    /**
     * Save the game
     *
     * @param path The path to save the game at
     */
    public void saveGame(String path) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
            outputStream.writeObject(map);
            outputStream.writeObject(gameDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the game
     *
     * @param path The path to load the game from
     */
    public void loadGame(String path) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
            map = (Map) inputStream.readObject();
            gameDate = (GameDate) inputStream.readObject();
            gameDate.registerEvent(() -> map.collectTaxes());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ran, when the load option is selected
     */
    @FXML
    private void loadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save File", "*.cbs")
        );
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            loadGame(file.getPath());
        }
    }

    /**
     * Ran, when the save button is selected
     */
    @FXML
    private void saveButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save File", "*.cbs")
        );
        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        if (file == null) {
            return;
        }
        String path = file.getPath();
        if (!path.endsWith(".cbs")) {
            path = path + ".cbs";
        }
        saveGame(path);
    }

    /**
     * Ran, when the new game button is pressed
     */
    @FXML
    private void reset() {
        map.reset();
        gameDate = new GameDate();
        gameDate.registerEvent(() -> map.collectTaxes());
    }

    /**
     * Ran, when the quit button is selected
     */
    @FXML
    private void quit() {
        ((Stage) root.getScene().getWindow()).close();
    }

    /**
     * Ran, when the about button is selected
     */
    @FXML
    private void about() {
        Popup popup = new Popup();
        Label text = new Label();
        text.setText("Citybuilder, made by Péter Csala, 2020");
        VBox vBox = new VBox();
        Button okButton = new Button();
        okButton.setText("Ok");
        vBox.getChildren().add(text);
        vBox.getChildren().add(okButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        okButton.setOnAction((actionEvent) -> popup.hide());
        popup.getContent().add(vBox);
        popup.show(root.getScene().getWindow());
    }
}