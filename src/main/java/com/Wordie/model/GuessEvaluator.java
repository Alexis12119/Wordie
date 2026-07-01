package com.Wordie.model;

public class GuessEvaluator {

    public GuessOutcome scoreGuess(String guess, String target) {
        String guessUppercase = guess.toUpperCase();
        String targetUppercase = target.toUpperCase();

        TileState[] letterStates = new TileState[guessUppercase.length()];
        boolean[] letterMatched = new boolean[targetUppercase.length()];

        for (int i = 0; i < guessUppercase.length(); i++) {
            if (guessUppercase.charAt(i) == targetUppercase.charAt(i)) {
                letterStates[i] = TileState.CORRECT;
                letterMatched[i] = true;
            }
        }

        for (int i = 0; i < guessUppercase.length(); i++) {
            if (letterStates[i] == TileState.CORRECT) continue;
            for (int j = 0; j < targetUppercase.length(); j++) {
                if (!letterMatched[j] && guessUppercase.charAt(i) == targetUppercase.charAt(j)) {
                    letterStates[i] = TileState.PRESENT;
                    letterMatched[j] = true;
                    break;
                }
            }
            if (letterStates[i] == null) {
                letterStates[i] = TileState.ABSENT;
            }
        }

        return new GuessOutcome(letterStates, guessUppercase.equals(targetUppercase));
    }

    public static class GuessOutcome {
        private final TileState[] letterStates;
        private final boolean correct;

        public GuessOutcome(TileState[] letterStates, boolean correct) {
            this.letterStates = letterStates;
            this.correct = correct;
        }

        public TileState[] getLetterStates() { return letterStates; }
        public boolean isCorrect() { return correct; }
    }
}
