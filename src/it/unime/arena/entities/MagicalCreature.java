package it.unime.arena.entities;

import it.unime.arena.strategy.MagicAttackStrategy;

/** Creature that uses magic instead of melee. */
public class MagicalCreature extends Creature {
    private static final long serialVersionUID = 1L;

    public MagicalCreature(String name, int health) {
        super(name, health, new MagicAttackStrategy());
    }
}
