package me.petercsala.NagyHazi;

import org.junit.Assert;

import java.awt.*;

public class MapTest {
    Map map;

    @org.junit.Before
    public void setUp() {
        map = new Map(5, 7);
    }

    @org.junit.Test
    public void getMapSize() {
        Point size = map.getMapSize();
        Assert.assertEquals(5, size.x);
        Assert.assertEquals(7, size.y);
    }

    @org.junit.Test
    public void canPlace() {
        Building building = new Building();
        building.size = new Point(2, 2);
        Assert.assertTrue(map.canPlace(building, new TilePos(1, 1)));
        Assert.assertFalse(map.canPlace(building, new TilePos(4, 4)));
    }

    @org.junit.Test
    public void place() {
        Building building = new Building();
        building.size = new Point(2, 2);
        map.place(building, new TilePos(1, 1));
        Assert.assertEquals(building, map.getTile(new TilePos(2, 2)).getPlaceable());
    }

    @org.junit.Test
    public void remove() {
        Building building = new Building();
        building.size = new Point(2, 2);
        map.place(building, new TilePos(1, 1));
        map.remove(new TilePos(1, 2));
        Assert.assertNull(map.getTile(new TilePos(1, 1)).getPlaceable());
    }

    @org.junit.Test
    public void worldToTileSpace() {
        TilePos result = map.worldToTileSpace(new Point(45, 30));
        Assert.assertEquals(result, new TilePos(2, 1));
    }
}