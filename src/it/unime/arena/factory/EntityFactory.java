package it.unime.arena.factory;

import it.unime.arena.entities.BossCreature;
import it.unime.arena.entities.Creature;
import it.unime.arena.entities.DestructibleObstacle;
import it.unime.arena.entities.Dragon;
import it.unime.arena.entities.FlyingCreature;
import it.unime.arena.entities.Healer;
import it.unime.arena.entities.MagicalCreature;
import it.unime.arena.entities.Mage;
import it.unime.arena.entities.Obstacle;
import it.unime.arena.entities.Player;
import it.unime.arena.entities.TrapObstacle;
import it.unime.arena.entities.Warrior;
import it.unime.arena.exceptions.ConfigException;
import it.unime.arena.items.Armor;
import it.unime.arena.items.HealthPotion;
import it.unime.arena.items.Weapon;

/**
 * Factory pattern: encapsulates the "which class to instantiate" decision
 * so callers (notably the LevelLoader) can stay generic.
 *
 * Demonstrates:
 *  - Modularity: adding a new creature type is a one-line change here.
 *  - Single Choice principle: every "what type is this string?" decision
 *    lives in one place.
 *  - Ad-hoc polymorphism: createPlayer is overloaded with two arities.
 */
public final class EntityFactory {

    private EntityFactory() {} // pure static

    /**
     * Overloaded version: caller doesn't have to supply 'extra' (mana).
     * Demonstrates ad-hoc polymorphism — the compiler chooses the
     * matching signature at compile time.
     */
    public static Player createPlayer(String type, String name, int health)
            throws ConfigException {
        return createPlayer(type, name, health, /* extra = */ 50);
    }

    public static Player createPlayer(String type, String name, int health, int extra)
            throws ConfigException {
        Player p;
        switch (type.trim().toUpperCase()) {
            case "WARRIOR": p = new Warrior(name, health); break;
            case "MAGE":    p = new Mage   (name, health, Math.max(0, extra)); break;
            case "HEALER":  p = new Healer (name, health); break;
            default: throw new ConfigException("Unknown player type: " + type);
        }
        equipStarterGear(p, type);
        return p;
    }

    /**
     * Adds typed starter items to the player's typed inventories.
     * This is where the parametric Inventory&lt;T&gt; class is actually used:
     * the compiler enforces that only Weapon goes in getWeapons(),
     * only Armor in getArmors(), etc.
     */
    private static void equipStarterGear(Player p, String type) {
        Armor leather = new Armor("Leather Armor", "Light protection.", 10);
        leather.equip(p);                         // applies +shield
        p.getArmors().add(leather);               // Inventory<Armor>

        HealthPotion hp = new HealthPotion("Minor Healing Potion", "Restores 25 HP.", 25);
        p.getPotions().add(hp);                   // Inventory<HealthPotion>

        switch (type.trim().toUpperCase()) {
            case "WARRIOR": {
                Weapon sword = new Weapon("Iron Sword", "+5 strength.", 5);
                sword.equip(p);
                p.getWeapons().add(sword);        // Inventory<Weapon>
                break;
            }
            case "MAGE": {
                Weapon staff = new Weapon("Apprentice Staff", "+3 strength.", 3);
                staff.equip(p);
                p.getWeapons().add(staff);
                break;
            }
            case "HEALER": {
                Weapon mace = new Weapon("Blessed Mace", "+2 strength.", 2);
                mace.equip(p);
                p.getWeapons().add(mace);
                break;
            }
            default: /* no extras */
        }
    }

    public static Creature createCreature(String type, String name, int health)
            throws ConfigException {
        switch (type.trim().toUpperCase()) {
            case "GENERIC": return new Creature(name, health);
            case "MAGICAL": return new MagicalCreature(name, health);
            case "BOSS":    return new BossCreature(name, health);
            case "FLYING":  return new FlyingCreature(name, health);
            case "DRAGON":  return new Dragon(name, health);
            default: throw new ConfigException("Unknown creature type: " + type);
        }
    }

    public static Obstacle createObstacle(String type, String name, int value)
            throws ConfigException {
        switch (type.trim().toUpperCase()) {
            case "PLAIN":        return new Obstacle(name, value);
            case "DESTRUCTIBLE": return new DestructibleObstacle(name, value);
            case "TRAP":         return new TrapObstacle(name, value);
            default: throw new ConfigException("Unknown obstacle type: " + type);
        }
    }
}
