package com.Wordie.view;

import com.Wordie.model.Difficulty;
import com.Wordie.model.Leaderboard;
import javax.swing.*;
import java.awt.*;

public class LeaderboardDialog extends JDialog {

    public LeaderboardDialog(JFrame owner, Leaderboard leaderboard) {
        super(owner, "Leaderboard", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        for (Difficulty d : Difficulty.values()) {
            tabs.addTab(d.name(), createPanel(leaderboard, d));
        }
        add(tabs, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Colors.BG);
        close.setBackground(Colors.KEY_BG);
        close.setForeground(Colors.WHITE);
        close.setFocusPainted(false);
        btnPanel.add(close);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(360, 320);
        setLocationRelativeTo(owner);
    }

    private JPanel createPanel(Leaderboard leaderboard, Difficulty difficulty) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.BG);

        java.util.List<Leaderboard.Entry> entries = leaderboard.getTop(difficulty);

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No records yet.", SwingConstants.CENTER);
            empty.setForeground(Colors.WHITE);
            panel.add(empty, BorderLayout.CENTER);
            return panel;
        }

        String[] columns = {"#", "Name", "Attempts", "Date"};
        Object[][] data = new Object[entries.size()][4];
        for (int i = 0; i < entries.size(); i++) {
            Leaderboard.Entry e = entries.get(i);
            data[i][0] = i + 1;
            data[i][1] = e.name();
            data[i][2] = e.attempts();
            data[i][3] = e.playedAt();
        }

        JTable table = new JTable(data, columns);
        table.setBackground(Colors.DARK);
        table.setForeground(Colors.WHITE);
        table.setGridColor(Colors.TILE_BORDER);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setBackground(Colors.KEY_BG);
        table.getTableHeader().setForeground(Colors.WHITE);
        table.setRowHeight(24);
        table.setEnabled(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Colors.DARK);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
