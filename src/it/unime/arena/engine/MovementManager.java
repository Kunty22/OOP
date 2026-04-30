package it.unime.arena.engine;

import it.unime.arena.entities.Entity;
import it.unime.arena.exceptions.InvalidMoveException;

/**
 * Validates and performs movement. Owns board boundaries — no other
 * class needs to know how big the board is at runtime.
 */
public class MovementManager {

    private final int width;
    private final int height;

    public MovementManager() { this(Position.BOARD_WIDTH, Position.BOARD_HEIGHT); }

    public MovementManager(int width, int height) {
        this.width  = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public int  getWidth()  { return width; }
    public int  getHeight() { return height; }

    public boolean isInside(Position p) {
        if (p == null) return false;
        return p.getX() >= 0 && p.getX() < width
            && p.getY() >= 0 && p.getY() < height;
    }

    public void moveEntity(Entity entity, Direction direction) throws InvalidMoveException {
        if (entity == null)        throw new InvalidMoveException("Entity is null");
        if (direction == null)     throw new InvalidMoveException("Direction is null");
        if (!entity.isAlive())     throw new InvalidMoveException(entity.getName() + " is dead");
        // Position is already self-clamping, so movement is always inside the grid.
        entity.move(direction);
    }
}
