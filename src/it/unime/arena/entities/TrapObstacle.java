package it.unime.arena.entities;

import it.unime.arena.interfaces.Damageable;

/**
 * Damages whoever steps on it, once. After triggering it is inert
 * unless reset.
 */
public class TrapObstacle extends Obstacle {

    private static final long serialVersionUID = 1L;

    private final int trapDamage;
    private boolean triggered;

    public TrapObstacle(String name, int trapDamage) {
        super(name, 1);
        this.trapDamage = Math.max(1, trapDamage);
        this.triggered = false;
    }

    public int  getTrapDamage() { return trapDamage; }
    public boolean isTriggered() { return triggered; }

    public void trigger(Damageable victim) {
        if (triggered || victim == null) return;
        victim.takeDamage(trapDamage);
        triggered = true;
    }

    public void reset() { triggered = false; }
}
