package it.unime.arena.exceptions;

/** Thrown when an attack target is null, out of range, or otherwise illegal. */
public class InvalidTargetException extends GameException {
    private static final long serialVersionUID = 1L;
    public InvalidTargetException(String message) { super(message); }
}
