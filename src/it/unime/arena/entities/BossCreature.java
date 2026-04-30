package it.unime.arena.entities;

import it.unime.arena.strategy.MeleeAttackStrategy;

/**
 * Heavy hitter: doubles whatever its strategy produces.
 * Demonstrates polymorphism via the onOutgoingDamage hook.
 */
public class BossCreature extends Creature {

    private static final long serialVersionUID = 1L;
    private static final int DAMAGE_MULTIPLIER = 2;

    public BossCreature(String name, int health) {
        super(name, health, new MeleeAttackStrategy());
    }

    @Override
    protected int onOutgoingDamage(int rawAmount) {
        return rawAmount * DAMAGE_MULTIPLIER;
    }
}
