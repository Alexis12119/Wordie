package com.Wordie.controller;

import com.Wordie.audio.AudioManager;
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
    private final AudioManager audioManager;
    private final Leaderboard leaderboard;
    private final KeyAdapter physicalKeyboardAdapter;
    private javax.swing.Timer gameTimer;
    private int secondsRemaining;
    private LeaderboardDialog leaderboardDialog;

    public GameController(GameModel model, GameFrame frame, WordDictionary dictionary, WordPicker wordPicker) {
        this.model = model;
        this.frame = frame;
        this.tilePanel = frame.getTilePanel();
        this.keyboardPanel = frame.getKeyboardPanel();
        this.dictionary = dictionary;
        this.wordPicker = wordPicker;
        this.audioManager = new AudioManager();
        this.leaderboard = new Leaderboard();

        model.addListener(this);
        wireKeyboard();
        physicalKeyboardAdapter = createPhysicalKeyboardAdapter();
        frame.addKeyListener(physicalKeyboardAdapter);
        wireMenu();

        secondsRemaining = model.getTimeLimit();
        frame.updateTimer(secondsRemaining);
        startTimer();
        audioManager.playBackground();
    }

    private void wireKeyboard() {
        keyboardPanel.setOnEnter(this::submitGuess);
        keyboardPanel.setOnDelete(this::deleteLastLetter);
        keyboardPanel.setInputHandler(this::typeLetter);
    }

    private KeyAdapter createPhysicalKeyboardAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (model.isGameOver()) return;
                char keyChar = Character.toUpperCase(e.getKeyChar());
                if (keyChar >= 'A' && keyChar <= 'Z') {
                    typeLetter(keyChar);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteLastLetter();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitGuess();
                }
            }
        };
    }

    private void wireMenu() {
        frame.setOnNewGame(() -> {
            int confirm = JOptionPane.showConfirmDialog(
                frame, "Are you sure? Progress will be lost.",
                "Wordie", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Difficulty difficulty = frame.showDifficultyPicker();
                startNewGame(difficulty);
            }
        });
        frame.setOnLeaderboard(this::showLeaderboard);
    }

    private void showLeaderboard() {
        if (leaderboardDialog == null) {
            leaderboardDialog = new LeaderboardDialog(frame, leaderboard);
        }
        leaderboardDialog.setVisible(true);
    }

    private void startNewGame(Difficulty difficulty) {
        stopTimer();
        audioManager.stopAll();
        String newTarget = wordPicker.pickWord();
        model.reset(newTarget, difficulty);
        secondsRemaining = difficulty.getMaxSeconds();
        frame.updateTimer(secondsRemaining);
        startTimer();
        audioManager.playBackground();
    }

    private void startTimer() {
        if (gameTimer != null) gameTimer.stop();
        gameTimer = new javax.swing.Timer(1000, e -> {
            if (model.isGameOver()) return;
            secondsRemaining--;
            frame.updateTimer(secondsRemaining);
            if (secondsRemaining <= 0) {
                stopTimer();
                model.timeUp();
            }
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
    }

    /** Releases all resources held by this controller. */
    public void cleanup() {
        stopTimer();
        audioManager.stopAll();
        model.removeListener(this);
        frame.removeKeyListener(physicalKeyboardAdapter);
        if (leaderboardDialog != null) {
            leaderboardDialog.dispose();
            leaderboardDialog = null;
        }
    }

    private void typeLetter(char c) {
        if (model.isGameOver()) return;
        model.typeLetter(c);
    }

    private void deleteLastLetter() {
        if (model.isGameOver()) return;
        model.deleteLetter();
    }

    private void submitGuess() {
        if (model.isGameOver()) return;
        if (model.getCurrentCol() != GameModel.WORD_LENGTH) {
            JOptionPane.showMessageDialog(frame, "Not enough letters", "Wordie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String guess = tilePanel.getWordAtRow(model.getCurrentRow());

        if (!dictionary.isValid(guess)) {
            JOptionPane.showMessageDialog(frame, "Not a valid word", "Wordie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.submitGuess(guess);
    }

    @Override
    public void onTileUpdated(int row, int col, char letter, TileState tileState) {
        if (letter == '\0') {
            tilePanel.clearLetter(row, col);
            tilePanel.setTileState(row, col, TileState.EMPTY);
        } else {
            tilePanel.setLetter(row, col, letter);
            if (tileState != TileState.EMPTY) {
                tilePanel.setTileState(row, col, tileState);
            }
        }
    }

    @Override
    public void onKeyUpdated(char letter, TileState tileState) {
        keyboardPanel.setKeyColor(letter, tileState);
    }

    @Override
    public void onRowCompleted(int row) {
    }

    @Override
    public void onGameOver(boolean won, String targetWord) {
        stopTimer();
        String gameOverMessage = won ? "You got it!\nThe word was " + targetWord
                                     : "Game Over!\nThe word was " + targetWord;
        JOptionPane.showMessageDialog(frame, gameOverMessage, "Wordie", JOptionPane.INFORMATION_MESSAGE);
        if (won) {
            audioManager.stopAll();
            audioManager.playWin();
            recordTopScore();
        } else {
            audioManager.stopAll();
            audioManager.playLoss();
        }
        offerReset();
    }

    private void recordTopScore() {
        int attempts = model.getCurrentRow() + 1;
        Difficulty difficulty = model.getDifficulty();
        if (leaderboard.isTopScore(difficulty, attempts)) {
            String name = JOptionPane.showInputDialog(frame, "New top score! Enter your name:", "Leaderboard", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                leaderboard.save(name.trim(), difficulty, attempts);
            }
        }
    }

    @Override
    public void onGameReset() {
        tilePanel.resetAll();
        keyboardPanel.resetAll();
        frame.requestFocusInWindow();
    }

    @Override
    public void onTimerUpdated(int secondsRemaining) {
    }

    @Override
    public void onDifficultyChanged(Difficulty difficulty) {
        frame.updateDifficulty(difficulty);
    }

    private void offerReset() {
        int playAgainChoice = JOptionPane.showConfirmDialog(frame, "Play again?", "Wordie", JOptionPane.YES_NO_OPTION);
        if (playAgainChoice == JOptionPane.YES_OPTION) {
            Difficulty difficulty = frame.showDifficultyPicker();
            startNewGame(difficulty);
        } else {
            cleanup();
            System.exit(0);
        }
    }
}
