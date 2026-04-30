package it.unime.arena.exceptions;

/** Thrown when a dead entity tries to act, or is the target of an action. */
public class EntityDeadException extends GameException {
    private static final long serialVersionUID = 1L;
    public EntityDeadException(String message) { super(message); }
}
