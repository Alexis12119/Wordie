package com.Wordie.view;

import com.Wordie.model.TileState;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class KeyboardPanel extends JPanel {
    private final Map<Character, JButton> keys;
    private Runnable onEnter = () -> {};
    private Runnable onDelete = () -> {};
    private KeyInputHandler inputHandler = c -> {};

    public KeyboardPanel() {
        this.keys = new HashMap<>();

        setBackground(Colors.BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        for (String row : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            rowPanel.setBackground(Colors.BG);

            for (char c : row.toCharArray()) {
                JButton btn = createKeyButton(String.valueOf(c), 48, 58, 11);
                char letter = c;
                btn.addActionListener(e -> inputHandler.onKeyPress(letter));
                keys.put(c, btn);
                rowPanel.add(btn);
            }

            if (row == rows[2]) {
                JButton enterBtn = createKeyButton("Enter", 68, 58, 11);
                enterBtn.addActionListener(e -> onEnter.run());
                rowPanel.add(enterBtn);

                JButton delBtn = createKeyButton("DEL", 68, 58, 11);
                delBtn.addActionListener(e -> onDelete.run());
                rowPanel.add(delBtn);
            }

            add(rowPanel);
        }
    }

    public void setOnEnter(Runnable onEnter) { this.onEnter = onEnter; }
    public void setOnDelete(Runnable onDelete) { this.onDelete = onDelete; }
    public void setInputHandler(KeyInputHandler handler) { this.inputHandler = handler; }

    private static JButton createKeyButton(String text, int width, int height, int fontSize) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setFont(new Font("Arial", Font.BOLD, fontSize));
        btn.setBackground(Colors.KEY_BG);
        btn.setForeground(Colors.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        return btn;
    }

    public void setKeyColor(char letter, TileState state) {
        JButton btn = keys.get(Character.toUpperCase(letter));
        if (btn == null) return;

        Color current = btn.getBackground();
        Color newColor = switch (state) {
            case CORRECT -> Colors.GREEN;
            case PRESENT -> (current != Colors.GREEN) ? Colors.YELLOW : Colors.GREEN;
            case ABSENT -> (current != Colors.GREEN && current != Colors.YELLOW) ? Colors.GRAY : current;
            default -> current;
        };
        btn.setBackground(newColor);
    }

    public void resetAll() {
        for (JButton btn : keys.values()) {
            btn.setBackground(Colors.KEY_BG);
        }
    }

    @FunctionalInterface
    public interface KeyInputHandler {
        void onKeyPress(char letter);
    }
}
