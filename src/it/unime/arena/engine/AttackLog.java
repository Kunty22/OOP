package it.unime.arena.engine;

import it.unime.arena.entities.Entity;

/**
 * Strategy-style hook used by the AI to report attacks back to whoever
 * is observing (the GUI prints to a log; tests could collect events).
 *
 * Keeping the engine free of any UI imports demonstrates the
 * "program to an interface, not an implementation" principle.
 */
@FunctionalInterface
public interface AttackLog {
    void record(Entity attacker, Entity target, int damage);
}
