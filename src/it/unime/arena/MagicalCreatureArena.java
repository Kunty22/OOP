package it.unime.arena;

import it.unime.arena.ui.MagicalCreatureArenaGUI;

import javax.swing.SwingUtilities;

/** Application entry point. Hands off to the Swing event-dispatch thread. */
public class MagicalCreatureArena {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> MagicalCreatureArenaGUI.main(args));
    }
}
