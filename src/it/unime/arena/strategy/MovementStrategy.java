package it.unime.arena.strategy;

import it.unime.arena.engine.Direction;
import it.unime.arena.engine.Position;
import it.unime.arena.interfaces.Identifiable;

import java.io.Serializable;

/**
 * Strategy pattern: encapsulates HOW an entity moves (1 tile, 2 tiles, etc.).
 * Returns a NEW Position rather than mutating the input — works well with
 * the immutable Position class.
 */
public interface MovementStrategy extends Identifiable, Serializable {
    Position move(Position from, Direction direction);
    int getRange();
}
