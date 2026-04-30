package it.unime.arena.interfaces;

import it.unime.arena.entities.Entity;

/**
 * Anything that can attack another entity.
 *
 * Note: this is the only interface that depends on the entities package,
 * because by definition an attack involves an Entity target.
 */
public interface Attacker {
    void attack(Entity target);
}
