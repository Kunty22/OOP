package it.unime.arena.strategy;

import it.unime.arena.engine.DamageMath;
import it.unime.arena.entities.Entity;
import java.util.Random;

/** Magic attack: 20-35 base damage with a 20% chance of a 1.5x crit. */
public class MagicAttackStrategy implements AttackStrategy {
    private static final long serialVersionUID = 1L;


    private static final int    MIN = 30;
    private static final int    MAX = 50;
    private static final int    CRIT_CHANCE_PERCENT = 25;
    private static final double CRIT_MULTIPLIER     = 1.5;

    private final Random rng = new Random();

    @Override
    public int calculateDamage(Entity attacker, Entity target) {
        if (attacker == null || target == null) return 0;
        int damage = MIN + rng.nextInt(MAX - MIN + 1);
        if (rng.nextInt(100) < CRIT_CHANCE_PERCENT) {
            // COERCION POLYMORPHISM: int 'damage' is widened to double
            // for the multiplication, then explicitly narrowed back to int.
            damage = DamageMath.scale(damage, CRIT_MULTIPLIER);
        }
        return damage;
    }

    @Override public String getName() { return "Magic"; }
}
