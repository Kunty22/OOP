package it.unime.arena.entities;

import it.unime.arena.strategy.AttackStrategy;
import it.unime.arena.strategy.MeleeAttackStrategy;

/**
 * AI-controlled enemy. The default uses a melee strategy, but any
 * subclass can pass a different strategy through the constructor.
 */
public class Creature extends Entity {

    private static final long serialVersionUID = 1L;

    public Creature(String name, int health) {
        super(name, health, new MeleeAttackStrategy());
    }

    /** For subclasses that want a different attack style. */
    protected Creature(String name, int health, AttackStrategy strategy) {
        super(name, health, strategy);
    }
}
