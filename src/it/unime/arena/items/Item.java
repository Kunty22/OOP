package it.unime.arena.items;

import it.unime.arena.interfaces.Identifiable;

import java.io.Serializable;

/**
 * Abstract base for any pickup. Concrete items (weapons, armor, potions)
 * extend this and implement Equipable or Consumable.
 *
 * Demonstrates abstraction (no Item is ever directly instantiated) and
 * encapsulation (name/description are immutable after construction).
 */
public abstract class Item implements Identifiable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;

    protected Item(String name, String description) {
        this.name = (name == null || name.isEmpty()) ? "Unknown Item" : name;
        this.description = (description == null) ? "" : description;
    }

    @Override public String getName() { return name; }
    public String getDescription() { return description; }

    @Override public String toString() { return name + ": " + description; }
}
