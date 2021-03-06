package me.petercsala.NagyHazi;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The "add building" menu
 */
public class AddBuilding implements Initializable {
    /**
     * The root node
     */
    @FXML
    private AnchorPane root;
    /**
     * The pane containing the building data
     */
    @FXML
    private GridPane buildingControls;
    /**
     * The image showing the selected image
     */
    @FXML
    private ImageView spriteView;
    /**
     * The name of the placeable
     */
    @FXML
    private TextField name;
    /**
     * The path to the selected sprite
     */
    @FXML
    private TextField spritePath;
    /**
     * The cost of the placeable
     */
    @FXML
    private TextField cost;
    /**
     * The decor value of the placeable
     */
    @FXML
    private TextField decor;
    /**
     * The description of the placeable
     */
    @FXML
    private TextArea description;
    /**
     * The checkbox saying if the placeable is a road
     */
    @FXML
    private CheckBox isRoad;
    /**
     * The number of workplaces provided by the building
     */
    @FXML
    private TextField workplaces;
    /**
     * The number of people living in the building
     */
    @FXML
    private TextField accommodation;
    /**
     * The amount of water provided by the building
     */
    @FXML
    private TextField water;
    /**
     * The amount of waste produced by the building
     */
    @FXML
    private TextField waste;
    /**
     * The amount of electricity generated by the building
     */
    @FXML
    private TextField electricity;

    /**
     * Initialize the scene
     *
     * @param url            ignored
     * @param resourceBundle ignored
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spritePath.textProperty().addListener(this::spritePathChange);
        isRoad.selectedProperty().addListener(this::roadToggle);
    }

    public void openSprite() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("All Images", "*.*"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileChooser.setInitialDirectory(new File("./userResources/sprites"));
        fileChooser.setTitle("Open Image");
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            spritePath.setText(new File("./userResources").toURI().relativize(file.toURI()).getPath());
        }
    }

    /**
     * The function ran when the player presses the "Add placeable" button
     */
    public void addPlaceable() {
        if (isFieldInvalidNumber(cost)) {
            showError("Cost is not a number!");
            return;
        }
        long costL = Long.parseLong(cost.getText());
        if (costL <= 0) {
            showError("Cost must be positive!");
            return;
        }
        if (!decor.getText().isEmpty() && isFieldInvalidNumber(decor)) {
            showError("Decor is not a number!");
            return;
        }
        if (name.getText().isBlank()) {
            showError("The name must contain non whitespace characters!");
            return;
        }
        if (description.getText().isBlank()) {
            showError("The description must contain non whitespace characters!");
            return;
        }
        if (spritePath.getText().isBlank()) {
            showError("The sprite path must be set!");
            return;
        }

        if (!isRoad.selectedProperty().get()) {
            if (!workplaces.getText().isEmpty()) {
                if (isFieldInvalidNumber(workplaces)) {
                    showError("Workplaces is not a number!");
                    return;
                }
                long workplacesL = Long.parseLong(workplaces.getText());
                if (workplacesL < 0) {
                    showError("Workplaces must be positive!");
                    return;
                }
            }
            if (!accommodation.getText().isEmpty()) {
                if (isFieldInvalidNumber(accommodation)) {
                    showError("Accommodation is not a number!");
                    return;
                }
                long accommodationL = Long.parseLong(accommodation.getText());
                if (accommodationL < 0) {
                    showError("Accommodation must be positive!");
                    return;
                }
            }
            if (!water.getText().isEmpty() && isFieldInvalidNumber(water)) {
                showError("Water is not a number!");
                return;
            }
            if (!waste.getText().isEmpty() && isFieldInvalidNumber(waste)) {
                showError("Waste is not a number!");
                return;
            }
            if (!electricity.getText().isEmpty() && isFieldInvalidNumber(electricity)) {
                showError("Electricity is not a number!");
                return;
            }
        }

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            String documentPath = String.valueOf(getClass().getClassLoader().getResource("./userResources/placeables.xml"));
            Document document = builder.parse(documentPath);
            document.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            Element root = document.getDocumentElement();

            Element newPlaceable = document.createElement(isRoad.selectedProperty().get() ? "road" : "building");
            newPlaceable.setAttribute("value", name.getText());
            Element descriptionElement = document.createElement("description");
            descriptionElement.setAttribute("value", description.getText());
            newPlaceable.appendChild(descriptionElement);
            Element costElement = document.createElement("cost");
            costElement.setAttribute("value", cost.getText());
            newPlaceable.appendChild(costElement);
            Element spriteElement = document.createElement("sprite");
            spriteElement.setAttribute("value", spritePath.getText());
            newPlaceable.appendChild(spriteElement);
            if (!decor.getText().isEmpty()) {
                Element decorElement = document.createElement("decor");
                decorElement.setAttribute("value", decor.getText());
                newPlaceable.appendChild(decorElement);
            }
            if (!isRoad.selectedProperty().get()) {
                if (!accommodation.getText().isEmpty()) {
                    Element accommodationElement = document.createElement("accommodation");
                    accommodationElement.setAttribute("value", accommodation.getText());
                    newPlaceable.appendChild(accommodationElement);
                }
                if (!workplaces.getText().isEmpty()) {
                    Element workplacesElement = document.createElement("workplaces");
                    workplacesElement.setAttribute("value", workplaces.getText());
                    newPlaceable.appendChild(workplacesElement);
                }
                if (!water.getText().isEmpty()) {
                    Element waterElement = document.createElement("water");
                    waterElement.setAttribute("value", water.getText());
                    newPlaceable.appendChild(waterElement);
                }
                if (!waste.getText().isEmpty()) {
                    Element wasteElement = document.createElement("waste");
                    wasteElement.setAttribute("value", waste.getText());
                    newPlaceable.appendChild(wasteElement);
                }
                if (!electricity.getText().isEmpty()) {
                    Element electricityElement = document.createElement("electricity");
                    electricityElement.setAttribute("value", electricity.getText());
                    newPlaceable.appendChild(electricityElement);
                }
            }
            root.appendChild(newPlaceable);

            DOMSource source = new DOMSource(document);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult streamResult = new StreamResult(documentPath);
            transformer.transform(source, streamResult);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException | XPathExpressionException e) {
            e.printStackTrace();
        }

        back();
    }

    /**
     * Helper function to determine, if a field is an invalid number or not
     *
     * @param field The field to check
     * @return Does it contain an invalid number
     */
    public boolean isFieldInvalidNumber(TextField field) {
        try {
            Long.parseLong(field.getText());
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    /**
     * The function called, when the sprite path changes
     *
     * @param observable ignored
     * @param oldValue   ignored
     * @param newValue   The value entered
     */
    public void spritePathChange(Observable observable, String oldValue, String newValue) {
        Image image = new Image(String.valueOf(getClass().getClassLoader().getResource("./userResources/" + newValue)), 200, 200, true, false);

        if (image.isError()) {
            showError("Couldn't load image!");
        }
        spriteView.setImage(image);
    }

    /**
     * Helper function to show an error popup
     *
     * @param error The message in the popup
     */
    private void showError(String error) {
        Popup popup = new Popup();
        VBox vbox = new VBox();
        Label errorMessage = new Label();
        errorMessage.setText(error);
        Button okButton = new Button();
        okButton.setText("Ok");
        vbox.getChildren().add(errorMessage);
        vbox.getChildren().add(okButton);
        vbox.setAlignment(Pos.CENTER);
        popup.getContent().add(vbox);
        okButton.setOnAction(actionEvent -> popup.hide());
        popup.show(root.getScene().getWindow());
    }

    /**
     * The function called, when the "Is Road" checkbox is toggled
     *
     * @param observable ignored
     * @param oldValue   ignored
     * @param newValue   The current value of it
     */
    private void roadToggle(Observable observable, boolean oldValue, boolean newValue) {
        buildingControls.setVisible(!newValue);
    }

    /**
     * The function called, when the player presses the "Back" button
     */
    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("./fxml/mainMenu.fxml"));
            loader.load();
            Parent mainMenuRoot = loader.getRoot();
            Scene mainMenu = new Scene(mainMenuRoot);
            ((Stage) root.getScene().getWindow()).setScene(mainMenu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
