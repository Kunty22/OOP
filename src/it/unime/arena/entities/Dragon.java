package it.unime.arena.entities;

import it.unime.arena.strategy.FireBreathAttackStrategy;

/**
 * Dragon: 3 levels of inheritance (Dragon -> MagicalCreature -> Creature -> Entity).
 * Behaviour comes entirely from the FireBreathAttackStrategy injected at
 * construction — the class itself adds no attack code.
 */
public class Dragon extends MagicalCreature {

    private static final long serialVersionUID = 1L;

    public Dragon(String name, int health) {
        super(name, health);
        setAttackStrategy(new FireBreathAttackStrategy());
    }

    public boolean isFireBreathReady() {
        // Coercion polymorphism: explicit downcast from AttackStrategy
        // (the static type of the field) to FireBreathAttackStrategy
        // (its actual runtime type). Safe here because the constructor
        // installed exactly that strategy.
        return ((FireBreathAttackStrategy) getAttackStrategy()).isReady();
    }
}
