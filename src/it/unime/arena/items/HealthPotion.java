package it.unime.arena.items;

import it.unime.arena.entities.Player;

public class HealthPotion extends Item implements Consumable {

    private static final long serialVersionUID = 1L;

    private final int healAmount;
    private boolean consumed;

    public HealthPotion(String name, String description, int healAmount) {
        super(name, description);
        this.healAmount = Math.max(0, healAmount);
    }

    public int getHealAmount() { return healAmount; }

    @Override
    public void consume(Player player) {
        if (player == null || consumed) return;
        player.heal(healAmount);
        consumed = true;
    }

    @Override public boolean isConsumed() { return consumed; }
}
