package it.unime.arena.interfaces;

/**
 * Anything that has a human-readable name.
 *
 * OOP principles demonstrated:
 *  - Abstraction: separates the IDEA of "having a name" from any class.
 *  - Subtyping: many unrelated types (Entity, Item, Bonus...) can share
 *    this contract without sharing a parent class.
 */
public interface Identifiable {
    String getName();
}
