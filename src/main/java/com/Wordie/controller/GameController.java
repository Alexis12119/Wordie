package com.Wordie.controller;

import com.Wordie.model.*;
import com.Wordie.view.*;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController implements GameListener {
    private final GameModel model;
    private final GameFrame frame;
    private final TilePanel tilePanel;
    private final KeyboardPanel keyboardPanel;
    private final WordDictionary dictionary;
    private final WordPicker wordPicker;

    public GameController(GameModel model, GameFrame frame, WordDictionary dictionary, WordPicker wordPicker) {
        this.model = model;
        this.frame = frame;
        this.tilePanel = frame.getTilePanel();
        this.keyboardPanel = frame.getKeyboardPanel();
        this.dictionary = dictionary;
        this.wordPicker = wordPicker;

        model.addListener(this);
        wireKeyboard();
        wirePhysicalKeyboard();
    }

    private void wireKeyboard() {
        keyboardPanel.setOnEnter(this::handleSubmit);
        keyboardPanel.setOnDelete(this::handleDelete);
        keyboardPanel.setInputHandler(this::handleCharInput);
    }

    private void wirePhysicalKeyboard() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (model.isGameOver()) return;
                char c = Character.toUpperCase(e.getKeyChar());
                if (c >= 'A' && c <= 'Z') {
                    handleCharInput(c);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    handleDelete();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSubmit();
                }
            }
        });
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
    }

    private void handleCharInput(char c) {
        model.typeLetter(c);
    }

    private void handleDelete() {
        model.deleteLetter();
    }

    private void handleSubmit() {
        if (model.isGameOver()) return;
        if (model.getCurrentCol() != GameModel.WORD_LENGTH) {
            JOptionPane.showMessageDialog(frame, "Not enough letters", "Wordie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < GameModel.WORD_LENGTH; c++) {
            sb.append(((JLabel) tilePanel.getComponent(model.getCurrentRow() * GameModel.WORD_LENGTH + c)).getText());
        }
        String guess = sb.toString();

        if (!dictionary.isValid(guess)) {
            JOptionPane.showMessageDialog(frame, "Not a valid word", "Wordie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.submitGuess(guess);
    }

    @Override
    public void onTileUpdated(int row, int col, char letter, TileState state) {
        if (letter == '\0') {
            tilePanel.clearLetter(row, col);
            tilePanel.setTileState(row, col, TileState.EMPTY);
        } else {
            tilePanel.setLetter(row, col, letter);
            if (state != TileState.EMPTY) {
                tilePanel.setTileState(row, col, state);
            }
        }
    }

    @Override
    public void onKeyUpdated(char letter, TileState state) {
        keyboardPanel.setKeyColor(letter, state);
    }

    @Override
    public void onRowCompleted(int row) {
    }

    @Override
    public void onGameOver(boolean won, String targetWord) {
        String msg = won ? "You got it!\nThe word was " + targetWord
                         : "Game Over!\nThe word was " + targetWord;
        JOptionPane.showMessageDialog(frame, msg, "Wordie", JOptionPane.INFORMATION_MESSAGE);
        offerReset();
    }

    @Override
    public void onGameReset() {
        tilePanel.resetAll();
        keyboardPanel.resetAll();
        frame.requestFocusInWindow();
    }

    private void offerReset() {
        int choice = JOptionPane.showConfirmDialog(frame, "Play again?", "Wordie", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            String newTarget = wordPicker.pickWord();
            model.reset(newTarget);
        } else {
            System.exit(0);
        }
    }
}
