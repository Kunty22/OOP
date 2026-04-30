package it.unime.arena.items;

import it.unime.arena.entities.Player;

/** Items that can be equipped/unequipped. */
public interface Equipable {
    void equip(Player player);
    void unequip(Player player);
    boolean isEquipped();
}
