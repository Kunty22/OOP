package it.unime.arena.ui;

import it.unime.arena.cheat.CheatManager;
import it.unime.arena.engine.Arena;
import it.unime.arena.engine.Direction;
import it.unime.arena.engine.GameController;
import it.unime.arena.entities.Creature;
import it.unime.arena.entities.Player;
import it.unime.arena.exceptions.ConfigException;
import it.unime.arena.exceptions.GameException;
import it.unime.arena.io.LevelLoader;
import it.unime.arena.io.SaveManager;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

/**
 * Thin Swing front-end. All game-rule decisions are delegated to the
 * GameController; this class only wires buttons to controller calls
 * and prints results to the on-screen log.
 */
public class MagicalCreatureArenaGUI {

    private static final int ATTACK_RANGE = 4;
    private static final String DEFAULT_LEVEL_RESOURCE = "/it/unime/arena/config/level1.txt";

    private final CheatManager cheats = CheatManager.getInstance();

    private JFrame    frame;
    private JTextArea logArea;
    private JButton   attackBtn, endTurnBtn, switchBtn, restartBtn, saveBtn, loadBtn, potionBtn;
    private JButton   upBtn, downBtn, leftBtn, rightBtn;

    // Boss-Hunt HUD
    private JLabel       bossNameLabel;
    private JProgressBar bossHpBar;
    private JLabel       objectiveLabel;

    private GameController controller;
    private GamePanel      gamePanel;

    public MagicalCreatureArenaGUI() {
        this.controller = newControllerOrFatal();
        initUI();
        it.unime.arena.entities.Creature boss = controller.getArena().getBoss();
        appendLog("=== BOSS HUNT ===");
        appendLog("Objective: defeat " + (boss != null ? boss.getName() : "the boss")
                + " to win. Other creatures only block your path.");
        appendLog(controller.getArena().status());
        appendLog(controller.getArena().inventories());
        refresh();
    }

    // -------- bootstrap --------
    private GameController newControllerOrFatal() {
        try {
            Arena arena = LevelLoader.loadFromResource(DEFAULT_LEVEL_RESOURCE);
            return new GameController(arena, ATTACK_RANGE);
        } catch (ConfigException e) {
            JOptionPane.showMessageDialog(null,
                    "Cannot load default level:\n" + e.getMessage(),
                    "Fatal", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    // -------- UI layout --------
    private void initUI() {
        frame = new JFrame("Magical Creature Arena");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 880);
        frame.setLayout(new BorderLayout(8, 8));

        gamePanel = new GamePanel(controller.getArena());

        JPanel movePanel = new JPanel(new GridLayout(2, 3, 8, 8));
        upBtn    = new JButton("↑");
        downBtn  = new JButton("↓");
        leftBtn  = new JButton("←");
        rightBtn = new JButton("→");
        movePanel.add(new JLabel());      movePanel.add(upBtn);    movePanel.add(new JLabel());
        movePanel.add(leftBtn);            movePanel.add(downBtn);  movePanel.add(rightBtn);
        movePanel.setBorder(BorderFactory.createTitledBorder("Move"));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        attackBtn  = new JButton("Attack [Space]");
        endTurnBtn = new JButton("End Turn [Enter]");
        switchBtn  = new JButton("Switch [Tab]");
        restartBtn = new JButton("Restart [R]");
        saveBtn    = new JButton("Save");
        loadBtn    = new JButton("Load");
        potionBtn  = new JButton("Use Potion [P]");
        endTurnBtn.setEnabled(false);
        actionPanel.add(new JLabel("Target: click red"));
        actionPanel.add(attackBtn);
        actionPanel.add(potionBtn);
        actionPanel.add(switchBtn);
        actionPanel.add(endTurnBtn);
        actionPanel.add(restartBtn);
        actionPanel.add(saveBtn);
        actionPanel.add(loadBtn);

        logArea = new JTextArea(6, 40);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        // wire buttons
        upBtn   .addActionListener(e -> doMove(Direction.UP));
        downBtn .addActionListener(e -> doMove(Direction.DOWN));
        leftBtn .addActionListener(e -> doMove(Direction.LEFT));
        rightBtn.addActionListener(e -> doMove(Direction.RIGHT));
        attackBtn .addActionListener(e -> doAttack());
        endTurnBtn.addActionListener(e -> doEnemyTurn());
        switchBtn .addActionListener(e -> doSwitchPlayer());
        restartBtn.addActionListener(e -> doRestart());
        saveBtn   .addActionListener(e -> doSave());
        loadBtn   .addActionListener(e -> doLoad());
        potionBtn .addActionListener(e -> doUsePotion());

        // key bindings
        JComponent root = frame.getRootPane();
        bindKey(root, KeyStroke.getKeyStroke("UP"),    () -> doMove(Direction.UP));
        bindKey(root, KeyStroke.getKeyStroke("DOWN"),  () -> doMove(Direction.DOWN));
        bindKey(root, KeyStroke.getKeyStroke("LEFT"),  () -> doMove(Direction.LEFT));
        bindKey(root, KeyStroke.getKeyStroke("RIGHT"), () -> doMove(Direction.RIGHT));
        bindKey(root, KeyStroke.getKeyStroke('W'),     () -> doMove(Direction.UP));
        bindKey(root, KeyStroke.getKeyStroke('S'),     () -> doMove(Direction.DOWN));
        bindKey(root, KeyStroke.getKeyStroke('A'),     () -> doMove(Direction.LEFT));
        bindKey(root, KeyStroke.getKeyStroke('D'),     () -> doMove(Direction.RIGHT));
        bindKey(root, KeyStroke.getKeyStroke("SPACE"), this::doAttack);
        bindKey(root, KeyStroke.getKeyStroke("ENTER"), this::doEnemyTurn);
        bindKey(root, KeyStroke.getKeyStroke("TAB"),   this::doSwitchPlayer);
        bindKey(root, KeyStroke.getKeyStroke('R'),     this::doRestart);
        bindKey(root, KeyStroke.getKeyStroke('P'),     this::doUsePotion);

        // global key listener for cheat codes
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_TYPED) {
                String result = cheats.processKeyInput(e.getKeyChar());
                if (result != null) {
                    appendLog("*** CHEAT: " + result + " ***");
                    refresh();
                }
            }
            return false;
        });

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(movePanel,  BorderLayout.WEST);
        bottom.add(actionPanel, BorderLayout.CENTER);
        bottom.add(logScroll,  BorderLayout.SOUTH);

        // ---- Boss-Hunt HUD ----
        objectiveLabel = new JLabel("OBJECTIVE: Slay the Dragon", SwingConstants.CENTER);
        objectiveLabel.setFont(objectiveLabel.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        objectiveLabel.setForeground(new java.awt.Color(140, 30, 30));
        objectiveLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        JPanel bossPanel = new JPanel(new BorderLayout(8, 4));
        bossPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 80, 80)),
                BorderFactory.createEmptyBorder(2, 12, 6, 12)));
        bossNameLabel = new JLabel("");
        bossNameLabel.setFont(bossNameLabel.getFont().deriveFont(java.awt.Font.BOLD, 13f));
        bossHpBar = new JProgressBar(0, 100);
        bossHpBar.setStringPainted(true);
        bossHpBar.setForeground(new java.awt.Color(180, 40, 40));
        bossHpBar.setPreferredSize(new java.awt.Dimension(0, 22));
        bossPanel.add(bossNameLabel, BorderLayout.WEST);
        bossPanel.add(bossHpBar,     BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        legend.add(new JLabel("⚔ Warrior"));
        legend.add(new JLabel("✦ Mage"));
        legend.add(new JLabel("✚ Healer"));
        legend.add(new JLabel("|"));
        legend.add(new JLabel("G Goblin"));
        legend.add(new JLabel("✷ Magical"));
        legend.add(new JLabel("W Wyvern"));
        legend.add(new JLabel("D Dragon (BOSS)"));
        legend.add(new JLabel("|"));
        legend.add(new JLabel("■ Wall"));
        legend.add(new JLabel("▲ Trap"));
        legend.add(new JLabel("|"));
        legend.add(new JLabel("Yellow ring = active player"));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        topPanel.add(objectiveLabel);
        topPanel.add(bossPanel);
        topPanel.add(legend);

        frame.add(topPanel,  BorderLayout.NORTH);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(bottom,    BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void bindKey(JComponent root, KeyStroke ks, Runnable action) {
        Object key = "act-" + ks.toString();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, key);
        root.getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { action.run(); }
        });
    }

    // -------- actions --------
    private void doAttack() {
        if (controller.getArena().isGameOver()) return;
        Creature target = gamePanel.getSelectedCreature();
        if (target == null || !target.isAlive()) {
            appendLog("Select a creature (click a red disc) before attacking.");
            return;
        }
        try {
            int dealt;
            if (cheats.oneHitKill()) {
                int before = target.getHealth();
                target.takeDamage(9999);
                dealt = before - target.getHealth();
            } else {
                dealt = controller.playerAttack(target);
            }
            gamePanel.showHit(target, dealt);
            appendLog(controller.getActivePlayer().getName()
                    + " attacks " + target.getName() + " (" + dealt + " dmg)");
            afterPlayerAction();
        } catch (GameException ex) {
            appendLog("! " + ex.getMessage());
        }
    }

    private void doMove(Direction d) {
        if (controller.getArena().isGameOver()) return;
        try {
            if (cheats.godMode()) {
                controller.freeMove(d, 5);
            } else if (cheats.speedMode()) {
                controller.freeMove(d, 3);
                // speed mode still costs the turn
                appendLog(controller.getActivePlayer().getName() + " moves " + d + " (SPEED!)");
                afterPlayerAction();
                return;
            } else {
                controller.playerMove(d);
            }
            appendLog(controller.getActivePlayer().getName() + " moves " + d
                    + (cheats.godMode() ? " (GODMODE!)" : ""));
            if (!cheats.godMode()) afterPlayerAction(); else refresh();
        } catch (GameException ex) {
            appendLog("! " + ex.getMessage());
        }
    }

    /**
     * Demonstrates two polymorphic features at once:
     *   - Parametric: the active player's typed Inventory&lt;HealthPotion&gt;
     *     is queried (compile-time type safety).
     *   - Inclusion + coercion: ManaPotion's consume() goes through the
     *     ManaUser interface check and a downcast.
     */
    private void doUsePotion() {
        if (controller.getArena().isGameOver()) return;
        Player p = controller.getActivePlayer();
        if (p == null) return;

        // Look in the typed Inventory<HealthPotion>
        for (it.unime.arena.items.HealthPotion hp : p.getPotions()) {
            if (!hp.isConsumed()) {
                int before = p.getHealth();
                hp.consume(p);
                int healed = p.getHealth() - before;
                appendLog(p.getName() + " drinks " + hp.getName()
                        + " (+" + healed + " HP)");
                refresh();
                return;
            }
        }
        appendLog(p.getName() + " has no potions left.");
    }

    private void doSwitchPlayer() {
        if (controller.getArena().isGameOver()) return;
        controller.switchPlayer();
        appendLog("Active player: " + controller.getActivePlayer().getName());
        refresh();
    }

    private void doEnemyTurn() {
        if (controller.getArena().isGameOver()) return;
        appendLog("--- Creatures' turn ---");
        java.util.Map<Player, Integer> beforeHp = controller.runEnemyTurn(
                (attacker, target, dmg) -> {
                    if (target instanceof Player) {
                        appendLog(attacker.getName() + " attacks "
                                + target.getName() + " (" + dmg + " dmg)");
                    }
                });

        // godmode: refund all damage taken this enemy turn
        if (cheats.godMode()) {
            for (Player p : controller.getArena().getPlayers()) {
                int before = beforeHp.getOrDefault(p, p.getHealth());
                int dmg = before - p.getHealth();
                if (dmg > 0) {
                    p.heal(dmg);
                    appendLog("[GODMODE] " + p.getName() + " blocked " + dmg + " damage!");
                }
            }
        }

        for (Player p : controller.getArena().getPlayers()) {
            appendLog(p.isAlive() ? p.getName() + " HP: " + p.getHealth()
                                  : p.getName() + " is DEAD!");
        }
        endTurnBtn.setEnabled(false);
        refresh();
        checkGameOver();
    }

    private void afterPlayerAction() {
        refresh();
        checkGameOver();
        if (controller.allPlayersActed()) {
            doEnemyTurn();
        } else {
            endTurnBtn.setEnabled(true);
        }
    }

    private void doRestart() {
        cheats.reset();
        frame.dispose();
        SwingUtilities.invokeLater(MagicalCreatureArenaGUI::new);
    }

    private void doSave() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        Path file = chooser.getSelectedFile().toPath();
        try {
            SaveManager.save(controller.getArena(), file);
            appendLog("Saved to " + file);
        } catch (ConfigException ex) {
            appendLog("! Save failed: " + ex.getMessage());
        }
    }

    private void doLoad() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        Path file = chooser.getSelectedFile().toPath();
        try {
            Arena loaded = SaveManager.load(file);
            controller = new GameController(loaded, ATTACK_RANGE);
            // re-create game panel with the new arena
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                MagicalCreatureArenaGUI g = new MagicalCreatureArenaGUI(loaded);
                g.appendLog("Loaded from " + file);
            });
        } catch (ConfigException ex) {
            appendLog("! Load failed: " + ex.getMessage());
        }
    }

    /** Constructor used by Load to inject a pre-built arena. */
    private MagicalCreatureArenaGUI(Arena loadedArena) {
        this.controller = new GameController(loadedArena, ATTACK_RANGE);
        initUI();
        appendLog(controller.getArena().status());
    }

    private void checkGameOver() {
        if (controller.getArena().isGameOver()) {
            String msg = controller.getArena().winner();
            boolean victory = controller.getArena().isBossDead()
                    && controller.getArena().anyPlayerAlive();
            String title = victory ? "★ VICTORY ★" : "DEFEAT";
            int    icon  = victory ? JOptionPane.INFORMATION_MESSAGE
                                   : JOptionPane.WARNING_MESSAGE;
            appendLog("=== " + title + " ===  " + msg);
            // big banner update
            objectiveLabel.setText(title + " — " + msg);
            objectiveLabel.setForeground(victory
                    ? new java.awt.Color(20, 110, 40)
                    : new java.awt.Color(140, 30, 30));
            updateBossHud();
            JOptionPane.showMessageDialog(frame, msg, title, icon);
            disableAll();
        }
    }

    private void disableAll() {
        for (JButton b : new JButton[]{upBtn, downBtn, leftBtn, rightBtn,
                                       attackBtn, potionBtn, endTurnBtn, switchBtn}) {
            b.setEnabled(false);
        }
    }

    private void refresh() {
        gamePanel.setActivePlayer(controller.getActivePlayer());
        gamePanel.repaint();
        updateBossHud();
    }

    private void updateBossHud() {
        if (bossHpBar == null) return; // not yet initialized
        it.unime.arena.entities.Creature boss = controller.getArena().getBoss();
        if (boss == null) {
            bossNameLabel.setText("No boss");
            bossHpBar.setValue(0);
            bossHpBar.setString("--");
            return;
        }
        int hp  = Math.max(0, boss.getHealth());
        int max = Math.max(1, boss.getMaxHealth());
        int pct = (int) Math.round(100.0 * hp / max);
        bossNameLabel.setText("BOSS: " + boss.getName());
        bossHpBar.setValue(pct);
        bossHpBar.setString(hp + " / " + max + " HP" + (boss.isAlive() ? "" : "  ☠ SLAIN"));
    }

    private void appendLog(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MagicalCreatureArenaGUI::new);
    }
}
