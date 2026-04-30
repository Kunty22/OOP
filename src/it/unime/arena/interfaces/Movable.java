package it.unime.arena.interfaces;

import it.unime.arena.engine.Direction;

/** Anything that can change its position on the board. */
public interface Movable {
    void move(Direction direction);
}
