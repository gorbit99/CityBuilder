package me.petercsala.NagyHazi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

public class BuildingTest {
    Building building;

    @Before
    public void setUp() {
        building = new Building();
        building.name = "Test";
        building.description = "Test building";
        building.size = new Point(2, 2);
        building.cost = 1000;
        building.decorProvided = 5;
    }

    @Test
    public void testClone() {
        Placeable clone = building.clone(new TilePos(1, 1));
        Assert.assertEquals(building, clone);
    }

    @Test
    public void cloneFrom() {
        Building clone = new Building();
        clone.cloneFrom(building);
        Assert.assertEquals(building, clone);
    }

    @Test
    public void isRoad() {
        Assert.assertFalse(building.isRoad());
    }
}