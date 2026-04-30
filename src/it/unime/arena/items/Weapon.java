package it.unime.arena.items;

import it.unime.arena.entities.Player;

public class Weapon extends Item implements Equipable {

    private static final long serialVersionUID = 1L;

    private final int damageBonus;
    private boolean equipped;

    public Weapon(String name, String description, int damageBonus) {
        super(name, description);
        this.damageBonus = Math.max(0, damageBonus);
    }

    public int getDamageBonus() { return damageBonus; }

    @Override
    public void equip(Player player) {
        if (player == null || equipped) return;
        player.addStrength(damageBonus);
        equipped = true;
    }

    @Override
    public void unequip(Player player) {
        if (player == null || !equipped) return;
        equipped = false;
    }

    @Override public boolean isEquipped() { return equipped; }
}
