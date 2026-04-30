package it.unime.arena.engine;

import it.unime.arena.entities.Entity;
import it.unime.arena.exceptions.EntityDeadException;
import it.unime.arena.exceptions.InvalidTargetException;

/**
 * Encapsulates combat rules: range checks, distance, and the attack itself.
 *
 * Demonstrates:
 *  - Single Responsibility: knows about combat, nothing else.
 *  - Delegation: defers the actual damage math to Entity.attackStrict,
 *    which in turn delegates to its AttackStrategy.
 */
public class CombatManager {

    public static final int DEFAULT_RANGE = 2;

    private final int attackRange;

    public CombatManager() { this(DEFAULT_RANGE); }

    public CombatManager(int attackRange) {
        this.attackRange = Math.max(1, attackRange);
    }

    public int getAttackRange() { return attackRange; }

    public int distance(Entity a, Entity b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        return a.getPosition().manhattanDistanceTo(b.getPosition());
    }

    public boolean inRange(Entity attacker, Entity target) {
        return distance(attacker, target) <= attackRange;
    }

    /**
     * Performs an attack with full validation. Throws when a rule is broken
     * so callers (the GUI, the AI) can react cleanly.
     */
    public void performAttack(Entity attacker, Entity target)
            throws EntityDeadException, InvalidTargetException {
        if (!inRange(attacker, target)) {
            throw new InvalidTargetException("Target is out of range");
        }
        attacker.attackStrict(target);
    }
}
