package it.unime.arena.entities;

/**
 * Static board object. Has no attack strategy, so attackStrict throws —
 * the base class enforces this contract for us.
 */
public class Obstacle extends Entity {
    private static final long serialVersionUID = 1L;

    public Obstacle(String name, int toughness) {
        super(name, toughness, /* attackStrategy = */ null);
    }
}
