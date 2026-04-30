package it.unime.arena.engine;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable 2D coordinate on the game board.
 *
 * OOP principles demonstrated:
 *  - Encapsulation: x,y are private and final; no setters.
 *  - Information hiding: clients never touch internal state directly.
 *  - Immutability: every "modifier" returns a NEW Position, so a Position
 *    handed out by a getter cannot be mutated by the caller.
 */
public final class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int BOARD_WIDTH  = 10;
    public static final int BOARD_HEIGHT = 10;

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = clamp(x, 0, BOARD_WIDTH  - 1);
        this.y = clamp(y, 0, BOARD_HEIGHT - 1);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    /** Returns a new Position translated by (dx, dy), clamped to the board. */
    public Position translate(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    /** Returns a new Position one step in the given direction. */
    public Position step(Direction d) {
        if (d == null) return this;
        switch (d) {
            case UP:    return translate(0, -1);
            case DOWN:  return translate(0,  1);
            case LEFT:  return translate(-1, 0);
            case RIGHT: return translate( 1, 0);
            default:    return this;
        }
    }

    public int manhattanDistanceTo(Position other) {
        if (other == null) return Integer.MAX_VALUE;
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // equals/hashCode are essential for using Position as a Map key
    // or comparing positions by value rather than by reference.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }

    @Override
    public String toString() { return "(" + x + "," + y + ")"; }
}
