package it.unime.arena.factory;

import it.unime.arena.items.Bonus;
import it.unime.arena.items.BonusType;

import java.util.Random;

/**
 * Factory for {@link Bonus} drops.
 *
 * ============================================================
 *   AD-HOC POLYMORPHISM (OVERLOADING)
 * ============================================================
 * The {@code createBonus} symbol below has THREE different signatures.
 * Each one performs a different algorithm; the compiler picks which
 * one to call from the static types of the arguments — exactly the
 * "static binding (compile time)" definition from the OOP-9 notes.
 *
 *   createBonus(BonusType)              // default name + default value
 *   createBonus(BonusType, int)         // default name + custom value
 *   createBonus(String, BonusType, int) // fully specified
 */
public final class BonusFactory {

    private static final int DEFAULT_VALUE = 10;
    private static final Random rng = new Random();

    private BonusFactory() {}

    // ---- Overloaded createBonus (ad-hoc polymorphism) ----

    public static Bonus createBonus(BonusType type) {
        return createBonus(defaultName(type), type, DEFAULT_VALUE);
    }

    public static Bonus createBonus(BonusType type, int value) {
        return createBonus(defaultName(type), type, value);
    }

    public static Bonus createBonus(String name, BonusType type, int value) {
        return new Bonus(name, type, value);
    }

    // ---- Convenience helpers (kept for readability at call sites) ----

    public static Bonus createHealth  (int value) { return createBonus("Health Potion", BonusType.HEALTH,   value); }
    public static Bonus createStrength(int value) { return createBonus("Power Crystal", BonusType.STRENGTH, value); }
    public static Bonus createShield  (int value) { return createBonus("Shield Orb",    BonusType.SHIELD,   value); }

    public static Bonus createRandomBonus() {
        switch (rng.nextInt(3)) {
            case 0:  return createHealth  (10 + rng.nextInt(11));
            case 1:  return createStrength( 5 + rng.nextInt( 6));
            default: return createShield  ( 8 + rng.nextInt( 8));
        }
    }

    private static String defaultName(BonusType type) {
        switch (type) {
            case HEALTH:   return "Health Potion";
            case STRENGTH: return "Power Crystal";
            case SHIELD:   return "Shield Orb";
            default:       return "Bonus";
        }
    }
}
