package com.Wordie.view;

import com.Wordie.model.TileState;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class TilePanel extends JPanel {
    private final JLabel[][] tiles;
    private final int rows;
    private final int cols;

    public TilePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new JLabel[rows][cols];

        setLayout(new GridLayout(rows, cols, 3, 3));
        setBackground(Colors.BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JLabel tile = createTile();
                tiles[r][c] = tile;
                add(tile);
            }
        }
    }

    private static JLabel createTile() {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Colors.DARK);
        label.setForeground(Colors.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 26));
        label.setPreferredSize(new Dimension(56, 56));
        label.setBorder(new LineBorder(Colors.TILE_BORDER, 2));
        return label;
    }

    public void setLetter(int row, int col, char letter) {
        tiles[row][col].setText(String.valueOf(letter));
    }

    public void clearLetter(int row, int col) {
        tiles[row][col].setText("");
    }

    public void setTileState(int row, int col, TileState state) {
        JLabel tile = tiles[row][col];
        Color bg = switch (state) {
            case CORRECT -> Colors.GREEN;
            case PRESENT -> Colors.YELLOW;
            case ABSENT -> Colors.GRAY;
            case EMPTY -> Colors.DARK;
        };
        Color border = state == TileState.EMPTY ? Colors.TILE_BORDER : bg;
        tile.setBackground(bg);
        tile.setBorder(new LineBorder(border, 2));
    }

    public void setRowState(int row, TileState[] states) {
        for (int c = 0; c < cols && c < states.length; c++) {
            setTileState(row, c, states[c]);
        }
    }

    public void resetAll() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c].setText("");
                tiles[r][c].setBackground(Colors.DARK);
                tiles[r][c].setBorder(new LineBorder(Colors.TILE_BORDER, 2));
            }
        }
    }
}
