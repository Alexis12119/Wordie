package com.Wordie.model;

public interface GameListener {
    void onTileUpdated(int row, int col, char letter, TileState state);
    void onKeyUpdated(char letter, TileState state);
    void onRowCompleted(int row);
    void onGameOver(boolean won, String targetWord);
    void onGameReset();
}
