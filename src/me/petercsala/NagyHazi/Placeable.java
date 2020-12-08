package me.petercsala.NagyHazi;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents anything placeable in the world
 */
public abstract class Placeable implements Cloneable, Serializable {
    /**
     * The sprite used by the placeable
     */
    protected Image sprite;
    /**
     * The actual tile position of the placeable
     */
    protected TilePos actualPos;
    /**
     * The size of the placeable
     */
    protected Point size;
    /**
     * The name of the placeable
     */
    protected String name;
    /**
     * The description of the placeable
     */
    protected String description;
    /**
     * The cost of the placeable
     */
    protected long cost = -1;
    /**
     * The decor provided by the placeable
     */
    protected long decorProvided = 0;

    /**
     * Clone the given placeable
     *
     * @param actualPos The position to place it at
     * @return The resulting clone
     */
    protected abstract Placeable clone(TilePos actualPos);

    /**
     * Virtual copy constructor pattern
     *
     * @param placeable The placeable to copy
     */
    protected abstract void cloneFrom(Placeable placeable);

    /**
     * Draw the placeable
     *
     * @param ctx    The graphics context
     * @param camera The scene camera
     */
    protected abstract void drawSelf(GraphicsContext ctx, Camera camera);

    /**
     * Is the placeable invalid
     *
     * @return If the placeable is invalid
     */
    protected boolean isInvalid() {
        return sprite == null || name == null || description == null || cost == -1;
    }

    /**
     * Get the actual position of the placeable
     *
     * @return The position
     */
    public TilePos getActualPos() {
        return actualPos;
    }

    /**
     * Get the size of the placeable
     *
     * @return The size
     */
    public Point getSize() {
        return size;
    }

    /**
     * Get the center of the placeable
     *
     * @return The center
     */
    public Point getCenter() {
        Point center = getActualPos().getRealPos();
        center.translate(size.x * 8, size.y * 8);
        return center;
    }

    /**
     * Get the provided decor at a given point
     *
     * @param pos The point to measure the decor at
     * @return The decor
     */
    public long getProvidedDecorAt(Point pos) {
        Point center = getCenter();
        double d = (pos.x - center.x) * (pos.x - center.x) + (pos.y - center.y) * (pos.y - center.y);
        return (long) (decorProvided * Math.exp(-d / 2 / decorProvided));
    }

    /**
     * Get the happiness from the area decor
     *
     * @return The happiness
     */
    protected abstract double getDecorHappiness();

    /**
     * Get the happiness from the water provided
     *
     * @return The happiness
     */
    protected abstract double getWaterHappiness();

    /**
     * Get the happiness from the handled waste
     *
     * @return The happiness
     */
    protected abstract double getWasteHappiness();

    /**
     * Get the happiness from the provided electricity
     *
     * @return The happiness
     */
    protected abstract double getElectricityHappiness();

    /**
     * Get the base happiness value
     *
     * @return The happiness
     */
    protected abstract double getBaseHappiness();

    /**
     * Get the happiness multiplier
     *
     * @return The multiplier
     */
    public abstract double getHappinessMultiplier();

    /**
     * Get the number of people living in the placeable
     *
     * @return The number of people
     */
    public abstract long getResidents();

    /**
     * Get the happiness value
     *
     * @return The happiness
     */
    public double getHappiness() {
        return getBaseHappiness()
                * getElectricityHappiness()
                * getWasteHappiness()
                * getWaterHappiness()
                * getDecorHappiness()
                * getHappinessMultiplier();
    }

    /**
     * Update the placeable
     *
     * @param map The map
     */
    public abstract void update(Map map);

    /**
     * Reset the resources provided to the placeable
     */
    public abstract void resetResources();

    /**
     * Pipe some water into the placeable
     *
     * @param capacity The capacity of the pipes
     * @return The remaining capacity
     */
    public abstract long pipeWater(long capacity);

    /**
     * Handle some waste from the placeable
     *
     * @param capacity The capacity of the pipes
     * @return The remaining capacity
     */
    public abstract long handleWaste(long capacity);

    /**
     * Add some decor to the area
     *
     * @param decor The decor to add
     */
    public abstract void addDecor(long decor);

    /**
     * Get the water production of the placeable
     *
     * @return The water production
     */
    public abstract long getWaterProduction();

    /**
     * Get the waste production of the placeable
     *
     * @return The waste production
     */
    public abstract long getWasteProduction();

    /**
     * Get the electricity production of the placeable
     *
     * @return The electricity production
     */
    public abstract long getElectricityProduction();

    /**
     * Set, if the electricity requirement is met
     *
     * @param set The value
     */
    public abstract void setElectricityProvided(boolean set);

    /**
     * Return if the placeable is a road
     *
     * @return Is it a road
     */
    public abstract boolean isRoad();

    /**
     * Get the neighbours of the placeable
     *
     * @return The neighbours
     */
    public List<TilePos> getNeighbours() {
        ArrayList<TilePos> neighbours = new ArrayList<>();
        for (int i = 0; i < size.x; i++) {
            neighbours.add(TilePos.add(actualPos, new TilePos(i, -1)));
        }
        for (int i = 0; i < size.x; i++) {
            neighbours.add(TilePos.add(actualPos, new TilePos(-1, i)));
        }
        for (int i = 0; i < size.x; i++) {
            neighbours.add(TilePos.add(actualPos, new TilePos(i, size.y)));
        }
        for (int i = 0; i < size.x; i++) {
            neighbours.add(TilePos.add(actualPos, new TilePos(size.x, i)));
        }
        return neighbours;
    }

    /**
     * Get the graphic used on the building panel button
     *
     * @return The graphic
     */
    public abstract ImageView getButtonGraphic();

    /**
     * Load all placeables
     *
     * @param path Path to the xml file
     * @return The resulting list
     */
    public static java.util.List<Placeable> loadPlaceables(String path) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            PlaceableLoader loader = new PlaceableLoader();
            parser.parse(new FileInputStream(path), loader);
            return loader.getResults();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if an object is equal to this
     * @param obj The other object
     * @return Are they equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Placeable)) {
            return false;
        }

        Placeable placeable = (Placeable)obj;
        return Objects.equals(placeable.description, description)
                && Objects.equals(placeable.name, name)
                && placeable.cost == cost
                && placeable.decorProvided == decorProvided
                && Objects.equals(placeable.size, size)
                && Objects.equals(placeable.sprite, sprite);
    }

    /**
     * The xml loader for the placeables
     */
    private static class PlaceableLoader extends DefaultHandler {
        /**
         * The list of placeables
         */
        java.util.List<Placeable> placeables = new ArrayList<>();
        /**
         * The builder for the current placeable
         */
        IPlaceableBuilder builder;

        /**
         * Ran for every xml element
         *
         * @param uri        ignored
         * @param localName  ignored
         * @param qName      The name of the xml node
         * @param attributes The attributes of the node
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            String value = attributes.getValue("value");
            switch (qName) {
                case "building":
                    if (builder != null) {
                        throw new RuntimeException("Badly formatted placeable.xml!");
                    }
                    builder = new Building.BuildingBuilder(value);
                    break;
                case "road":
                    if (builder != null) {
                        throw new RuntimeException("Badly formatted placeable.xml!");
                    }
                    builder = new Road.RoadBuilder(value);
                    break;
                case "description":
                    builder.setDescription(value);
                    break;
                case "sprite":
                    builder.setSprite(value);
                    break;
                case "cost":
                    builder.setCost(Long.parseLong(value));
                    break;
                case "workplaces":
                    builder.setWorkplaces(Long.parseLong(value));
                    break;
                case "accommodation":
                    builder.setAccommodation(Long.parseLong(value));
                    break;
                case "water":
                    builder.setProducedWater(Long.parseLong(value));
                    break;
                case "waste":
                    builder.setProducedWaste(Long.parseLong(value));
                    break;
                case "electricity":
                    builder.setProducedElectricity(Long.parseLong(value));
                    break;
                case "decor":
                    builder.setDecorProvided(Long.parseLong(value));
                    break;
            }
        }

        /**
         * Ran for the end of every xml element
         *
         * @param uri       ignored
         * @param localName ignored
         * @param qName     The name of the element
         */
        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("building".equals(qName) || "road".equals(qName)) {
                Placeable placeable = builder.build();
                if (placeable == null) {
                    throw new RuntimeException("The xml file is malformed!");
                }
                builder = null;
                placeables.add(placeable);
            }
        }

        /**
         * Get the results
         *
         * @return The results
         */
        public List<Placeable> getResults() {
            return placeables;
        }
    }

    /**
     * Interface for the builder pattern for placeables
     */
    public interface IPlaceableBuilder {
        /**
         * Build the placeable
         *
         * @return The resulting placeable
         */
        Placeable build();

        /**
         * Set the description
         *
         * @param description The description
         */
        void setDescription(String description);

        /**
         * Set the sprite path
         *
         * @param spritePath The path
         */
        void setSprite(String spritePath);

        /**
         * Set the cost
         *
         * @param cost The cost
         */
        void setCost(long cost);

        /**
         * Set the number of workplaces
         *
         * @param workplaces The number of workplaces
         */
        void setWorkplaces(long workplaces);

        /**
         * Set the number of people in the placeable
         *
         * @param accommodation The number of people in the placeable
         */
        void setAccommodation(long accommodation);

        /**
         * Set the water produced by the placeable
         *
         * @param producedWater The amount of water produced
         */
        void setProducedWater(long producedWater);

        /**
         * Set the produced waste
         *
         * @param producedWaste The amount of waste produced
         */
        void setProducedWaste(long producedWaste);

        /**
         * Set the produced electricity
         *
         * @param producedElectricity The amount of electricity produced
         */
        void setProducedElectricity(long producedElectricity);

        /**
         * Set the provided decor
         *
         * @param decor The decor provided
         */
        void setDecorProvided(long decor);
    }

    /**
     * Read the object from a stream
     *
     * @param inputStream The stream to read from
     * @throws ClassNotFoundException Thrown, when the underlying interface throws a ClassNotFoundException
     * @throws IOException            Thrown, when the underlying interface throws an IOException
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        name = (String) inputStream.readObject();
        actualPos = (TilePos) inputStream.readObject();
        Placeable placeable = Game.getPlaceable(name);
        if (placeable == null) {
            throw new IOException("Placeable doesn't exist!");
        }
        cloneFrom(placeable);
    }

    /**
     * Write object to a stream
     *
     * @param outputStream The stream to write to
     * @throws IOException Thrown, when the underlying interface throws an IOException
     */
    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(name);
        outputStream.writeObject(actualPos);
    }
}
