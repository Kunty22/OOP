package it.unime.arena.exceptions;

/** Thrown when an action lacks the resources it needs (e.g. mana). */
public class InsufficientResourceException extends GameException {
    private static final long serialVersionUID = 1L;
    public InsufficientResourceException(String message) { super(message); }
}
