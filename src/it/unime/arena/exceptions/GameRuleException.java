package it.unime.arena.exceptions;

/** Thrown when a high-level game rule is violated (e.g. acting after game over). */
public class GameRuleException extends GameException {
    private static final long serialVersionUID = 1L;
    public GameRuleException(String message) { super(message); }
}
