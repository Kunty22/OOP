package it.unime.arena.interfaces;

/**
 * A character that consumes and regenerates mana to cast spells.
 *
 * OOP principles demonstrated:
 *  - Subtyping over type-checking: code that uses mana checks for
 *    "instanceof ManaUser" instead of "instanceof Mage", so any future
 *    Sorcerer/Wizard/Witch class plugs in for free.
 *  - Open-Closed Principle: adding a new mana-using class does not
 *    require changing ManaPotion.consume().
 */
public interface ManaUser {
    int  getMana();
    void restoreMana(int amount);
    boolean canCastSpell();
}
