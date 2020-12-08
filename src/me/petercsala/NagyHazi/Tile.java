package me.petercsala.NagyHazi;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a tile on the map
 */
public class Tile implements Serializable {
    /**
     * The placeable on the tile
     */
    private Placeable placeable;

    /**
     * Constructor
     */
    public Tile() {
        placeable = null;
    }

    /**
     * Get the placeable
     *
     * @return The placeable
     */
    public Placeable getPlaceable() {
        return placeable;
    }

    /**
     * Get if the tile can be built on
     *
     * @return Is it buildable
     */
    public boolean isBuildable() {
        return placeable == null;
    }

    /**
     * Place a placeable on the tile
     *
     * @param placeable The placeable to place
     */
    public void place(Placeable placeable) {
        this.placeable = placeable;
    }

    /**
     * Update the tile
     *
     * @param map The map
     */
    public void update(Map map) {
        if (placeable != null) {
            placeable.update(map);
        }
    }

    /**
     * Reset the resources inside the placeable
     */
    public void resetResources() {
        if (placeable != null) {
            placeable.resetResources();
        }
    }

    /**
     * Pipe some water into the placeable
     *
     * @param capacity The capacity of the pipe
     * @return The remaining capacity
     */
    public long pipeWater(long capacity) {
        if (placeable == null) {
            return capacity;
        }
        return placeable.pipeWater(capacity);
    }

    /**
     * Handle some waste from the placeable
     *
     * @param capacity The capacity of the pipe
     * @return The remaining capacity
     */
    public long handleWaste(long capacity) {
        if (placeable == null) {
            return capacity;
        }
        return placeable.handleWaste(capacity);
    }

    /**
     * Get the water production of the placeable
     *
     * @return The water production
     */
    public long getWaterProduction() {
        if (placeable == null) {
            return 0;
        }
        return placeable.getWaterProduction();
    }

    /**
     * Get the waste production of the placeable
     *
     * @return The waste production
     */
    public long getWasteProduction() {
        if (placeable == null) {
            return 0;
        }
        return placeable.getWasteProduction();
    }

    /**
     * Get the electricity provided by the placeable
     *
     * @return The electricity provided
     */
    public long getElectricityProduction() {
        if (placeable == null) {
            return 0;
        }
        return placeable.getElectricityProduction();
    }

    /**
     * Is the placeable a road
     *
     * @return Is it a road
     */
    public boolean isRoad() {
        if (placeable == null) {
            return false;
        }
        return placeable.isRoad();
    }
}

/**
 * Represents a tile position
 */
class TilePos implements Serializable {
    /**
     * The x position
     */
    public int x;
    /**
     * The y position
     */
    public int y;

    /**
     * Constructor
     *
     * @param x The x position
     * @param y The y position
     */
    public TilePos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the real x position
     *
     * @return The real x position
     */
    public int getRealX() {
        return x * 16;
    }

    /**
     * Get the real y position
     *
     * @return The real y position
     */
    public int getRealY() {
        return y * 16;
    }

    /**
     * Get the real position
     *
     * @return The real position
     */
    public Point getRealPos() {
        return new Point(getRealX(), getRealY());
    }

    /**
     * Add an offset to this point
     *
     * @param other The offset
     * @return The result
     */
    public TilePos add(TilePos other) {
        x += other.x;
        y += other.y;
        return this;
    }

    /**
     * Add two tile positions together
     *
     * @param a The first position
     * @param b The second position
     * @return The result
     */
    public static TilePos add(TilePos a, TilePos b) {
        return new TilePos(a.x + b.x, a.y + b.y);
    }

    /**
     * Is this position equal to an object
     *
     * @param obj The object to check
     * @return Are they equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TilePos)) {
            return false;
        }

        TilePos other = (TilePos) obj;
        return other.x == x && other.y == y;
    }
}
