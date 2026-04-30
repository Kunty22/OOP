package it.unime.arena.interfaces;

/** Anything that can receive damage and be killed/destroyed. */
public interface Damageable {
    void takeDamage(int amount);
    int  getHealth();
    boolean isAlive();
}
