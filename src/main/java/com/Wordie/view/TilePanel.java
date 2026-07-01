package com.Wordie.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.Wordie.model.TileState;

public class TilePanel extends JPanel {

    private final int rows;
    private final int columns;
    private final char[][] letters;
    private final TileState[][] tileStates;

    public TilePanel(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.letters = new char[rows][columns];
        this.tileStates = new TileState[rows][columns];

        setBackground(Colors.BG);

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                tileStates[row][column] = TileState.EMPTY;
            }
        }
    }

    public void setLetter(int row, int column, char letter) {
        letters[row][column] = letter;
        repaint();
    }

    public void clearLetter(int row, int column) {
        letters[row][column] = '\0';
        repaint();
    }

    public void setTileState(int row, int column, TileState tileState) {
        tileStates[row][column] = tileState;
        repaint();
    }

    public void resetAll() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                letters[row][column] = '\0';
                tileStates[row][column] = TileState.EMPTY;
            }
        }
        repaint();
    }

    public String getWordAtRow(int row) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int column = 0; column < columns; column++) {
            stringBuilder.append(letters[row][column]);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int gap = Math.max(width / 120, 3);
        int tileSize = Math.min(
            (width - gap * (columns + 1)) / columns,
            (height - gap * (rows + 1)) / rows
        );
        tileSize = Math.max(tileSize, 20);
        tileSize = Math.min(tileSize, 80);

        int gridWidth = columns * tileSize + (columns - 1) * gap;
        int gridHeight = rows * tileSize + (rows - 1) * gap;
        int startX = (width - gridWidth) / 2;
        int startY = (height - gridHeight) / 2;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int x = startX + column * (tileSize + gap);
                int y = startY + row * (tileSize + gap);

                TileState tileState = tileStates[row][column];
                Color background = switch (tileState) {
                    case CORRECT -> Colors.GREEN;
                    case PRESENT -> Colors.YELLOW;
                    case ABSENT -> Colors.GRAY;
                    case EMPTY -> Colors.DARK;
                };
                Color border = tileState == TileState.EMPTY ? Colors.TILE_BORDER : background;

                graphics2D.setColor(background);
                graphics2D.fillRoundRect(x, y, tileSize, tileSize, 4, 4);
                graphics2D.setColor(border);
                graphics2D.setStroke(new BasicStroke(2));
                graphics2D.drawRoundRect(x, y, tileSize, tileSize, 4, 4);

                char letter = letters[row][column];
                if (letter != '\0') {
                    graphics2D.setColor(Colors.WHITE);
                    int fontSize = (int) (tileSize * 0.5);
                    graphics2D.setFont(new Font("Arial", Font.BOLD, fontSize));
                    FontMetrics fontMetrics = graphics2D.getFontMetrics();
                    String text = String.valueOf(letter);
                    int tileX = x + (tileSize - fontMetrics.stringWidth(text)) / 2;
                    int tileY = y + (tileSize + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
                    graphics2D.drawString(text, tileX, tileY);
                }
            }
        }

        graphics2D.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 360);
    }
}
