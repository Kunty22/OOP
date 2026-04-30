package it.unime.arena.ui;

import it.unime.arena.engine.Arena;
import it.unime.arena.engine.Position;
import it.unime.arena.entities.BossCreature;
import it.unime.arena.entities.Creature;
import it.unime.arena.entities.Dragon;
import it.unime.arena.entities.FlyingCreature;
import it.unime.arena.entities.Healer;
import it.unime.arena.entities.MagicalCreature;
import it.unime.arena.entities.Mage;
import it.unime.arena.entities.Obstacle;
import it.unime.arena.entities.Player;
import it.unime.arena.entities.TrapObstacle;
import it.unime.arena.entities.Warrior;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure rendering + click-to-select. Knows nothing about turn flow,
 * cheats, or game rules — it just draws what Arena tells it.
 *
 * Each entity type gets a distinct color, icon, and name label so the
 * board reads like a tactical map rather than coloured pebbles.
 */
public class GamePanel extends JPanel {

    private final Arena arena;

    private Creature selectedCreature;
    private Player   activePlayer;
    private final List<FloatingText> texts = new ArrayList<>();
    private Creature flashTarget;
    private int flashTicks = 0;

    public GamePanel(Arena arena) {
        this.arena = arena;
        setPreferredSize(new Dimension(620, 620));
        setBackground(new Color(245, 240, 225));

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                selectedCreature = creatureAtPixel(e.getX(), e.getY());
                repaint();
            }
        });

        Timer fxTimer = new Timer(16, e -> {
            texts.removeIf(FloatingText::tick);
            if (flashTicks > 0) flashTicks--;
            repaint();
        });
        fxTimer.start();
    }

    public Creature getSelectedCreature() { return selectedCreature; }

    public void setActivePlayer(Player p) { this.activePlayer = p; repaint(); }

    public void showHit(Creature target, int dmg) {
        if (target == null) return;
        Point p = cellToPixel(target.getPosition().getX(), target.getPosition().getY());
        texts.add(new FloatingText("-" + dmg, p.x + 10, p.y + 10, new Color(180, 30, 30)));
        flashTarget = target;
        flashTicks = 12;
    }

    public void showHeal(Player target, int amount) {
        if (target == null) return;
        Point p = cellToPixel(target.getPosition().getX(), target.getPosition().getY());
        texts.add(new FloatingText("+" + amount, p.x + 10, p.y + 10, new Color(30, 140, 50)));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cols = Position.BOARD_WIDTH;
        int rows = Position.BOARD_HEIGHT;
        int cellW = getWidth()  / cols;
        int cellH = getHeight() / rows;

        // grid
        g.setColor(new Color(220, 210, 190));
        for (int x = 0; x <= cols; x++) g.drawLine(x * cellW, 0, x * cellW, rows * cellH);
        for (int y = 0; y <= rows; y++) g.drawLine(0, y * cellH, cols * cellW, y * cellH);

        for (Obstacle o : arena.getObstacles())  drawObstacle (g, o, cellW, cellH);
        for (Creature c : arena.getCreatures())  drawCreature (g, c, cellW, cellH);
        for (Player   p : arena.getPlayers())    drawPlayer   (g, p, cellW, cellH);

        for (FloatingText t : texts) t.paint(g);
    }

    // ------------------------------------------------------------------

    private void drawPlayer(Graphics2D g, Player p, int cellW, int cellH) {
        Point pt = cellToPixel(p.getPosition().getX(), p.getPosition().getY());
        boolean alive = p.isAlive();
        Color  body   = alive ? colorForPlayer(p) : new Color(60, 60, 60);
        String icon   = iconForPlayer(p);

        drawPiece(g, pt, cellW, cellH, body, icon);
        drawLabel(g, pt, cellW, cellH, p.getName(), p.getHealth(), p.getMaxHealth(), true);

        if (alive && p == activePlayer) {
            g.setColor(new Color(255, 200, 0));
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(3f));
            g.drawOval(pt.x + 3, pt.y + 3, cellW - 6, cellH - 6);
            g.setStroke(old);
        }
    }

    private void drawCreature(Graphics2D g, Creature c, int cellW, int cellH) {
        Point pt = cellToPixel(c.getPosition().getX(), c.getPosition().getY());
        boolean alive = c.isAlive();
        Color body;
        if (!alive) {
            body = new Color(60, 60, 60);
        } else if (flashTarget == c && flashTicks % 4 < 2) {
            body = new Color(255, 230, 80);
        } else {
            body = colorForCreature(c);
        }
        String icon = iconForCreature(c);

        drawPiece(g, pt, cellW, cellH, body, icon);
        drawLabel(g, pt, cellW, cellH, c.getName(), c.getHealth(), c.getMaxHealth(), false);

        if (alive && c == selectedCreature) {
            g.setColor(new Color(0, 0, 0, 200));
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(3f));
            g.drawOval(pt.x + 3, pt.y + 3, cellW - 6, cellH - 6);
            g.setStroke(old);
        }
    }

    private void drawObstacle(Graphics2D g, Obstacle o, int cellW, int cellH) {
        Point pt = cellToPixel(o.getPosition().getX(), o.getPosition().getY());
        boolean isTrap = o instanceof TrapObstacle;
        Color   body   = isTrap ? new Color(140, 80, 30) : new Color(120, 120, 120);
        String  icon   = isTrap ? "▲" : "■";

        g.setColor(body);
        g.fillRoundRect(pt.x + 6, pt.y + 6, cellW - 12, cellH - 12, 8, 8);
        g.setColor(body.darker());
        g.drawRoundRect(pt.x + 6, pt.y + 6, cellW - 12, cellH - 12, 8, 8);

        g.setFont(getFont().deriveFont(Font.BOLD, 18f));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int tx = pt.x + (cellW - fm.stringWidth(icon)) / 2;
        int ty = pt.y + (cellH + fm.getAscent()) / 2 - 4;
        g.drawString(icon, tx, ty);

        g.setFont(getFont().deriveFont(Font.PLAIN, 10f));
        fm = g.getFontMetrics();
        String name = o.getName();
        int nx = pt.x + (cellW - fm.stringWidth(name)) / 2;
        int ny = pt.y + cellH - 4;
        g.setColor(new Color(60, 60, 60));
        g.drawString(name, nx, ny);
    }

    private void drawPiece(Graphics2D g, Point pt, int cellW, int cellH,
                           Color body, String icon) {
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(pt.x + 8, pt.y + 10, cellW - 14, cellH - 14);
        g.setColor(body);
        g.fillOval(pt.x + 6, pt.y + 6, cellW - 14, cellH - 14);
        g.setColor(body.darker());
        g.drawOval(pt.x + 6, pt.y + 6, cellW - 14, cellH - 14);

        g.setFont(getFont().deriveFont(Font.BOLD, 22f));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int tx = pt.x + (cellW - fm.stringWidth(icon)) / 2;
        int ty = pt.y + (cellH + fm.getAscent()) / 2 - 8;
        g.drawString(icon, tx, ty);
    }

    private void drawLabel(Graphics2D g, Point pt, int cellW, int cellH,
                           String name, int hp, int max, boolean isPlayer) {
        // name pill above the cell
        g.setFont(getFont().deriveFont(Font.BOLD, 11f));
        FontMetrics fm = g.getFontMetrics();
        int padX = 4;
        int pillW = fm.stringWidth(name) + padX * 2;
        int nx = pt.x + (cellW - fm.stringWidth(name)) / 2;
        int ny = pt.y + 12;
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRoundRect(nx - padX, ny - fm.getAscent(), pillW, fm.getHeight(), 6, 6);
        g.setColor(isPlayer ? new Color(20, 60, 160) : new Color(140, 30, 30));
        g.drawString(name, nx, ny);

        // HP bar
        int barW = cellW - 14;
        int barX = pt.x + 7;
        int barY = pt.y + cellH - 16;
        int filled = (max <= 0) ? 0
                : Math.max(0, Math.min(barW, (int) Math.round(barW * (hp / (double) max))));
        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(barX, barY, barW, 10, 4, 4);
        g.setColor(hpColor(hp, max));
        g.fillRoundRect(barX + 1, barY + 1, Math.max(0, filled - 2), 8, 4, 4);

        String hpText = hp + "/" + max;
        g.setFont(getFont().deriveFont(Font.BOLD, 9f));
        fm = g.getFontMetrics();
        int hx = barX + (barW - fm.stringWidth(hpText)) / 2;
        int hy = barY + 9;
        g.setColor(Color.WHITE);
        g.drawString(hpText, hx, hy);
    }

    private Color hpColor(int hp, int max) {
        if (max <= 0) return Color.GRAY;
        double r = hp / (double) max;
        if (r > 0.6) return new Color(60, 180, 70);
        if (r > 0.3) return new Color(220, 170, 40);
        return new Color(200, 50, 50);
    }

    // ------------------------------------------------------------------
    //  Type → color / icon
    // ------------------------------------------------------------------

    private static Color colorForPlayer(Player p) {
        if (p instanceof Mage)    return new Color(110,  70, 200);
        if (p instanceof Healer)  return new Color( 90, 170, 110);
        if (p instanceof Warrior) return new Color( 60, 110, 200);
        return new Color(80, 80, 80);
    }

    private static String iconForPlayer(Player p) {
        if (p instanceof Mage)    return "✦";
        if (p instanceof Healer)  return "✚";
        if (p instanceof Warrior) return "⚔";
        return "?";
    }

    private static Color colorForCreature(Creature c) {
        if (c instanceof Dragon)          return new Color(170,  40,  40);
        if (c instanceof BossCreature)    return new Color(120,  20,  60);
        if (c instanceof FlyingCreature)  return new Color(100, 130, 180);
        if (c instanceof MagicalCreature) return new Color(180,  90, 180);
        return new Color(200,  90,  60);
    }

    private static String iconForCreature(Creature c) {
        if (c instanceof Dragon)          return "D";
        if (c instanceof BossCreature)    return "☠";
        if (c instanceof FlyingCreature)  return "W";
        if (c instanceof MagicalCreature) return "✷";
        return "G";
    }

    // ------------------------------------------------------------------

    private Creature creatureAtPixel(int px, int py) {
        int cellW = getWidth()  / Position.BOARD_WIDTH;
        int cellH = getHeight() / Position.BOARD_HEIGHT;
        int cx = Math.floorDiv(px, cellW);
        int cy = Math.floorDiv(py, cellH);
        for (Creature c : arena.getCreatures()) {
            if (c.isAlive()
                    && c.getPosition().getX() == cx
                    && c.getPosition().getY() == cy) return c;
        }
        return null;
    }

    private Point cellToPixel(int x, int y) {
        int cellW = getWidth()  / Position.BOARD_WIDTH;
        int cellH = getHeight() / Position.BOARD_HEIGHT;
        return new Point(x * cellW, y * cellH);
    }

    private static class FloatingText {
        final String text;
        final Color  color;
        int x, y, life = 32;
        FloatingText(String text, int x, int y, Color color) {
            this.text = text; this.x = x; this.y = y; this.color = color;
        }
        boolean tick() { y -= 1; return --life <= 0; }
        void paint(Graphics g) {
            int alpha = Math.max(0, Math.min(255, life * 8));
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
            g.drawString(text, x, y);
        }
    }
}
