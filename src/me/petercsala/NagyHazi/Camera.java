package me.petercsala.NagyHazi;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.awt.*;

/**
 * A camera in the scene
 */
public class Camera {
    /**
     * The position of the camera
     */
    private final Vec2 position;
    /**
     * The size of the camera rectangle
     */
    private final Rectangle bounds;
    /**
     * The pane the camera is bound to
     */
    private final Pane boundPane;

    /**
     * Constructor
     *
     * @param map       The map in the scene
     * @param boundPane The pane the camera is bound to
     */
    public Camera(Map map, Pane boundPane) {
        position = new Vec2(0, 0);
        Point mapSize = map.getMapSize();
        bounds = new Rectangle(0, 0, mapSize.x * 16, mapSize.y * 16);
        this.boundPane = boundPane;
    }

    /**
     * Move the camera
     *
     * @param offset The amount to move it by
     */
    public void move(Vec2 offset) {
        position.x = Math.max(bounds.x, Math.min(bounds.width + bounds.x - (int) boundPane.getWidth(), position.x + offset.x));
        position.y = Math.max(bounds.y, Math.min(bounds.height + bounds.y - (int) boundPane.getHeight(), position.y + offset.y));
    }

    /**
     * Transform a point in the world to be relative to the camera
     *
     * @param worldSpace The original point
     * @return The point in camera space
     */
    public Point worldToCameraSpace(Point worldSpace) {
        return new Point((int) (worldSpace.x - position.x), (int) (worldSpace.y - position.y));
    }

    /**
     * Transform a point from being relative to the camera to world space
     *
     * @param cameraSpace The original point
     * @return The resulting point
     */
    public Point cameraToWorldSpace(Point cameraSpace) {
        return new Point((int) (cameraSpace.x + position.x), (int) (cameraSpace.y + position.y));
    }

    /**
     * Transform a point from window to camera space
     *
     * @param windowSpace The original point
     * @return The resulting point
     */
    public Point windowToCameraSpace(Point windowSpace) {
        Point2D p = new Point2D(windowSpace.x, windowSpace.y);
        Point2D transformed = boundPane.sceneToLocal(p);
        return new Point((int) transformed.getX(), (int) transformed.getY());
    }
}
