package it.unime.arena.strategy;

import it.unime.arena.engine.Direction;
import it.unime.arena.engine.Position;

/** Default: 1 tile per move. */
public class StandardMovementStrategy implements MovementStrategy {
    private static final long serialVersionUID = 1L;


    private static final int RANGE = 1;

    @Override
    public Position move(Position from, Direction direction) {
        if (from == null || direction == null) return from;
        return from.step(direction);
    }

    @Override public int getRange() { return RANGE; }
    @Override public String getName() { return "Standard"; }
}
