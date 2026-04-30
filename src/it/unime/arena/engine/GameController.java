package it.unime.arena.engine;

import it.unime.arena.entities.Creature;
import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.EntityDeadException;
import it.unime.arena.exceptions.GameRuleException;
import it.unime.arena.exceptions.InvalidMoveException;
import it.unime.arena.exceptions.InvalidTargetException;
import it.unime.arena.factory.BonusFactory;
import it.unime.arena.items.Bonus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Orchestrates the turn flow. Built so the GUI can stay dumb: every
 * user action calls one method here and gets a clear outcome.
 *
 * Demonstrates:
 *  - Single Responsibility: only knows about turn flow.
 *  - Composition: combines Arena + CombatManager + MovementManager + AIController.
 *  - Encapsulation: per-player "has acted" tracking is private here, not
 *    spread across the GUI as before.
 */
public class GameController {

    private static final int BONUS_DROP_CHANCE_PERCENT = 30;

    private final Arena arena;
    private final CombatManager  combat;
    private final MovementManager movement;
    private final AIController   ai;

    private final Set<Player> actedThisTurn = new HashSet<>();
    private Player activePlayer;

    private final Random rng = new Random();

    public GameController(Arena arena, int attackRange) {
        this.arena    = arena;
        this.combat   = new CombatManager(attackRange);
        this.movement = new MovementManager();
        this.ai       = new AIController(combat, movement);
        this.activePlayer = arena.firstAlivePlayer();
    }

    // ----- Accessors -----
    public Arena         getArena()         { return arena; }
    public CombatManager getCombatManager() { return combat; }
    public Player        getActivePlayer()  { return activePlayer; }
    public boolean       hasActed(Player p) { return p != null && actedThisTurn.contains(p); }

    // ----- Player actions -----
    public int playerAttack(Creature target)
            throws GameRuleException, EntityDeadException, InvalidTargetException {
        arena.requireRunning();
        if (activePlayer == null) throw new GameRuleException("No active player");
        if (hasActed(activePlayer)) {
            throw new GameRuleException(activePlayer.getName() + " has already acted");
        }
        int before = target == null ? 0 : target.getHealth();
        combat.performAttack(activePlayer, target);
        int dealt = before - target.getHealth();
        actedThisTurn.add(activePlayer);
        return dealt;
    }

    public void playerMove(Direction d) throws GameRuleException, InvalidMoveException {
        arena.requireRunning();
        if (activePlayer == null) throw new GameRuleException("No active player");
        if (hasActed(activePlayer)) {
            throw new GameRuleException(activePlayer.getName() + " has already acted");
        }
        movement.moveEntity(activePlayer, d);
        actedThisTurn.add(activePlayer);
    }

    /** Lets the GUI override the "one action per turn" rule (e.g. cheats). */
    public void freeMove(Direction d, int times) throws InvalidMoveException {
        if (activePlayer == null) return;
        for (int i = 0; i < Math.max(1, times); i++) {
            movement.moveEntity(activePlayer, d);
        }
    }

    // ----- Turn flow -----
    /** True once every alive player has used their action. */
    public boolean allPlayersActed() {
        for (Player p : arena.getPlayers()) {
            if (p.isAlive() && !actedThisTurn.contains(p)) return false;
        }
        return true;
    }

    public Map<Player, Integer> runEnemyTurn(AttackLog log) {
        Map<Player, Integer> healthBefore = new HashMap<>();
        for (Player p : arena.getPlayers()) healthBefore.put(p, p.getHealth());

        ai.runEnemyTurn(arena, log);

        // Random bonus drop after enemy turn
        if (!arena.isGameOver() && rng.nextInt(100) < BONUS_DROP_CHANCE_PERCENT) {
            Player lucky = arena.firstAlivePlayer();
            if (lucky != null) {
                Bonus b = BonusFactory.createRandomBonus();
                lucky.collectBonus(b);
                arena.addDroppedBonus(b);
            }
        }

        actedThisTurn.clear();
        ensureActivePlayerAlive();
        return healthBefore;
    }

    public void switchPlayer() {
        java.util.List<Player> alive = new java.util.ArrayList<>();
        for (Player p : arena.getPlayers()) if (p.isAlive()) alive.add(p);
        if (alive.size() <= 1) {
            activePlayer = alive.isEmpty() ? null : alive.get(0);
            return;
        }
        int idx = alive.indexOf(activePlayer);
        activePlayer = alive.get((idx + 1) % alive.size());
    }

    private void ensureActivePlayerAlive() {
        if (activePlayer != null && activePlayer.isAlive()) return;
        activePlayer = arena.firstAlivePlayer();
    }
}
