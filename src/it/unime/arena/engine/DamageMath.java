package it.unime.arena.engine;

/**
 * Small numeric helper for damage calculations.
 *
 * ============================================================
 *   COERCION POLYMORPHISM (type promotion / casting)
 * ============================================================
 * The OOP-9 notes define coercion as "when a value of a type is seen
 * (and sometimes transformed) as the value of another type". Java
 * performs this automatically (widening: int -> double) and also
 * supports explicit casts (narrowing: double -> int).
 *
 * Every method below mixes int and double on purpose:
 *
 *   - {@link #scale(int, double)} and {@link #percentOf(int, int)}
 *     show implicit widening: an int operand is silently coerced to
 *     double when it meets a double, so "rawDamage * multiplier"
 *     evaluates in double-precision arithmetic.
 *
 *   - The final {@code (int) ...} performs an explicit narrowing cast
 *     back to int — the sort the lecture calls "casting".
 *
 * This file is the canonical coercion example for the report.
 */
public final class DamageMath {

    private DamageMath() {}

    /**
     * Multiply an int damage value by a floating-point multiplier.
     * Implicit widening (int -&gt; double) on the left operand of {@code *},
     * then explicit narrowing (double -&gt; int) on the way out.
     */
    public static int scale(int rawDamage, double multiplier) {
        // 'rawDamage' is widened to double automatically.
        double scaled = rawDamage * multiplier;
        // Explicit narrowing cast (truncates toward zero).
        return (int) scaled;
    }

    /** Returns 'percent' percent of value, with int/double coercion in between. */
    public static int percentOf(int value, int percent) {
        // Mixed-type expression: 'percent' is widened to double for the divide.
        double fraction = percent / 100.0;
        return scale(value, fraction);
    }

    /** Clamp helper — takes ints, returns int, no coercion needed here. */
    public static int clamp(int value, int lo, int hi) {
        return Math.max(lo, Math.min(hi, value));
    }
}
