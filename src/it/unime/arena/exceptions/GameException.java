package it.unime.arena.exceptions;

/**
 * Root of every game-specific checked exception.
 *
 * OOP principles demonstrated:
 *  - Inheritance + abstraction: a single root means callers can write
 *    "catch (GameException e)" once instead of catching every subtype.
 *  - Exception handling: the project funnels all rule violations through
 *    its own type hierarchy rather than throwing raw RuntimeExceptions.
 */
public abstract class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    protected GameException(String message) { super(message); }
    protected GameException(String message, Throwable cause) { super(message, cause); }
}
