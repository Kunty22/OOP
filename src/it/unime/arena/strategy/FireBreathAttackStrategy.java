package it.unime.arena.strategy;

import it.unime.arena.entities.Entity;
import java.util.Random;

/**
 * Heavy attack with a cooldown. While on cooldown it falls back to a
 * weaker "claw" hit. Encapsulates its own cooldown counter so the
 * Entity doesn't need to manage that state.
 */
public class FireBreathAttackStrategy implements AttackStrategy {
    private static final long serialVersionUID = 1L;


    private static final int FIRE_MIN = 60;
    private static final int FIRE_MAX = 80;
    private static final int CLAW_MIN = 15;
    private static final int CLAW_MAX = 25;
    private static final int COOLDOWN_TURNS = 2;

    private int cooldown = 0;
    private final Random rng = new Random();

    @Override
    public int calculateDamage(Entity attacker, Entity target) {
        if (attacker == null || target == null) return 0;
        if (cooldown == 0) {
            cooldown = COOLDOWN_TURNS;
            return FIRE_MIN + rng.nextInt(FIRE_MAX - FIRE_MIN + 1);
        }
        cooldown--;
        return CLAW_MIN + rng.nextInt(CLAW_MAX - CLAW_MIN + 1);
    }

    public int getCooldown() { return cooldown; }
    public boolean isReady() { return cooldown == 0; }

    @Override public String getName() { return "Fire Breath"; }
}
