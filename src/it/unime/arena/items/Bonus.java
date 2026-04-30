package it.unime.arena.items;

import it.unime.arena.entities.Player;
import it.unime.arena.interfaces.Identifiable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Power-up that applies an effect to a Player when collected.
 * Final fields + value-based equals/hashCode make it safe to use as
 * a map key or compare across game saves.
 */
public final class Bonus implements Identifiable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final BonusType type;
    private final int value;

    public Bonus(String name, BonusType type, int value) {
        this.name  = (name  == null) ? type.name() : name;
        this.type  = Objects.requireNonNull(type, "type");
        this.value = Math.max(0, value);
    }

    @Override public String getName() { return name; }
    public BonusType getType()  { return type; }
    public int       getValue() { return value; }

    public void applyTo(Player player) {
        if (player == null) return;
        switch (type) {
            case HEALTH:   player.heal(value);        break;
            case STRENGTH: player.addStrength(value); break;
            case SHIELD:   player.addShield(value);   break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bonus)) return false;
        Bonus b = (Bonus) o;
        return value == b.value && type == b.type && Objects.equals(name, b.name);
    }

    @Override public int hashCode() { return Objects.hash(name, type, value); }

    @Override
    public String toString() { return "Bonus{" + name + ", " + type + "=" + value + "}"; }
}
