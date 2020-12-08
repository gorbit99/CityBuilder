package me.petercsala.NagyHazi;

/**
 * Represents a 2d vector
 */
public class Vec2 {
    /**
     * The x component
     */
    public float x;
    /**
     * The y component
     */
    public float y;

    /**
     * Constructor
     * @param x The x component
     * @param y The y component
     */
    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Add a vector to this one
     * @param other The other vector
     * @return The result
     */
    public Vec2 add(Vec2 other) {
        x += other.x;
        y += other.y;
        return this;
    }

    /**
     * Add two vectors
     * @param a The first vector
     * @param b The second vector
     * @return The result
     */
    public static Vec2 add(Vec2 a, Vec2 b) {
        return new Vec2(a.x + b.x, a.y + b.y);
    }

    /**
     * Is the vector equal to an object
     * @param obj The object to check
     * @return Are they equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vec2)) {
            return false;
        }

        Vec2 other = (Vec2) obj;
        return other.x == x && other.y == y;
    }
}
