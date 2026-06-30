package com.Wordie.model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    public static final int MAX_GUESSES = 6;
    public static final int WORD_LENGTH = 5;

    private final GuessEvaluator evaluator;
    private String targetWord;
    private final List<String> guesses;
    private final List<GameListener> listeners;

    private int currentRow;
    private int currentCol;
    private boolean gameOver;

    public GameModel(GuessEvaluator evaluator, String targetWord) {
        this.evaluator = evaluator;
        this.targetWord = targetWord;
        this.guesses = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.currentRow = 0;
        this.currentCol = 0;
        this.gameOver = false;
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    public String getTargetWord() { return targetWord; }
    public int getCurrentRow() { return currentRow; }
    public int getCurrentCol() { return currentCol; }
    public boolean isGameOver() { return gameOver; }
    public List<String> getGuesses() { return guesses; }

    public void typeLetter(char letter) {
        if (gameOver || currentRow >= MAX_GUESSES || currentCol >= WORD_LENGTH) return;
        char upper = Character.toUpperCase(letter);
        currentCol++;
        notifyTileUpdated(currentRow, currentCol - 1, upper, TileState.EMPTY);
    }

    public void deleteLetter() {
        if (gameOver || currentCol <= 0) return;
        currentCol--;
        notifyTileUpdated(currentRow, currentCol, '\0', TileState.EMPTY);
    }

    public GuessEvaluator.EvaluationResult submitGuess(String guess) {
        String guessUp = guess.toUpperCase();

        GuessEvaluator.EvaluationResult result = evaluator.evaluate(guessUp, targetWord);
        guesses.add(guessUp);

        TileState[] states = result.getStates();
        for (int i = 0; i < WORD_LENGTH; i++) {
            notifyTileUpdated(currentRow, i, guessUp.charAt(i), states[i]);
            notifyKeyUpdated(guessUp.charAt(i), states[i]);
        }

        notifyRowCompleted(currentRow);

        if (result.isCorrect()) {
            gameOver = true;
            notifyGameOver(true, targetWord);
        } else {
            currentRow++;
            currentCol = 0;
            if (currentRow >= MAX_GUESSES) {
                gameOver = true;
                notifyGameOver(false, targetWord);
            }
        }

        return result;
    }

    public void reset(String newTarget) {
        targetWord = newTarget;
        guesses.clear();
        currentRow = 0;
        currentCol = 0;
        gameOver = false;
        notifyGameReset();
    }

    private void notifyTileUpdated(int row, int col, char letter, TileState state) {
        for (GameListener l : listeners) {
            l.onTileUpdated(row, col, letter, state);
        }
    }

    private void notifyKeyUpdated(char letter, TileState state) {
        for (GameListener l : listeners) {
            l.onKeyUpdated(letter, state);
        }
    }

    private void notifyRowCompleted(int row) {
        for (GameListener l : listeners) {
            l.onRowCompleted(row);
        }
    }

    private void notifyGameOver(boolean won, String targetWord) {
        for (GameListener l : listeners) {
            l.onGameOver(won, targetWord);
        }
    }

    private void notifyGameReset() {
        for (GameListener l : listeners) {
            l.onGameReset();
        }
    }
}
