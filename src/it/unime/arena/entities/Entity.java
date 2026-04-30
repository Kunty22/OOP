package it.unime.arena.entities;

import it.unime.arena.engine.Direction;
import it.unime.arena.engine.Position;
import it.unime.arena.exceptions.EntityDeadException;
import it.unime.arena.exceptions.InvalidTargetException;
import it.unime.arena.interfaces.Attacker;
import it.unime.arena.interfaces.Damageable;
import it.unime.arena.interfaces.Identifiable;
import it.unime.arena.interfaces.Movable;
import it.unime.arena.interfaces.Positionable;
import it.unime.arena.strategy.AttackStrategy;
import it.unime.arena.strategy.MovementStrategy;
import it.unime.arena.strategy.StandardMovementStrategy;

import java.io.Serializable;

/**
 * Abstract base class for every object that lives on the board.
 *
 * OOP principles demonstrated by this class:
 *  - ABSTRACTION: declared abstract; cannot be instantiated directly,
 *    forcing subclasses to commit to a concrete role.
 *  - ENCAPSULATION + INFORMATION HIDING: name/health/position are private;
 *    health is exposed read-only and mutated only through takeDamage/heal.
 *  - INHERITANCE: all concrete game objects extend Entity to reuse this
 *    common state and behaviour.
 *  - COMPOSITION: an Entity HAS-AN AttackStrategy and a MovementStrategy
 *    (delegation) instead of hard-coding behaviour in subclasses.
 *  - SUBTYPING: implements 5 interfaces, so a single Entity can be passed
 *    where any of them is expected.
 *  - POLYMORPHISM: attack() and move() dispatch through strategy objects;
 *    swapping the strategy changes behaviour at runtime without changing
 *    the class hierarchy.
 */
public abstract class Entity
        implements Movable, Damageable, Positionable, Identifiable, Attacker, Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final int    maxHealth;
    private int          health;
    private Position     position;

    // Composition: the algorithms are objects, not hard-coded.
    private AttackStrategy   attackStrategy;
    private MovementStrategy movementStrategy;

    protected Entity(String name, int maxHealth, AttackStrategy attackStrategy) {
        this.name = (name == null || name.isEmpty()) ? "Unknown" : name;
        this.maxHealth = Math.max(1, maxHealth);
        this.health = this.maxHealth;
        this.position = new Position(0, 0);
        this.attackStrategy = attackStrategy; // may be null for non-fighters
        this.movementStrategy = new StandardMovementStrategy();
    }

    // ----- Identifiable -----
    @Override public String getName() { return name; }

    // ----- Damageable -----
    @Override public int getHealth() { return health; }
    public  int getMaxHealth() { return maxHealth; }
    @Override public boolean isAlive() { return health > 0; }

    @Override
    public void takeDamage(int amount) {
        if (amount <= 0 || !isAlive()) return;
        // Hook for subclasses to absorb/modify incoming damage (e.g. Player shield).
        int finalDamage = onIncomingDamage(amount);
        if (finalDamage > 0) {
            health = Math.max(0, health - finalDamage);
        }
    }

    /**
     * Hook that lets subclasses transform raw incoming damage before it
     * touches health. The default just passes the amount through.
     * Replaces the previous "instanceof Player" pattern around shields.
     */
    protected int onIncomingDamage(int rawAmount) { return rawAmount; }

    /** Helper for healing logic, used by Healer/potions through subclasses. */
    protected void increaseHealth(int amount) {
        if (amount <= 0) return;
        health = Math.min(maxHealth, health + amount);
    }

    // ----- Positionable / Movable -----
    @Override public Position getPosition() { return position; }
    public  void setPosition(Position p) { if (p != null) this.position = p; }

    @Override
    public void move(Direction direction) {
        if (direction == null || !isAlive()) return;
        this.position = movementStrategy.move(this.position, direction);
    }

    // ----- Attacker -----
    @Override
    public void attack(Entity target) {
        // Public, lenient version: silently no-ops on bad input. The strict
        // version (attackStrict) is the one the engine should call.
        try { attackStrict(target); }
        catch (EntityDeadException | InvalidTargetException ignored) { /* no-op */ }
    }

    /**
     * Strict attack that throws when the action is illegal, so the engine
     * can decide how to react (log, refuse, end turn).
     */
    public void attackStrict(Entity target)
            throws EntityDeadException, InvalidTargetException {
        if (!isAlive())                  throw new EntityDeadException(name + " is dead");
        if (target == null)              throw new InvalidTargetException("Target is null");
        if (!target.isAlive())           throw new InvalidTargetException("Target is already dead");
        if (attackStrategy == null)      throw new InvalidTargetException(name + " cannot attack");

        int damage = Math.max(0, attackStrategy.calculateDamage(this, target));
        damage = onOutgoingDamage(damage); // hook (e.g. Player strength bonus)
        target.takeDamage(damage);
    }

    /** Hook for subclasses that buff/modify outgoing damage. */
    protected int onOutgoingDamage(int rawAmount) { return rawAmount; }

    // ----- Strategy plumbing -----
    public AttackStrategy getAttackStrategy()   { return attackStrategy; }
    public MovementStrategy getMovementStrategy() { return movementStrategy; }

    public void setAttackStrategy(AttackStrategy s)   { if (s != null) this.attackStrategy   = s; }
    public void setMovementStrategy(MovementStrategy s){ if (s != null) this.movementStrategy = s; }

    @Override
    public String toString() {
        return String.format("%s{%s HP=%d/%d %s}",
                getClass().getSimpleName(), name, health, maxHealth, position);
    }
}
