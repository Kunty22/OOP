package it.unime.arena.engine;

import it.unime.arena.entities.Creature;
import it.unime.arena.entities.Entity;
import it.unime.arena.entities.Obstacle;
import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.GameRuleException;
import it.unime.arena.items.Bonus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the live game state: which entities are on the board, who is alive,
 * who has won. No turn logic lives here — that's the GameController's job.
 *
 * Demonstrates:
 *  - Composition: Arena HAS lists of entities; it doesn't extend anything.
 *  - Information hiding: lists are exposed only as unmodifiable views.
 *  - Serializable: the whole arena can be saved to disk in one call.
 */
public class Arena implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Player>   players;
    private final List<Creature> creatures;
    private final List<Obstacle> obstacles;
    private final List<Bonus>    droppedBonuses;

    public Arena(List<Player> players,
                 List<Creature> creatures,
                 List<Obstacle> obstacles) {
        this.players        = (players   != null) ? new ArrayList<>(players)   : new ArrayList<>();
        this.creatures      = (creatures != null) ? new ArrayList<>(creatures) : new ArrayList<>();
        this.obstacles      = (obstacles != null) ? new ArrayList<>(obstacles) : new ArrayList<>();
        this.droppedBonuses = new ArrayList<>();
    }

    // ----- Read-only views -----
    public List<Player>   getPlayers()        { return Collections.unmodifiableList(players); }
    public List<Creature> getCreatures()      { return Collections.unmodifiableList(creatures); }
    public List<Obstacle> getObstacles()      { return Collections.unmodifiableList(obstacles); }
    public List<Bonus>    getDroppedBonuses() { return Collections.unmodifiableList(droppedBonuses); }

    public void addDroppedBonus(Bonus b) { if (b != null) droppedBonuses.add(b); }

    // ----- Game state queries -----
    public boolean anyPlayerAlive()   { return players.stream().anyMatch(Entity::isAlive); }
    public boolean anyCreatureAlive() { return creatures.stream().anyMatch(Entity::isAlive); }

    /**
     * The boss the players are hunting. Currently the first Dragon found
     * on the board; if there is no Dragon, falls back to the toughest
     * remaining creature so the game still has a target.
     */
    public Creature getBoss() {
        for (Creature c : creatures) {
            if (c instanceof it.unime.arena.entities.Dragon) return c;
        }
        // Fallback: pick the creature with the highest max HP.
        Creature best = null;
        for (Creature c : creatures) {
            if (best == null || c.getMaxHealth() > best.getMaxHealth()) best = c;
        }
        return best;
    }

    public boolean isBossDead() {
        Creature boss = getBoss();
        return boss != null && !boss.isAlive();
    }

    /**
     * Boss Hunt rules:
     *   WIN  = the boss is dead (other creatures don't matter)
     *   LOSE = every player is dead
     *   The game also ends in a draw if both happen on the same turn.
     */
    public boolean isGameOver() { return !anyPlayerAlive() || isBossDead(); }

    public String winner() {
        boolean playersAlive = anyPlayerAlive();
        boolean bossDead     = isBossDead();
        Creature boss        = getBoss();
        String bossName      = (boss != null) ? boss.getName() : "the boss";

        if (playersAlive && bossDead)  return bossName + " has been slain — Victory!";
        if (!playersAlive && bossDead) return "Mutual destruction — " + bossName + " falls but so do the heroes.";
        if (!playersAlive)             return "All heroes have fallen — " + bossName + " reigns.";
        return "No winner yet";
    }

    public void requireRunning() throws GameRuleException {
        if (isGameOver()) throw new GameRuleException("Game is already over");
    }

    public Player firstAlivePlayer() {
        for (Player p : players) if (p.isAlive()) return p;
        return null;
    }

    public Creature firstAliveCreature() {
        for (Creature c : creatures) if (c.isAlive()) return c;
        return null;
    }

    /** Closest alive player to the given entity (Manhattan distance). */
    public Player nearestAlivePlayerTo(Entity e) {
        if (e == null) return null;
        Player best = null;
        int bestDist = Integer.MAX_VALUE;
        for (Player p : players) {
            if (!p.isAlive()) continue;
            int d = e.getPosition().manhattanDistanceTo(p.getPosition());
            if (d < bestDist) { bestDist = d; best = p; }
        }
        return best;
    }

    public String status() {
        StringBuilder sb = new StringBuilder("Players: ");
        for (Player p : players) {
            sb.append(p.getName())
              .append("(HP=").append(p.getHealth())
              .append(", Str+=").append(p.getStrengthBonus())
              .append(", Shield=").append(p.getShield()).append(") ");
        }
        sb.append("| Creatures: ");
        for (Creature c : creatures) {
            sb.append(c.getName()).append("(HP=").append(c.getHealth()).append(") ");
        }
        return sb.toString().trim();
    }

    /** Formatted inventory listing for the on-screen log. */
    public String inventories() {
        StringBuilder sb = new StringBuilder();
        for (Player p : players) {
            sb.append(p.getName()).append(" — ");
            sb.append("weapons: ")
              .append(it.unime.arena.items.Inventory.namesOf(p.getWeapons()))
              .append("; armor: ")
              .append(it.unime.arena.items.Inventory.namesOf(p.getArmors()))
              .append("; potions: ")
              .append(it.unime.arena.items.Inventory.namesOf(p.getPotions()))
              .append('\n');
        }
        return sb.toString().trim();
    }
}
