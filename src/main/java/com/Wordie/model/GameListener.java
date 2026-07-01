package com.Wordie.model;

public interface GameListener {
    void onTileUpdated(int row, int col, char letter, TileState tileState);
    void onKeyUpdated(char letter, TileState tileState);
    void onRowCompleted(int row);
    void onGameOver(boolean won, String targetWord);
    void onGameReset();
    void onTimerUpdated(int secondsRemaining);
    void onDifficultyChanged(Difficulty difficulty);
}
