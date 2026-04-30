package it.unime.arena.exceptions;

/** Thrown when a move is rejected (out of bounds, blocked, dead mover, etc.). */
public class InvalidMoveException extends GameException {
    private static final long serialVersionUID = 1L;
    public InvalidMoveException(String message) { super(message); }
}
