package me.petercsala.NagyHazi;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Represents the game map
 */
public class Map implements Serializable {
    /**
     * The size of the map
     */
    private final Point mapSize;

    /**
     * The tiles inside the map
     */
    private final Tile[][] map;

    /**
     * The current population
     */
    private long population = 0;
    /**
     * The current happiness
     */
    private double happiness = 0;
    /**
     * The current balance
     */
    private long money = 1000;

    /**
     * Constructor
     *
     * @param width  The width of the map
     * @param height The height of the map
     */
    public Map(int width, int height) {
        mapSize = new Point(width, height);

        map = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = new Tile();
            }
        }
    }

    /**
     * Get the map size
     *
     * @return The map size
     */
    public Point getMapSize() {
        return mapSize;
    }

    /**
     * Can a given placeable be placed on the given point?
     *
     * @param placeable The placeable to check
     * @param pos       The point to check
     * @return If the placeable can be placed
     */
    public boolean canPlace(Placeable placeable, TilePos pos) {
        if (pos.x < 0 || pos.x + placeable.getSize().x >= mapSize.x
                || pos.y < 0 || pos.y + placeable.getSize().y >= mapSize.y) {
            return false;
        }
        if (placeable.cost > money) {
            return false;
        }
        for (int dx = 0; dx < placeable.getSize().x; dx++) {
            for (int dy = 0; dy < placeable.getSize().y; dy++) {
                if (!map[pos.x + dx][pos.y + dy].isBuildable()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Place the given placeable on the given point
     *
     * @param placeable The placeable to place
     * @param pos       The point to place it at
     */
    public void place(Placeable placeable, TilePos pos) {
        Placeable copy = placeable.clone(pos);

        for (int dx = 0; dx < copy.getSize().x; dx++) {
            for (int dy = 0; dy < copy.getSize().y; dy++) {
                map[pos.x + dx][pos.y + dy].place(copy);
            }
        }

        updateWithNeighbours(pos);
        updateResourceFlow();

        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                Placeable other = map[x][y].getPlaceable();
                if (other != null && other.actualPos.equals(new TilePos(x, y))) {
                    long deltaDecor = copy.getProvidedDecorAt(other.getCenter());
                    other.addDecor(deltaDecor);
                    if (!copy.actualPos.equals(new TilePos(x, y))) {
                        copy.addDecor(other.getProvidedDecorAt(copy.getCenter()));
                    }
                }
            }
        }

        recalculateHappiness();

        population += copy.getResidents();

        money -= placeable.cost;
    }

    /**
     * Remove a placeable from a given point
     *
     * @param pos The position to remove it from
     */
    public void remove(TilePos pos) {
        if (pos.x < 0 || pos.x >= mapSize.x
                || pos.y < 0 || pos.y >= mapSize.y) {
            return;
        }
        Placeable placeable = map[pos.x][pos.y].getPlaceable();
        if (placeable == null) {
            return;
        }
        pos = placeable.getActualPos();
        for (int dx = 0; dx < placeable.getSize().x; dx++) {
            for (int dy = 0; dy < placeable.getSize().y; dy++) {
                map[pos.x + dx][pos.y + dy].place(null);
            }
        }

        updateWithNeighbours(pos);
        updateResourceFlow();

        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                Placeable other = map[x][y].getPlaceable();
                if (other != null && other.actualPos.equals(new TilePos(x, y))) {
                    long deltaDecor = placeable.getProvidedDecorAt(other.getCenter());
                    other.addDecor(-deltaDecor);
                }
            }
        }

        recalculateHappiness();

        population -= placeable.getResidents();
    }

    /**
     * Recalculate the happiness of the area
     */
    private void recalculateHappiness() {
        happiness = 0;
        double happinessSum = 0;
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                Placeable placeable = getTile(new TilePos(x, y)).getPlaceable();
                if (placeable == null || !placeable.getActualPos().equals(new TilePos(x, y))) {
                    continue;
                }
                happiness += placeable.getHappiness();
                happinessSum += placeable.getHappinessMultiplier();
            }
        }
        if (happinessSum != 0) {
            happiness /= happinessSum;
        } else {
            happiness = 0;
        }
    }

    /**
     * Update a cell with its neighbours
     *
     * @param pos The position to update around
     */
    private void updateWithNeighbours(TilePos pos) {
        Tile[] toUpdate = new Tile[]{
                getTile(pos),
                getTile(TilePos.add(pos, new TilePos(0, -1))),
                getTile(TilePos.add(pos, new TilePos(-1, 0))),
                getTile(TilePos.add(pos, new TilePos(0, 1))),
                getTile(TilePos.add(pos, new TilePos(1, 0)))
        };

        for (Tile updateTile : toUpdate) {
            if (updateTile != null) {
                updateTile.update(this);
            }
        }
    }

    /**
     * Get the tile at a given position
     *
     * @param pos The position to get the tile from
     * @return The tile at that position
     */
    public Tile getTile(TilePos pos) {
        if (pos.x < 0 || pos.x >= mapSize.x
                || pos.y < 0 || pos.y >= mapSize.y) {
            return null;
        }
        return map[pos.x][pos.y];
    }

    /**
     * Draw the map
     *
     * @param ctx    The graphics context
     * @param camera The scene camera
     */
    public void drawSelf(GraphicsContext ctx, Camera camera) {
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                Placeable placeable = map[x][y].getPlaceable();
                if (placeable == null) {
                    continue;
                }
                if (placeable.actualPos.equals(new TilePos(x, y))) {
                    placeable.drawSelf(ctx, camera);
                }
            }
        }
    }

    /**
     * Transform world space to tile space
     *
     * @param worldSpace The original point
     * @return The tile position
     */
    public TilePos worldToTileSpace(Point worldSpace) {
        return new TilePos(worldSpace.x / 16, worldSpace.y / 16);
    }

    /**
     * Update the resources
     */
    private void updateResourceFlow() {
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                map[x][y].resetResources();
            }
        }

        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                spreadResourceFrom(new TilePos(x, y));
            }
        }

        boolean[][] traversed = new boolean[mapSize.x][mapSize.y];
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                if (traversed[x][y]) {
                    continue;
                }
                if (!(map[x][y].isRoad())) {
                    continue;
                }

                List<Placeable> inLoop = new ArrayList<>();

                long electricityProduction = 0;
                Queue<TilePos> toVisit = new LinkedList<>();
                toVisit.add(new TilePos(x, y));
                while (!toVisit.isEmpty()) {
                    TilePos current = toVisit.remove();
                    Placeable placeable = getTile(current).getPlaceable();
                    if (placeable == null) {
                        continue;
                    }
                    inLoop.add(placeable);
                    if (!placeable.isRoad() || traversed[current.x][current.y]) {
                        continue;
                    }

                    List<TilePos> neighbours = placeable.getNeighbours();
                    for (TilePos neighbour : neighbours) {
                        if (getTile(neighbour) == null) {
                            continue;
                        }
                        electricityProduction += getTile(neighbour).getElectricityProduction();
                        toVisit.add(neighbour);
                    }
                    traversed[current.x][current.y] = true;
                }

                for (Placeable placeable : inLoop) {
                    placeable.setElectricityProvided(electricityProduction >= 0);
                }
            }
        }
    }

    /**
     * Get the happiness of the area
     *
     * @return The happiness
     */
    public double getHappiness() {
        return happiness;
    }

    /**
     * Get the population of the area
     *
     * @return The population
     */
    public long getPopulation() {
        return population;
    }

    /**
     * Collect taxes from the people
     */
    public void collectTaxes() {
        money += (population * happiness) * 100 + 300;
    }

    /**
     * Get the balance of the player
     *
     * @return The balance
     */
    public long getMoney() {
        return money;
    }

    /**
     * Try to spread resources from a given tile
     *
     * @param pos The point to spread resources from
     */
    private void spreadResourceFrom(TilePos pos) {
        if (getTile(pos).getPlaceable() == null || !getTile(pos).getPlaceable().getActualPos().equals(pos)) {
            return;
        }
        long waterProduction = getTile(pos).getWaterProduction();
        long wasteProduction = getTile(pos).getWasteProduction();
        if (waterProduction <= 0 && wasteProduction >= 0) {
            return;
        }

        List<TilePos> neighbours = map[pos.x][pos.y].getPlaceable().getNeighbours();

        List<Road> roads = new ArrayList<>();
        for (TilePos neighbour : neighbours) {
            Tile tile = getTile(neighbour);
            if (tile == null) {
                continue;
            }
            if (tile.isRoad()) {
                roads.add((Road) getTile(neighbour).getPlaceable());
            }
        }

        for (Road road : roads) {
            long waterLeft = waterProduction / roads.size();
            long wasteLeft = wasteProduction / roads.size();
            Queue<TilePos> toVisit = new LinkedList<>();
            toVisit.add(road.actualPos);
            boolean[][] traversed = new boolean[mapSize.x][mapSize.y];
            while (!toVisit.isEmpty() && (waterLeft > 0 || wasteLeft < 0)) {
                TilePos current = toVisit.remove();
                if (traversed[current.x][current.y] || !(getTile(current).isRoad())) {
                    continue;
                }
                List<TilePos> roadNeighbours = getTile(current).getPlaceable().getNeighbours();
                for (TilePos roadNeighbour : roadNeighbours) {
                    if (getTile(roadNeighbour) == null) {
                        continue;
                    }
                    waterLeft = getTile(roadNeighbour).pipeWater(waterLeft);
                    wasteLeft = getTile(roadNeighbour).handleWaste(wasteLeft);
                    toVisit.add(roadNeighbour);
                }
                traversed[current.x][current.y] = true;
            }
        }
    }

    /**
     * Reset the map
     */
    public void reset() {
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                map[x][y].place(null);
            }
        }
        population = 0;
        happiness = 0;
        money = 1000;
    }
}
