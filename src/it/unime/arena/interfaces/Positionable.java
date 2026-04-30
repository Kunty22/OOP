package it.unime.arena.interfaces;

import it.unime.arena.engine.Position;

/** Anything that has a location on the board. */
public interface Positionable {
    Position getPosition();
}
