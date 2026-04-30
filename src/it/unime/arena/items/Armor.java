package it.unime.arena.items;

import it.unime.arena.entities.Player;

public class Armor extends Item implements Equipable {

    private static final long serialVersionUID = 1L;

    private final int defenseBonus;
    private boolean equipped;

    public Armor(String name, String description, int defenseBonus) {
        super(name, description);
        this.defenseBonus = Math.max(0, defenseBonus);
    }

    public int getDefenseBonus() { return defenseBonus; }

    @Override
    public void equip(Player player) {
        if (player == null || equipped) return;
        player.addShield(defenseBonus);
        equipped = true;
    }

    @Override
    public void unequip(Player player) {
        if (player == null || !equipped) return;
        equipped = false;
    }

    @Override public boolean isEquipped() { return equipped; }
}
