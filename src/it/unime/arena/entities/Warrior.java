package it.unime.arena.entities;

import it.unime.arena.strategy.MeleeAttackStrategy;

/** Heavy melee fighter. High HP, hits hard. */
public class Warrior extends Player {
    private static final long serialVersionUID = 1L;

    public Warrior(String name, int health) {
        super(name, health, new MeleeAttackStrategy());
    }
}
