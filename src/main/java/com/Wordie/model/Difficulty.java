package com.Wordie.model;

public enum Difficulty {
    EASY(300),
    MEDIUM(180),
    HARD(60);

    private final int maxSeconds;

    Difficulty(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }

    public int getMaxSeconds() {
        return maxSeconds;
    }
}
