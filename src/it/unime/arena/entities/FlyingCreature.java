package it.unime.arena.entities;

import it.unime.arena.strategy.FastMovementStrategy;
import it.unime.arena.strategy.RangedAttackStrategy;

/**
 * Fast unit: 2 tiles per move thanks to its movement strategy.
 * Demonstrates how a different strategy changes behaviour without
 * touching Entity.move().
 */
public class FlyingCreature extends Creature {

    private static final long serialVersionUID = 1L;

    public FlyingCreature(String name, int health) {
        super(name, health, new RangedAttackStrategy());
        setMovementStrategy(new FastMovementStrategy());
    }
}
