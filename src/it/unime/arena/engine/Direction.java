package it.unime.arena.engine;

/**
 * The four cardinal movement directions.
 *
 * OOP principles demonstrated:
 *  - Abstraction: a Direction is a high-level concept; callers never deal
 *    with raw dx/dy pairs.
 *  - Encapsulation: each constant carries its own delta, hiding the math.
 */
public enum Direction {
    UP    ( 0, -1),
    DOWN  ( 0,  1),
    LEFT  (-1,  0),
    RIGHT ( 1,  0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }

    public static Direction fromString(String s) {
        if (s == null) return null;
        try { return Direction.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }
}
