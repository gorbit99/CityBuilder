package me.petercsala.NagyHazi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

public class RoadTest {
    Road road;

    @Before
    public void setUp() {
        road = new Road();
        road.size = new Point(1, 1);
        road.name = "Test";
        road.description = "Test Road";
        road.cost = 100;
        road.decorProvided = 10;
    }

    @Test
    public void testClone() {
        Placeable clone = road.clone(new TilePos(1, 1));
        Assert.assertEquals(road, clone);
    }

    @Test
    public void cloneFrom() {
        Road clone = new Road();
        clone.cloneFrom(road);
        Assert.assertEquals(road, clone);
    }

    @Test
    public void isRoad() {
        Assert.assertTrue(road.isRoad());
    }
}