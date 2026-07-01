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
    private Difficulty difficulty;

    public GameModel(GuessEvaluator evaluator, String targetWord) {
        this(evaluator, targetWord, Difficulty.MEDIUM);
    }

    public GameModel(GuessEvaluator evaluator, String targetWord, Difficulty difficulty) {
        this.evaluator = evaluator;
        this.targetWord = targetWord;
        this.difficulty = difficulty;
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
    public Difficulty getDifficulty() { return difficulty; }
    public int getTimeLimit() { return difficulty.getMaxSeconds(); }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        notifyDifficultyChanged(difficulty);
    }

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

    public GuessEvaluator.GuessOutcome submitGuess(String guess) {
        String guessUp = guess.toUpperCase();

        GuessEvaluator.GuessOutcome guessResult = evaluator.scoreGuess(guessUp, targetWord);
        guesses.add(guessUp);

        TileState[] letterStates = guessResult.getLetterStates();
        for (int i = 0; i < WORD_LENGTH; i++) {
            notifyTileUpdated(currentRow, i, guessUp.charAt(i), letterStates[i]);
            notifyKeyUpdated(guessUp.charAt(i), letterStates[i]);
        }

        notifyRowCompleted(currentRow);

        if (guessResult.isCorrect()) {
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

        return guessResult;
    }

    public void timeUp() {
        gameOver = true;
        notifyGameOver(false, targetWord);
    }

    public void reset(String newTarget) {
        reset(newTarget, difficulty);
    }

    public void reset(String newTarget, Difficulty difficulty) {
        targetWord = newTarget;
        this.difficulty = difficulty;
        guesses.clear();
        currentRow = 0;
        currentCol = 0;
        gameOver = false;
        notifyGameReset();
        notifyDifficultyChanged(difficulty);
    }

    private void notifyTileUpdated(int row, int col, char letter, TileState tileState) {
        for (GameListener l : listeners) {
            l.onTileUpdated(row, col, letter, tileState);
        }
    }

    private void notifyKeyUpdated(char letter, TileState tileState) {
        for (GameListener l : listeners) {
            l.onKeyUpdated(letter, tileState);
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

    private void notifyDifficultyChanged(Difficulty difficulty) {
        for (GameListener l : listeners) {
            l.onDifficultyChanged(difficulty);
        }
    }
}
