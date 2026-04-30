package it.unime.arena.io;

import it.unime.arena.engine.Arena;
import it.unime.arena.exceptions.ConfigException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persists/restores the live Arena to disk using Java object serialization
 * (ObjectOutputStream / ObjectInputStream — see I/O lecture).
 *
 * Demonstrates:
 *  - Serialization: every entity, item, position, and bonus implements
 *    Serializable, so saving the whole game is a single writeObject call.
 *  - Exception handling: I/O failures are wrapped in a typed game exception.
 *  - try-with-resources for guaranteed stream closure.
 */
public final class SaveManager {

    private SaveManager() {}

    public static void save(Arena arena, Path file) throws ConfigException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(arena);
        } catch (IOException e) {
            throw new ConfigException("Cannot save game to: " + file, e);
        }
    }

    public static Arena load(Path file) throws ConfigException {
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(file))) {
            Object obj = ois.readObject();
            if (!(obj instanceof Arena)) {
                throw new ConfigException("Save file does not contain an Arena: " + file);
            }
            return (Arena) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new ConfigException("Cannot load save from: " + file, e);
        }
    }
}
