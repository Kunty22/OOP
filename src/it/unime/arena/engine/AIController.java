package it.unime.arena.engine;

import it.unime.arena.entities.Creature;
import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.EntityDeadException;
import it.unime.arena.exceptions.InvalidMoveException;
import it.unime.arena.exceptions.InvalidTargetException;

import java.util.List;

/**
 * Drives creature behaviour each enemy turn: move toward the nearest
 * player, attack if adjacent. Uses CombatManager + MovementManager
 * instead of doing the math itself.
 */
public class AIController {

    private final CombatManager  combat;
    private final MovementManager movement;

    public AIController(CombatManager combat, MovementManager movement) {
        this.combat   = combat;
        this.movement = movement;
    }

    public void runEnemyTurn(Arena arena, AttackLog log) {
        if (arena.isGameOver()) return;

        for (Creature c : arena.getCreatures()) {
            if (!c.isAlive()) continue;

            Player target = arena.nearestAlivePlayerTo(c);
            if (target == null) continue;

            if (combat.inRange(c, target)) {
                int before = target.getHealth();
                try {
                    combat.performAttack(c, target);
                    int dealt = before - target.getHealth();
                    log.record(c, target, dealt);
                } catch (EntityDeadException | InvalidTargetException ex) {
                    // creature can't act this turn; skip silently
                }
            } else {
                Direction d = greedyDirection(c.getPosition(), target.getPosition());
                try { movement.moveEntity(c, d); }
                catch (InvalidMoveException ignored) { /* dead between turns */ }
            }
        }
    }

    private Direction greedyDirection(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        }
        return dy > 0 ? Direction.DOWN : Direction.UP;
    }
}
