package it.unime.arena.entities;

import it.unime.arena.interfaces.Healable;
import it.unime.arena.items.Armor;
import it.unime.arena.items.Bonus;
import it.unime.arena.items.HealthPotion;
import it.unime.arena.items.Inventory;
import it.unime.arena.items.Weapon;
import it.unime.arena.strategy.AttackStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Player-controlled character. Adds shield (damage absorption), strength
 * bonus (extra damage), and collected bonuses on top of Entity. Also
 * carries three TYPE-SAFE inventories — one per item kind — built from
 * the generic {@link it.unime.arena.items.Inventory} class. This is the
 * canonical example of parametric polymorphism in the project.
 *
 * Demonstrates:
 *  - Inheritance: extends Entity, reuses health/position/attack plumbing.
 *  - Polymorphism (inclusion): overrides onIncomingDamage/onOutgoingDamage.
 *  - Polymorphism (parametric): three Inventory&lt;T&gt; instances, each
 *    specialised at compile time to a different item type.
 *  - Encapsulation: bonuses list is exposed only as an unmodifiable view.
 */
public class Player extends Entity implements Healable {

    private static final long serialVersionUID = 1L;

    private int shield = 0;          // absorbs incoming damage first
    private int strengthBonus = 0;   // added to outgoing damage

    private final List<Bonus> bonuses = new ArrayList<>();

    // Parametric polymorphism: one generic class, three specialised types.
    private final Inventory<Weapon>       weapons  = new Inventory<>();
    private final Inventory<Armor>        armors   = new Inventory<>();
    private final Inventory<HealthPotion> potions  = new Inventory<>();

    public Player(String name, int health, AttackStrategy strategy) {
        super(name, health, strategy);
    }

    // ----- Damage hooks (replace the old "instanceof Player" approach) -----
    @Override
    protected int onIncomingDamage(int rawAmount) {
        int remaining = rawAmount;
        if (shield > 0) {
            int absorbed = Math.min(shield, remaining);
            shield   -= absorbed;
            remaining -= absorbed;
        }
        return remaining;
    }

    @Override
    protected int onOutgoingDamage(int rawAmount) {
        return rawAmount + Math.max(0, strengthBonus);
    }

    // ----- Healing -----
    @Override
    public void heal(int amount) { super.increaseHealth(amount); }

    // ----- Buffs -----
    public void addShield(int amount)   { if (amount > 0) shield        += amount; }
    public void addStrength(int amount) { if (amount > 0) strengthBonus += amount; }

    public int getShield()        { return shield; }
    public int getStrengthBonus() { return strengthBonus; }

    // ----- Bonuses -----
    public void collectBonus(Bonus bonus) {
        if (bonus == null) return;
        bonuses.add(bonus);
        bonus.applyTo(this);
    }

    public List<Bonus> getBonuses() { return Collections.unmodifiableList(bonuses); }

    // ----- Typed inventories (parametric polymorphism) -----
    public Inventory<Weapon>       getWeapons() { return weapons; }
    public Inventory<Armor>        getArmors()  { return armors;  }
    public Inventory<HealthPotion> getPotions() { return potions; }
}
