package me.petercsala.NagyHazi;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.util.List;

/**
 * Represents a road in the game
 */
public class Road extends Placeable {
    /**
     * The mask describing the orientation of the road
     */
    int bitMask = 0;

    /**
     * Clone the road as a placeable
     *
     * @param actualPos The position to place it at
     * @return The resulting placeable
     */
    @Override
    protected Placeable clone(TilePos actualPos) {
        Road copy = new Road();
        copy.name = name;
        copy.cost = cost;
        copy.description = description;
        copy.sprite = sprite;
        copy.actualPos = actualPos;
        copy.size = size;
        copy.decorProvided = decorProvided;
        return copy;
    }

    /**
     * Virtual copy constructor pattern
     *
     * @param placeable The placeable to copy
     */
    @Override
    protected void cloneFrom(Placeable placeable) {
        if (!(placeable instanceof Road)) {
            throw new RuntimeException("Trying to load an invalid road!");
        }

        name = placeable.name;
        cost = placeable.cost;
        description = placeable.description;
        sprite = placeable.sprite;
        size = placeable.size;
        decorProvided = placeable.decorProvided;
    }

    /**
     * Draw the road
     *
     * @param ctx    The graphics context
     * @param camera The scene camera
     */
    @Override
    protected void drawSelf(GraphicsContext ctx, Camera camera) {
        Point position = camera.worldToCameraSpace(actualPos.getRealPos());
        ctx.drawImage(sprite, (bitMask % 4) * 16, (float) ((bitMask / 4) * 16), 16, 16, position.x, position.y, 16, 16);
    }

    /**
     * Get the happiness from the area decor
     *
     * @return The happiness
     */
    @Override
    protected double getDecorHappiness() {
        return 1;
    }

    /**
     * Get the happiness from the provided water
     *
     * @return The happiness
     */
    @Override
    protected double getWaterHappiness() {
        return 1;
    }

    /**
     * Get the happiness from the handled waste
     *
     * @return The happiness
     */
    @Override
    protected double getWasteHappiness() {
        return 1;
    }

    /**
     * Get the happiness from the provided electricity
     *
     * @return The happiness
     */
    @Override
    protected double getElectricityHappiness() {
        return 1;
    }

    /**
     * Get the base happiness
     *
     * @return The happiness
     */
    @Override
    protected double getBaseHappiness() {
        return 1;
    }

    /**
     * Get the happiness multiplier
     *
     * @return The happiness
     */
    @Override
    public double getHappinessMultiplier() {
        return 0;
    }

    /**
     * Get the number of people living on the road (doesn't make much sense now, does it?)
     *
     * @return The number of people
     */
    @Override
    public long getResidents() {
        return 0;
    }

    /**
     * Update the road
     *
     * @param map The map
     */
    @Override
    public void update(Map map) {
        List<TilePos> neighbours = getNeighbours();

        bitMask = 0;
        int i = 0;
        for (TilePos neighbourPos : neighbours) {
            Tile neighbour = map.getTile(neighbourPos);
            if (neighbour != null && neighbour.isRoad()) {
                bitMask |= 1 << i;
            }
            i++;
        }

    }

    /**
     * Reset the resourced inside the road
     */
    @Override
    public void resetResources() {
    }

    /**
     * Pipe some water into the road
     *
     * @param capacity The capacity of the pipes
     * @return The remaining capacity
     */
    @Override
    public long pipeWater(long capacity) {
        return capacity;
    }

    /**
     * Handle some waste from the road
     *
     * @param capacity The capacity of the pipes
     * @return The remaining capacity
     */
    @Override
    public long handleWaste(long capacity) {
        return capacity;
    }

    /**
     * Add some decor to the area
     *
     * @param decor The decor to add
     */
    @Override
    public void addDecor(long decor) {

    }

    /**
     * Get the water production
     *
     * @return The water production
     */
    @Override
    public long getWaterProduction() {
        return 0;
    }

    /**
     * Get the waste production
     *
     * @return The waste production
     */
    @Override
    public long getWasteProduction() {
        return 0;
    }

    /**
     * Get the electricity production
     *
     * @return The electricity production
     */
    @Override
    public long getElectricityProduction() {
        return 0;
    }

    /**
     * Set if the electricity requirements are met
     *
     * @param set The value
     */
    @Override
    public void setElectricityProvided(boolean set) {
    }

    /**
     * Return if the road is a road
     *
     * @return If the road is a road
     */
    @Override
    public boolean isRoad() {
        return true;
    }

    /**
     * Get the graphic for the build panel button
     *
     * @return The graphic
     */
    @Override
    public ImageView getButtonGraphic() {
        ImageView imageView = new ImageView(sprite);
        imageView.setViewport(new Rectangle2D(48, 48, 16, 16));
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        return imageView;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Road)) {
            return false;
        }

        return super.equals(obj);
    }

    /**
     * Builder pattern for the road
     */
    public static class RoadBuilder implements IPlaceableBuilder {
        /**
         * The road being built
         */
        private final Road road;

        /**
         * Constructor
         *
         * @param name The name of the road
         */
        public RoadBuilder(String name) {
            road = new Road();
            road.name = name;
            road.size = new Point(1, 1);
        }

        /**
         * Set the sprite path
         *
         * @param path The path
         */
        public void setSprite(String path) {
            road.sprite = new Image(String.valueOf(getClass().getClassLoader().getResource("userResources/" + path)));
        }

        /**
         * Set the road cost
         *
         * @param cost The cost
         */
        @Override
        public void setCost(long cost) {
            road.cost = cost;
        }

        /**
         * Set the number of workplaces provided by the road
         *
         * @param workplaces The number of workplaces
         */
        @Override
        public void setWorkplaces(long workplaces) {
            throw new RuntimeException("Not a valid parameter!");
        }

        /**
         * Set the number of people living on the road
         *
         * @param accommodation The number of people in the placeable
         */
        @Override
        public void setAccommodation(long accommodation) {
            throw new RuntimeException("Not a valid parameter!");
        }

        /**
         * Set the amount of water produced by the road
         *
         * @param producedWater The amount of water produced
         */
        @Override
        public void setProducedWater(long producedWater) {
            throw new RuntimeException("Not a valid parameter!");
        }

        /**
         * Set the amount of waste produced by the road
         *
         * @param producedWaste The amount of waste produced
         */
        @Override
        public void setProducedWaste(long producedWaste) {
            throw new RuntimeException("Not a valid parameter!");
        }

        /**
         * Set the amount of electricity produced by the road
         *
         * @param producedElectricity The amount of electricity produced
         */
        @Override
        public void setProducedElectricity(long producedElectricity) {
            throw new RuntimeException("Not a valid parameter!");
        }

        /**
         * Set the decor provided by the road
         *
         * @param decor The decor provided
         */
        @Override
        public void setDecorProvided(long decor) {
            road.decorProvided = decor;
        }

        /**
         * Set the description of the road
         *
         * @param description The description
         */
        @Override
        public void setDescription(String description) {
            road.description = description;
        }

        /**
         * Build the road
         *
         * @return The result
         */
        public Road build() {
            if (road.isInvalid())
                return null;
            return road;
        }
    }
}
