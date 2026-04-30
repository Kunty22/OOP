package it.unime.arena.entities;

import it.unime.arena.interfaces.ManaUser;
import it.unime.arena.strategy.MagicAttackStrategy;

/**
 * Spellcaster. Implements ManaUser so any code that cares about mana
 * (e.g. ManaPotion) does not need to know the concrete class.
 */
public class Mage extends Player implements ManaUser {

    private static final long serialVersionUID = 1L;

    private static final int SPELL_COST   = 10;
    private static final int FIZZLE_PENALTY = 8; // dmg lost when out of mana

    private int mana;
    private final int maxMana;

    public Mage(String name, int health, int mana) {
        super(name, health, new MagicAttackStrategy());
        this.maxMana = Math.max(0, mana);
        this.mana = this.maxMana;
    }

    // ----- ManaUser -----
    @Override public int  getMana()      { return mana; }
    @Override public boolean canCastSpell() { return mana >= SPELL_COST; }

    @Override
    public void restoreMana(int amount) {
        if (amount <= 0) return;
        mana = Math.min(maxMana, mana + amount);
    }

    public int getMaxMana() { return maxMana; }

    /**
     * Mage modifies outgoing damage based on whether mana is available.
     * Spell hits cost mana but are at full strength; otherwise the
     * damage is reduced (a "fizzled" spell).
     */
    @Override
    protected int onOutgoingDamage(int rawAmount) {
        int withStrength = super.onOutgoingDamage(rawAmount);
        if (canCastSpell()) {
            mana -= SPELL_COST;
            return withStrength;
        }
        return Math.max(0, withStrength - FIZZLE_PENALTY);
    }
}
