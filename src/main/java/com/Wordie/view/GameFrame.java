package com.Wordie.view;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final TilePanel tilePanel;
    private final KeyboardPanel keyboardPanel;

    public GameFrame(TilePanel tilePanel, KeyboardPanel keyboardPanel) {
        super("Wordie");
        this.tilePanel = tilePanel;
        this.keyboardPanel = keyboardPanel;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Colors.BG);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Colors.BG);

        add(tilePanel, BorderLayout.CENTER);
        add(keyboardPanel, BorderLayout.SOUTH);

        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public TilePanel getTilePanel() { return tilePanel; }
    public KeyboardPanel getKeyboardPanel() { return keyboardPanel; }
}
