package com.Wordie.view;

import com.Wordie.model.Difficulty;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private final TilePanel tilePanel;
    private final KeyboardPanel keyboardPanel;
    private final JLabel timerLabel;
    private Difficulty currentDifficulty;

    private Runnable onNewGame;
    private Runnable onLeaderboard;

    public GameFrame(TilePanel tilePanel, KeyboardPanel keyboardPanel) {
        super("Wordie");
        this.tilePanel = tilePanel;
        this.keyboardPanel = keyboardPanel;
        this.currentDifficulty = Difficulty.MEDIUM;

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

        timerLabel = new JLabel(formatLabel(Difficulty.MEDIUM, 0), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(Colors.WHITE);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        add(timerLabel, BorderLayout.NORTH);

        add(tilePanel, BorderLayout.CENTER);
        add(keyboardPanel, BorderLayout.SOUTH);

        setJMenuBar(createMenuBar());

        setResizable(false);
        setSize(400, 530);
        setLocationRelativeTo(null);
    }

    public void setOnNewGame(Runnable r) { this.onNewGame = r; }
    public void setOnLeaderboard(Runnable r) { this.onLeaderboard = r; }

    public TilePanel getTilePanel() { return tilePanel; }
    public KeyboardPanel getKeyboardPanel() { return keyboardPanel; }

    public void updateTimer(int secondsRemaining) {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("%s · %02d:%02d", currentDifficulty.name(), minutes, seconds));
    }

    public void updateDifficulty(Difficulty difficulty) {
        currentDifficulty = difficulty;
        timerLabel.setText(formatLabel(difficulty, 0));
    }

    public Difficulty getCurrentDifficulty() { return currentDifficulty; }

    public Difficulty showDifficultyPicker() {
        String[] options = {"Easy (5 min)", "Medium (3 min)", "Hard (1 min)"};
        int choice = JOptionPane.showOptionDialog(
            this, "Choose difficulty:", "New Game",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[1]
        );
        return switch (choice) {
            case 0 -> Difficulty.EASY;
            case 1 -> Difficulty.MEDIUM;
            case 2 -> Difficulty.HARD;
            default -> currentDifficulty;
        };
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> {
            if (onNewGame != null) onNewGame.run();
        });
        menu.add(newGameItem);

        menu.addSeparator();

        JMenuItem leaderboardItem = new JMenuItem("Leaderboard");
        leaderboardItem.addActionListener(e -> {
            if (onLeaderboard != null) onLeaderboard.run();
        });
        menu.add(leaderboardItem);

        menuBar.add(menu);
        return menuBar;
    }

    private String formatLabel(Difficulty d, int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%s · %02d:%02d", d.name(), m, s);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        tilePanel.repaint();
        keyboardPanel.repaint();
    }
}
