package it.unime.arena.cheat;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Singleton: tracks which cheats are currently active and matches typed
 * input against known cheat codes.
 *
 * Demonstrates:
 *  - Singleton pattern: a private constructor + getInstance() guarantees
 *    a single shared registry across the whole game.
 *  - Encapsulation: the active-cheats set is exposed only as an
 *    unmodifiable view.
 */
public final class CheatManager {

    private static final int BUFFER_LIMIT = 20;

    private static final CheatManager INSTANCE = new CheatManager();

    private final Set<CheatCode> activeCheats = EnumSet.noneOf(CheatCode.class);
    private final StringBuilder  inputBuffer  = new StringBuilder();

    private CheatManager() {} // prevents external construction

    public static CheatManager getInstance() { return INSTANCE; }

    /** Feed the next typed character. Returns a status message if a cheat toggled, else null. */
    public String processKeyInput(char key) {
        inputBuffer.append(Character.toLowerCase(key));
        if (inputBuffer.length() > BUFFER_LIMIT) {
            inputBuffer.delete(0, inputBuffer.length() - BUFFER_LIMIT);
        }
        String typed = inputBuffer.toString();
        for (CheatCode c : CheatCode.values()) {
            if (typed.endsWith(c.getCode())) {
                toggle(c);
                inputBuffer.setLength(0);
                return c.name() + (isActive(c) ? " ACTIVATED" : " DEACTIVATED");
            }
        }
        return null;
    }

    public void toggle(CheatCode c) {
        if (!activeCheats.add(c)) activeCheats.remove(c);
    }

    public boolean isActive(CheatCode c) { return activeCheats.contains(c); }

    public boolean godMode()    { return isActive(CheatCode.GODMODE); }
    public boolean oneHitKill() { return isActive(CheatCode.ONEHITKILL); }
    public boolean speedMode()  { return isActive(CheatCode.SPEEDMODE); }

    public Set<CheatCode> getActiveCheats() {
        return Collections.unmodifiableSet(activeCheats);
    }

    public void reset() {
        activeCheats.clear();
        inputBuffer.setLength(0);
    }
}
