package com.Wordie.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.Wordie.model.TileState;

public class KeyboardPanel extends JPanel {

    private static final String[] KEY_ROWS = {
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    };
    private static final String ROW2_PREFIX = "ENTER";
    private static final String ROW2_SUFFIX = "DEL";

    private static class KeyDescriptor {
        final String label;
        final char letter;
        final boolean isSpecial;
        Rectangle bounds;

        KeyDescriptor(String label, char letter, boolean isSpecial) {
            this.label = label;
            this.letter = letter;
            this.isSpecial = isSpecial;
        }
    }

    private final List<KeyDescriptor> keys;
    private final Map<Character, Color> keyColors;
    private Runnable onEnter = () -> {};
    private Runnable onDelete = () -> {};
    private LetterInputListener inputHandler = c -> {};

    public KeyboardPanel() {
        this.keys = new ArrayList<>();
        this.keyColors = new HashMap<>();

        setBackground(Colors.BG);

        for (int rowIndex = 0; rowIndex < KEY_ROWS.length; rowIndex++) {
            String row = KEY_ROWS[rowIndex];
            if (rowIndex == 2) {
                keys.add(new KeyDescriptor(ROW2_PREFIX, '\0', true));
                for (char letter : row.toCharArray()) {
                    keys.add(new KeyDescriptor(String.valueOf(letter), letter, false));
                    keyColors.put(letter, Colors.KEY_BG);
                }
                keys.add(new KeyDescriptor(ROW2_SUFFIX, '\0', true));
            } else {
                for (char letter : row.toCharArray()) {
                    keys.add(new KeyDescriptor(String.valueOf(letter), letter, false));
                    keyColors.put(letter, Colors.KEY_BG);
                }
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onKeyClick(e.getX(), e.getY());
            }
        });
    }

    public void setOnEnter(Runnable onEnter) { this.onEnter = onEnter; }
    public void setOnDelete(Runnable onDelete) { this.onDelete = onDelete; }
    public void setInputHandler(LetterInputListener handler) { this.inputHandler = handler; }

    public void setKeyColor(char letter, TileState state) {
        char uppercase = Character.toUpperCase(letter);
        Color current = keyColors.getOrDefault(uppercase, Colors.KEY_BG);
        Color newColor = switch (state) {
            case CORRECT -> Colors.GREEN;
            case PRESENT -> (current != Colors.GREEN) ? Colors.YELLOW : Colors.GREEN;
            case ABSENT -> (current != Colors.GREEN && current != Colors.YELLOW) ? Colors.GRAY : current;
            default -> current;
        };
        keyColors.put(uppercase, newColor);
        repaint();
    }

    public void resetAll() {
        keyColors.clear();
        for (KeyDescriptor key : keys) {
            if (!key.isSpecial) {
                keyColors.put(key.letter, Colors.KEY_BG);
            }
        }
        repaint();
    }

    private void onKeyClick(int mouseX, int mouseY) {
        for (KeyDescriptor key : keys) {
            if (key.bounds != null && key.bounds.contains(mouseX, mouseY)) {
                switch (key.label) {
                    case ROW2_PREFIX -> onEnter.run();
                    case ROW2_SUFFIX -> onDelete.run();
                    default -> inputHandler.onKeyPress(key.letter);
                }
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int padding = Math.max(width / 40, 6);
        int keyGap = Math.max(width / 120, 3);
        int vGap = Math.max(height / 20, 4);

        int availableWidth = width - 2 * padding;

        int normalKeyWidth = (availableWidth - 9 * keyGap) / 10;
        normalKeyWidth = Math.max(normalKeyWidth, 18);
        normalKeyWidth = Math.min(normalKeyWidth, 60);

        int specialKeyWidth = (int) (normalKeyWidth * 1.6);
        int keyHeight = (int) (normalKeyWidth * 1.3);
        keyHeight = Math.max(keyHeight, 28);

        for (int row = 0; row < KEY_ROWS.length; row++) {
            List<KeyDescriptor> rowKeys = getRowKeys(row);
            int rowWidth = computeRowWidth(rowKeys, normalKeyWidth, specialKeyWidth, keyGap);
            int xOffset = padding + (availableWidth - rowWidth) / 2;
            int totalHeight = KEY_ROWS.length * keyHeight + (KEY_ROWS.length - 1) * vGap;
            int yOffset = padding + row * (keyHeight + vGap) + (height - 2 * padding - totalHeight) / 2;

            for (KeyDescriptor key : rowKeys) {
                int keyWidth = key.isSpecial ? specialKeyWidth : normalKeyWidth;

                Color background = key.isSpecial ? Colors.KEY_BG : keyColors.getOrDefault(key.letter, Colors.KEY_BG);
                graphics2D.setColor(background);
                graphics2D.fillRoundRect(xOffset, yOffset, keyWidth, keyHeight, 6, 6);

                int fontSize = key.isSpecial ? (int) (normalKeyWidth * 0.22) : (int) (normalKeyWidth * 0.3);
                graphics2D.setFont(new Font("Arial", Font.BOLD, fontSize));
                graphics2D.setColor(Colors.WHITE);
                FontMetrics fontMetrics = graphics2D.getFontMetrics();
                int tileX = xOffset + (keyWidth - fontMetrics.stringWidth(key.label)) / 2;
                int tileY = yOffset + (keyHeight + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
                graphics2D.drawString(key.label, tileX, tileY);

                key.bounds = new Rectangle(xOffset, yOffset, keyWidth, keyHeight);
                xOffset += keyWidth + keyGap;
            }
        }

        graphics2D.dispose();
    }

    private List<KeyDescriptor> getRowKeys(int rowIndex) {
        List<KeyDescriptor> rowKeys = new ArrayList<>();
        int keyStartOffset = 0;
        for (int row = 0; row < rowIndex; row++) {
            keyStartOffset += KEY_ROWS[row].length() + (row == 2 ? 2 : 0);
        }
        int keyCount = KEY_ROWS[rowIndex].length() + (rowIndex == 2 ? 2 : 0);
        for (int i = keyStartOffset; i < keyStartOffset + keyCount; i++) {
            rowKeys.add(keys.get(i));
        }
        return rowKeys;
    }

    private int computeRowWidth(List<KeyDescriptor> rowKeys, int normalKeyWidth, int specialKeyWidth, int gap) {
        int totalRowWidth = 0;
        for (KeyDescriptor key : rowKeys) {
            totalRowWidth += key.isSpecial ? specialKeyWidth : normalKeyWidth;
        }
        totalRowWidth += (rowKeys.size() - 1) * gap;
        return totalRowWidth;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 160);
    }

    @FunctionalInterface
    public interface LetterInputListener {
        void onKeyPress(char letter);
    }
}
