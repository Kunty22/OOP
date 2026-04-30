package it.unime.arena.strategy;

import it.unime.arena.engine.Direction;
import it.unime.arena.engine.Position;

/** 2 tiles per move — flyers, fast units. */
public class FastMovementStrategy implements MovementStrategy {
    private static final long serialVersionUID = 1L;


    private static final int RANGE = 2;

    @Override
    public Position move(Position from, Direction direction) {
        if (from == null || direction == null) return from;
        Position p = from;
        for (int i = 0; i < RANGE; i++) p = p.step(direction);
        return p;
    }

    @Override public int getRange() { return RANGE; }
    @Override public String getName() { return "Fast"; }
}
