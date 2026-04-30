package it.unime.arena.exceptions;

/** Thrown when a level configuration file is malformed or missing. */
public class ConfigException extends GameException {
    private static final long serialVersionUID = 1L;
    public ConfigException(String message) { super(message); }
    public ConfigException(String message, Throwable cause) { super(message, cause); }
}
