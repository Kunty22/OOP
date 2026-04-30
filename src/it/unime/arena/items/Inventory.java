package it.unime.arena.items;

import it.unime.arena.interfaces.Identifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Generic, type-safe container for items.
 *
 * ============================================================
 *   PARAMETRIC POLYMORPHISM (a.k.a. generics)
 * ============================================================
 * The same algorithm — store, find by name, list, remove — works for
 * EVERY item subtype. The type variable T is replaced at compile time:
 *
 *     Inventory&lt;Weapon&gt;       weapons  = new Inventory&lt;&gt;();
 *     Inventory&lt;Armor&gt;        armory   = new Inventory&lt;&gt;();
 *     Inventory&lt;HealthPotion&gt; potions  = new Inventory&lt;&gt;();
 *
 * Each instantiation produces a container with all the same methods but
 * specialised to a different item type, so the compiler rejects e.g.
 *
 *     weapons.add(new Armor(...));   // compile error
 *
 * The bound "T extends Item" lets us still call Item methods (getName)
 * inside the generic algorithm, while keeping concrete-type safety for
 * the caller. This is exactly the parametric-polymorphism example from
 * the lecture (a single algorithm given many types).
 *
 * Note the contrast with subtype polymorphism elsewhere in the project:
 *   - Subtype  : ONE method handles many runtime types via dispatch.
 *   - Parametric: ONE algorithm gets specialised to many compile-time types.
 */
public class Inventory<T extends Item> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> items = new ArrayList<>();

    /** Add an item; null is silently ignored. */
    public void add(T item) {
        if (item != null) items.add(item);
    }

    /** Find the first item whose name matches. Works for any T. */
    public Optional<T> findByName(String name) {
        if (name == null) return Optional.empty();
        for (T it : items) {
            if (name.equalsIgnoreCase(it.getName())) return Optional.of(it);
        }
        return Optional.empty();
    }

    public boolean remove(T item) { return items.remove(item); }

    public int size()        { return items.size(); }
    public boolean isEmpty() { return items.isEmpty(); }

    /** Read-only view, parameterised on T. */
    public List<T> asList() { return Collections.unmodifiableList(items); }

    @Override
    public java.util.Iterator<T> iterator() { return items.iterator(); }

    /**
     * Generic METHOD (in addition to the generic class). Demonstrates
     * that a single class can host independently-parameterised methods.
     * Works for any U that is Identifiable, even if U has nothing to do
     * with this Inventory's T.
     */
    public static <U extends Identifiable> String namesOf(Iterable<U> things) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (U u : things) {
            if (!first) sb.append(", ");
            sb.append(u.getName());
            first = false;
        }
        return sb.toString();
    }
}
