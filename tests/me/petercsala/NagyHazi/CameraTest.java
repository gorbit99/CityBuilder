package me.petercsala.NagyHazi;

import javafx.scene.layout.AnchorPane;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

public class CameraTest {
    Camera camera;

    @Before
    public void setUp() throws Exception {
        Map map = new Map(5, 5);
        AnchorPane pane = new AnchorPane();
        pane.setLayoutX(5);
        pane.setLayoutY(5);
        camera = new Camera(map, pane);
    }

    @Test
    public void worldToCameraSpace() {
        camera.move(new Vec2(10, 10));
        Point original = new Point(100, 80);
        Point transformed = camera.worldToCameraSpace(original);
        Assert.assertEquals(transformed, new Point(90, 70));
    }

    @Test
    public void cameraToWorldSpace() {
        camera.move(new Vec2(10, 10));
        Point original = new Point(100, 80);
        Point transformed = camera.cameraToWorldSpace(original);
        Assert.assertEquals(transformed, new Point(110, 90));
    }

    @Test
    public void windowToCameraSpace() {
        camera.move(new Vec2(10, 10));
        Point original = new Point(100, 80);
        Point transformed = camera.windowToCameraSpace(original);
        Assert.assertEquals(transformed, new Point(95, 75));
    }
}