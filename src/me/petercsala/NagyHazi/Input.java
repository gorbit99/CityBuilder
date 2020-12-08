package me.petercsala.NagyHazi;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A class holding the keyboard and mouse state
 */
public class Input {
    /**
     * The mouse button states
     */
    HashMap<MouseButton, ButtonState> buttons = new HashMap<>();
    /**
     * The keyboard state
     */
    HashMap<KeyCode, KeyState> keys = new HashMap<>();
    /**
     * The position of the mouse
     */
    private final Point mousePos = new Point();
    /**
     * The mouse position in the previous frame
     */
    private Point prevMousePos = new Point();

    /**
     * Constructor
     *
     * @param boundScene The scene the input is bound to
     * @param boundNode  The node the input is bound to
     */
    public Input(Scene boundScene, Node boundNode) {
        boundScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (!keys.containsKey(keyEvent.getCode())) {
                keys.put(keyEvent.getCode(), new KeyState());
            }
            KeyState state = keys.get(keyEvent.getCode());
            if (!state.held) {
                state.pressed = true;
                state.held = true;
            }
            keys.put(keyEvent.getCode(), state);
        });
        boundScene.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (!keys.containsKey(keyEvent.getCode())) {
                keys.put(keyEvent.getCode(), new KeyState());
            }
            KeyState state = keys.get(keyEvent.getCode());
            if (state.held) {
                state.released = true;
                state.held = false;
            }
            keys.put(keyEvent.getCode(), state);
        });
        boundScene.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (!mouseEvent.getPickResult().getIntersectedNode().equals(boundNode)) {
                return;
            }
            if (!buttons.containsKey(mouseEvent.getButton())) {
                buttons.put(mouseEvent.getButton(), new ButtonState());
            }
            ButtonState state = buttons.get(mouseEvent.getButton());
            if (!state.held) {
                state.pressed = true;
                state.held = true;
            }
            state.clickCount = mouseEvent.getClickCount();
            buttons.put(mouseEvent.getButton(), state);
        });
        boundScene.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            if (!buttons.containsKey(mouseEvent.getButton())) {
                buttons.put(mouseEvent.getButton(), new ButtonState());
            }
            ButtonState state = buttons.get(mouseEvent.getButton());
            if (state.held) {
                state.released = true;
                state.held = false;
            }
            state.clickCount = mouseEvent.getClickCount();
            buttons.put(mouseEvent.getButton(), state);
        });
        boundScene.addEventHandler(MouseEvent.MOUSE_MOVED, mouseEvent -> mousePos.setLocation(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
        boundNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> mousePos.setLocation(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
    }

    /**
     * Get the state of the given key
     *
     * @param keyCode The keycode to look for
     * @return The state of the key
     */
    public KeyState getKey(KeyCode keyCode) {
        if (!keys.containsKey(keyCode)) {
            keys.put(keyCode, new KeyState());
        }
        return keys.get(keyCode);
    }

    /**
     * Get the state of the given mouse button
     *
     * @param button The button to look for
     * @return The state of the button
     */
    public ButtonState getButton(MouseButton button) {
        if (!buttons.containsKey(button)) {
            buttons.put(button, new ButtonState());
        }
        return buttons.get(button);
    }

    /**
     * Get the mouse position
     *
     * @return The mouse position
     */
    public Point getMousePos() {
        return mousePos;
    }

    /**
     * Get the mouse movement since the last frame
     *
     * @return The mouse movement
     */
    public Point getMouseDelta() {
        return new Point(mousePos.x - prevMousePos.x, mousePos.y - prevMousePos.y);
    }

    /**
     * Reset the state of the input after a frame
     */
    public void reset() {
        for (Map.Entry<KeyCode, KeyState> entry : keys.entrySet()) {
            KeyState state = entry.getValue();
            state.pressed = false;
            state.released = false;
            entry.setValue(state);
        }
        for (Map.Entry<MouseButton, ButtonState> entry : buttons.entrySet()) {
            ButtonState state = entry.getValue();
            state.pressed = false;
            state.released = false;
            entry.setValue(state);
        }
        prevMousePos = (Point) mousePos.clone();
    }

    /**
     * Represents the state of a mouse button
     */
    static class ButtonState {
        /**
         * Was the button just pressed
         */
        public boolean pressed;
        /**
         * Is the button held
         */
        public boolean held;
        /**
         * Was the button just released
         */
        public boolean released;
        /**
         * The number of clicks
         */
        public int clickCount;
    }

    /**
     * Represents the state of a keyboard key
     */
    static class KeyState {
        /**
         * Was the key just pressed
         */
        public boolean pressed;
        /**
         * Is the key held
         */
        public boolean held;
        /**
         * Was the key just released
         */
        public boolean released;
    }
}
