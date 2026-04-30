package it.unime.arena.entities;

import it.unime.arena.interfaces.Healable;
import it.unime.arena.strategy.RangedAttackStrategy;

import java.util.List;
import java.util.Random;

/**
 * Support class. Weak attacker, but can restore HP to allies.
 *
 * ============================================================
 *   AD-HOC POLYMORPHISM (method overloading)
 * ============================================================
 * The methods named {@code heal} below all share a name but differ in
 * parameter lists. The compiler picks one at compile time based on the
 * static argument types — this is overloading, the textbook example of
 * "ad-hoc" / static polymorphism (per OOP-9 and OOP-10 notes).
 *
 *   heal(Healable)               -- random amount, single target
 *   heal(Healable, int)          -- exact amount, single target
 *   heal(List&lt;? extends Healable&gt;) -- random amount, every target
 *
 * Note this is fundamentally different from the inclusion polymorphism
 * we use elsewhere: there, ONE method name dispatches to different
 * implementations at RUNTIME. Here, DIFFERENT method bodies are picked
 * at COMPILE time based on argument types.
 *
 * Also demonstrates:
 *  - Subtype polymorphism: the parameter type {@link Healable} accepts
 *    Player, Warrior, Mage, Healer, ... interchangeably (LSP).
 */
public class Healer extends Player {

    private static final long serialVersionUID = 1L;

    private static final int HEAL_MIN = 15;
    private static final int HEAL_MAX = 25;

    private final Random rng = new Random();

    public Healer(String name, int health) {
        super(name, health, new RangedAttackStrategy());
    }

    /** Overload #1: heal a single ally for a random amount. */
    public int heal(Healable ally) {
        if (ally == null || !isAlive()) return 0;
        int amount = HEAL_MIN + rng.nextInt(HEAL_MAX - HEAL_MIN + 1);
        ally.heal(amount);
        return amount;
    }

    /** Overload #2: heal a single ally for an exact amount. */
    public int heal(Healable ally, int amount) {
        if (ally == null || !isAlive() || amount <= 0) return 0;
        ally.heal(amount);
        return amount;
    }

    /** Overload #3: AoE heal — hit every ally in the list. */
    public int heal(List<? extends Healable> allies) {
        if (allies == null || !isAlive()) return 0;
        int totalHealed = 0;
        for (Healable a : allies) {
            totalHealed += heal(a); // dispatches to overload #1
        }
        return totalHealed;
    }

    /** Original method name preserved for backward compatibility. */
    public int healAlly(Healable ally) { return heal(ally); }
}
