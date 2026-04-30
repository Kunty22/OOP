package it.unime.arena.items;

import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.InsufficientResourceException;
import it.unime.arena.interfaces.ManaUser;

/**
 * Restores mana to any ManaUser. The old version used "instanceof Mage"
 * and a cast — this version programs to an interface, so any future
 * mana-using class works without modification (Open-Closed).
 */
public class ManaPotion extends Item implements Consumable {

    private static final long serialVersionUID = 1L;

    private final int manaAmount;
    private boolean consumed;

    public ManaPotion(String name, String description, int manaAmount) {
        super(name, description);
        this.manaAmount = Math.max(0, manaAmount);
    }

    public int getManaAmount() { return manaAmount; }

    @Override
    public void consume(Player player) throws InsufficientResourceException {
        if (player == null || consumed) return;
        if (!(player instanceof ManaUser)) {
            throw new InsufficientResourceException(
                player.getName() + " cannot use mana");
        }
        // Coercion polymorphism: 'player' is statically typed as Player but
        // is cast (downcast) to its ManaUser facet so we can call
        // restoreMana. Java verifies the cast at runtime.
        ((ManaUser) player).restoreMana(manaAmount);
        consumed = true;
    }

    @Override public boolean isConsumed() { return consumed; }
}
