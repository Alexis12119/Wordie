package com.Wordie.view;

import com.Wordie.model.TileState;
import javax.swing.*;
import java.awt.*;

public class TilePanel extends JPanel {

    private final int rows;
    private final int cols;
    private final char[][] letters;
    private final TileState[][] states;

    public TilePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.letters = new char[rows][cols];
        this.states = new TileState[rows][cols];

        setBackground(Colors.BG);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                states[r][c] = TileState.EMPTY;
            }
        }
    }

    public void setLetter(int row, int col, char letter) {
        letters[row][col] = letter;
        repaint();
    }

    public void clearLetter(int row, int col) {
        letters[row][col] = '\0';
        repaint();
    }

    public void setTileState(int row, int col, TileState state) {
        states[row][col] = state;
        repaint();
    }

    public void resetAll() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                letters[r][c] = '\0';
                states[r][c] = TileState.EMPTY;
            }
        }
        repaint();
    }

    public String getWordAtRow(int row) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < cols; c++) {
            sb.append(letters[row][c]);
        }
        return sb.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int gap = Math.max(w / 120, 3);
        int tileSize = Math.min(
            (w - gap * (cols + 1)) / cols,
            (h - gap * (rows + 1)) / rows
        );
        tileSize = Math.max(tileSize, 20);
        tileSize = Math.min(tileSize, 80);

        int gridW = cols * tileSize + (cols - 1) * gap;
        int gridH = rows * tileSize + (rows - 1) * gap;
        int startX = (w - gridW) / 2;
        int startY = (h - gridH) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = startX + c * (tileSize + gap);
                int y = startY + r * (tileSize + gap);

                TileState state = states[r][c];
                Color bg = switch (state) {
                    case CORRECT -> Colors.GREEN;
                    case PRESENT -> Colors.YELLOW;
                    case ABSENT -> Colors.GRAY;
                    case EMPTY -> Colors.DARK;
                };
                Color border = state == TileState.EMPTY ? Colors.TILE_BORDER : bg;

                g2.setColor(bg);
                g2.fillRoundRect(x, y, tileSize, tileSize, 4, 4);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, tileSize, tileSize, 4, 4);

                char letter = letters[r][c];
                if (letter != '\0') {
                    g2.setColor(Colors.WHITE);
                    int fontSize = (int) (tileSize * 0.5);
                    g2.setFont(new Font("Arial", Font.BOLD, fontSize));
                    FontMetrics fm = g2.getFontMetrics();
                    String text = String.valueOf(letter);
                    int tx = x + (tileSize - fm.stringWidth(text)) / 2;
                    int ty = y + (tileSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, tx, ty);
                }
            }
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 360);
    }
}
