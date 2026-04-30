package it.unime.arena.strategy;

import it.unime.arena.entities.Entity;
import it.unime.arena.interfaces.Identifiable;

import java.io.Serializable;

/**
 * Strategy pattern: encapsulates the algorithm that decides how much damage
 * one Entity inflicts on another.
 *
 * OOP principles demonstrated:
 *  - Composition over inheritance: an Entity HAS-AN AttackStrategy rather
 *    than overriding attack() in every subclass.
 *  - Open-Closed Principle: new attack styles plug in without modifying
 *    Entity, Player, or Creature.
 *  - Delegation: Entity.attack() delegates the math to its strategy.
 */
public interface AttackStrategy extends Identifiable, Serializable {
    /**
     * @return the damage that {@code attacker} deals to {@code target} this
     *         hit, after applying any randomness or special effects.
     *         Must be >= 0.
     */
    int calculateDamage(Entity attacker, Entity target);
}
