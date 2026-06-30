package com.Wordie.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private final TilePanel tilePanel;
    private final KeyboardPanel keyboardPanel;

    public GameFrame(TilePanel tilePanel, KeyboardPanel keyboardPanel) {
        super("Wordie");
        this.tilePanel = tilePanel;
        this.keyboardPanel = keyboardPanel;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    GameFrame.this, "Are you sure you want to exit?", "Wordie",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        setBackground(Colors.BG);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Colors.BG);

        add(tilePanel, BorderLayout.CENTER);
        add(keyboardPanel, BorderLayout.SOUTH);

        setResizable(false);
        setSize(400, 500);

        setLocationRelativeTo(null);
    }

    public TilePanel getTilePanel() { return tilePanel; }
    public KeyboardPanel getKeyboardPanel() { return keyboardPanel; }

    @Override
    public void invalidate() {
        super.invalidate();
        tilePanel.repaint();
        keyboardPanel.repaint();
    }
}
