package it.unime.arena.strategy;

import it.unime.arena.entities.Entity;
import java.util.Random;

/** Distance attack: lower minimum, higher variance than melee. */
public class RangedAttackStrategy implements AttackStrategy {
    private static final long serialVersionUID = 1L;


    private static final int MIN = 20;
    private static final int MAX = 35;

    private final Random rng = new Random();

    @Override
    public int calculateDamage(Entity attacker, Entity target) {
        if (attacker == null || target == null) return 0;
        return MIN + rng.nextInt(MAX - MIN + 1);
    }

    @Override public String getName() { return "Ranged"; }
}
