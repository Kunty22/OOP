package it.unime.arena.entities;

/** Obstacle that can be broken by damage. */
public class DestructibleObstacle extends Obstacle {
    private static final long serialVersionUID = 1L;

    public DestructibleObstacle(String name, int toughness) {
        super(name, toughness);
    }

    public boolean isDestroyed() { return !isAlive(); }
}
