package com.Wordie.view;

import com.Wordie.model.TileState;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class KeyboardPanel extends JPanel {

    private static final String[] KEY_ROWS = {
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    };
    private static final String ROW2_PREFIX = "ENTER";
    private static final String ROW2_SUFFIX = "DEL";

    private static class KeyInfo {
        final String label;
        final char letter;
        final boolean isSpecial;
        Rectangle bounds;

        KeyInfo(String label, char letter, boolean isSpecial) {
            this.label = label;
            this.letter = letter;
            this.isSpecial = isSpecial;
        }
    }

    private final List<KeyInfo> keys;
    private final Map<Character, Color> keyColors;
    private Runnable onEnter = () -> {};
    private Runnable onDelete = () -> {};
    private KeyInputHandler inputHandler = c -> {};

    public KeyboardPanel() {
        this.keys = new ArrayList<>();
        this.keyColors = new HashMap<>();

        setBackground(Colors.BG);

        for (int r = 0; r < KEY_ROWS.length; r++) {
            String row = KEY_ROWS[r];
            if (r == 2) {
                keys.add(new KeyInfo(ROW2_PREFIX, '\0', true));
                for (char c : row.toCharArray()) {
                    keys.add(new KeyInfo(String.valueOf(c), c, false));
                    keyColors.put(c, Colors.KEY_BG);
                }
                keys.add(new KeyInfo(ROW2_SUFFIX, '\0', true));
            } else {
                for (char c : row.toCharArray()) {
                    keys.add(new KeyInfo(String.valueOf(c), c, false));
                    keyColors.put(c, Colors.KEY_BG);
                }
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    public void setOnEnter(Runnable onEnter) { this.onEnter = onEnter; }
    public void setOnDelete(Runnable onDelete) { this.onDelete = onDelete; }
    public void setInputHandler(KeyInputHandler handler) { this.inputHandler = handler; }

    public void setKeyColor(char letter, TileState state) {
        char upper = Character.toUpperCase(letter);
        Color current = keyColors.getOrDefault(upper, Colors.KEY_BG);
        Color newColor = switch (state) {
            case CORRECT -> Colors.GREEN;
            case PRESENT -> (current != Colors.GREEN) ? Colors.YELLOW : Colors.GREEN;
            case ABSENT -> (current != Colors.GREEN && current != Colors.YELLOW) ? Colors.GRAY : current;
            default -> current;
        };
        keyColors.put(upper, newColor);
        repaint();
    }

    public void resetAll() {
        keyColors.clear();
        for (KeyInfo key : keys) {
            if (!key.isSpecial) {
                keyColors.put(key.letter, Colors.KEY_BG);
            }
        }
        repaint();
    }

    private void handleClick(int mx, int my) {
        for (KeyInfo key : keys) {
            if (key.bounds != null && key.bounds.contains(mx, my)) {
                if (key.label.equals(ROW2_PREFIX)) {
                    onEnter.run();
                } else if (key.label.equals(ROW2_SUFFIX)) {
                    onDelete.run();
                } else {
                    inputHandler.onKeyPress(key.letter);
                }
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int padding = Math.max(w / 40, 6);
        int keyGap = Math.max(w / 120, 3);
        int vGap = Math.max(h / 20, 4);

        int availableWidth = w - 2 * padding;

        int normalKeyWidth = (availableWidth - 9 * keyGap) / 10;
        normalKeyWidth = Math.max(normalKeyWidth, 18);
        normalKeyWidth = Math.min(normalKeyWidth, 60);

        int specialKeyWidth = (int) (normalKeyWidth * 1.6);
        int keyHeight = (int) (normalKeyWidth * 1.3);
        keyHeight = Math.max(keyHeight, 28);

        for (int r = 0; r < KEY_ROWS.length; r++) {
            List<KeyInfo> rowKeys = getRowKeys(r);
            int rowWidth = computeRowWidth(rowKeys, normalKeyWidth, specialKeyWidth, keyGap);
            int xOff = padding + (availableWidth - rowWidth) / 2;
            int totalH = KEY_ROWS.length * keyHeight + (KEY_ROWS.length - 1) * vGap;
            int yOff = padding + r * (keyHeight + vGap) + (h - 2 * padding - totalH) / 2;

            for (KeyInfo key : rowKeys) {
                int kw = key.isSpecial ? specialKeyWidth : normalKeyWidth;

                Color bg = key.isSpecial ? Colors.KEY_BG : keyColors.getOrDefault(key.letter, Colors.KEY_BG);
                g2.setColor(bg);
                g2.fillRoundRect(xOff, yOff, kw, keyHeight, 6, 6);

                int fontSize = key.isSpecial ? (int) (normalKeyWidth * 0.22) : (int) (normalKeyWidth * 0.3);
                g2.setFont(new Font("Arial", Font.BOLD, fontSize));
                g2.setColor(Colors.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = xOff + (kw - fm.stringWidth(key.label)) / 2;
                int ty = yOff + (keyHeight + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(key.label, tx, ty);

                key.bounds = new Rectangle(xOff, yOff, kw, keyHeight);
                xOff += kw + keyGap;
            }
        }

        g2.dispose();
    }

    private List<KeyInfo> getRowKeys(int rowIndex) {
        List<KeyInfo> result = new ArrayList<>();
        int start = 0;
        for (int r = 0; r < rowIndex; r++) {
            start += KEY_ROWS[r].length() + (r == 2 ? 2 : 0);
        }
        int cnt = KEY_ROWS[rowIndex].length() + (rowIndex == 2 ? 2 : 0);
        for (int i = start; i < start + cnt; i++) {
            result.add(keys.get(i));
        }
        return result;
    }

    private int computeRowWidth(List<KeyInfo> rowKeys, int normalKeyWidth, int specialKeyWidth, int gap) {
        int total = 0;
        for (KeyInfo k : rowKeys) {
            total += k.isSpecial ? specialKeyWidth : normalKeyWidth;
        }
        total += (rowKeys.size() - 1) * gap;
        return total;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 160);
    }

    @FunctionalInterface
    public interface KeyInputHandler {
        void onKeyPress(char letter);
    }
}
