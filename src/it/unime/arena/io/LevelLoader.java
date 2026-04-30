package it.unime.arena.io;

import it.unime.arena.engine.Arena;
import it.unime.arena.engine.Position;
import it.unime.arena.entities.Creature;
import it.unime.arena.entities.Entity;
import it.unime.arena.entities.Obstacle;
import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.ConfigException;
import it.unime.arena.factory.EntityFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the level layout from a text file using java.io.BufferedReader
 * and java.nio.file.Path/Files (both covered in the I/O chapter).
 *
 * File format (one entity per line, "#" for comments):
 *
 *   PLAYER    type name health extra x y
 *   CREATURE  type name health x y
 *   OBSTACLE  type name toughness x y
 *
 * Examples:
 *   PLAYER    WARRIOR Knight 120 0 1 8
 *   PLAYER    MAGE    Wizard  80 100 2 8
 *   CREATURE  DRAGON  Smaug 200 8 0
 *   OBSTACLE  TRAP    Spikes 15 4 4
 *
 * Demonstrates:
 *  - File I/O using BufferedReader (per OOP-13 notes).
 *  - try-with-resources (Java 7 AutoCloseable) for safe stream closing.
 *  - Exception handling: every parse failure throws a typed ConfigException.
 */
public final class LevelLoader {

    private LevelLoader() {}

    /** Loads from a filesystem path. */
    public static Arena loadFromPath(Path path) throws ConfigException {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parse(br);
        } catch (IOException e) {
            throw new ConfigException("Cannot read level file: " + path, e);
        }
    }

    /** Loads from a classpath resource (used for the bundled default level). */
    public static Arena loadFromResource(String resourceName) throws ConfigException {
        InputStream in = LevelLoader.class.getResourceAsStream(resourceName);
        if (in != null) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return parse(br);
            } catch (IOException e) {
                throw new ConfigException("Cannot read resource: " + resourceName, e);
            }
        }
        // Fallback: try the same name on the filesystem so the project also
        // works when run without packaging the resource into the classpath.
        // Strip any leading slash so it's a relative path the user can place
        // next to the working directory.
        String fileName = resourceName.startsWith("/")
                ? resourceName.substring(resourceName.lastIndexOf('/') + 1)
                : resourceName;
        Path p = java.nio.file.Paths.get(fileName);
        if (Files.exists(p)) return loadFromPath(p);

        // One more fallback: src/.../config/level1.txt for IDE use
        Path src = java.nio.file.Paths.get("src", "it", "unime", "arena", "config", fileName);
        if (Files.exists(src)) return loadFromPath(src);

        throw new ConfigException("Level file not found on classpath or filesystem: "
                + resourceName);
    }

    // --------------------------------------------------------------------

    private static Arena parse(BufferedReader br) throws IOException, ConfigException {
        List<Player>   players   = new ArrayList<>();
        List<Creature> creatures = new ArrayList<>();
        List<Obstacle> obstacles = new ArrayList<>();

        String line;
        int lineNo = 0;
        while ((line = br.readLine()) != null) {
            lineNo++;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            parseLine(trimmed, lineNo, players, creatures, obstacles);
        }

        if (players.isEmpty())   throw new ConfigException("Level has no players");
        if (creatures.isEmpty()) throw new ConfigException("Level has no creatures");

        return new Arena(players, creatures, obstacles);
    }

    private static void parseLine(String line, int lineNo,
                                  List<Player> players,
                                  List<Creature> creatures,
                                  List<Obstacle> obstacles) throws ConfigException {
        String[] tokens = line.split("\\s+");
        try {
            switch (tokens[0].toUpperCase()) {
                case "PLAYER":   players  .add(parsePlayer  (tokens)); return;
                case "CREATURE": creatures.add(parseCreature(tokens)); return;
                case "OBSTACLE": obstacles.add(parseObstacle(tokens)); return;
                default: throw new ConfigException(
                        "Line " + lineNo + ": unknown record type '" + tokens[0] + "'");
            }
        } catch (NumberFormatException nfe) {
            throw new ConfigException("Line " + lineNo + ": invalid number — " + line, nfe);
        } catch (ArrayIndexOutOfBoundsException oob) {
            throw new ConfigException("Line " + lineNo + ": missing fields — " + line, oob);
        }
    }

    // ---- record parsers ----

    private static Player parsePlayer(String[] t) throws ConfigException {
        // PLAYER type name health extra x y
        String type   = t[1];
        String name   = t[2];
        int    health = Integer.parseInt(t[3]);
        int    extra  = Integer.parseInt(t[4]);
        int    x      = Integer.parseInt(t[5]);
        int    y      = Integer.parseInt(t[6]);
        Player p = EntityFactory.createPlayer(type, name, health, extra);
        place(p, x, y);
        return p;
    }

    private static Creature parseCreature(String[] t) throws ConfigException {
        // CREATURE type name health x y
        String type   = t[1];
        String name   = t[2];
        int    health = Integer.parseInt(t[3]);
        int    x      = Integer.parseInt(t[4]);
        int    y      = Integer.parseInt(t[5]);
        Creature c = EntityFactory.createCreature(type, name, health);
        place(c, x, y);
        return c;
    }

    private static Obstacle parseObstacle(String[] t) throws ConfigException {
        // OBSTACLE type name value x y
        String type  = t[1];
        String name  = t[2];
        int    value = Integer.parseInt(t[3]);
        int    x     = Integer.parseInt(t[4]);
        int    y     = Integer.parseInt(t[5]);
        Obstacle o = EntityFactory.createObstacle(type, name, value);
        place(o, x, y);
        return o;
    }

    private static void place(Entity e, int x, int y) {
        e.setPosition(new Position(x, y));
    }
}
