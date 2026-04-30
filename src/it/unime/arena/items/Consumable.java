package it.unime.arena.items;

import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.InsufficientResourceException;

/** Items that are used once and gone. */
public interface Consumable {
    void consume(Player player) throws InsufficientResourceException;
    boolean isConsumed();
}
