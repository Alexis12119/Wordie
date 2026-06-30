package com.Wordie.model;

public class GuessEvaluator {

    public EvaluationResult evaluate(String guess, String target) {
        String guessUp = guess.toUpperCase();
        String targetUp = target.toUpperCase();

        TileState[] states = new TileState[guessUp.length()];
        boolean[] used = new boolean[targetUp.length()];

        for (int i = 0; i < guessUp.length(); i++) {
            if (guessUp.charAt(i) == targetUp.charAt(i)) {
                states[i] = TileState.CORRECT;
                used[i] = true;
            }
        }

        for (int i = 0; i < guessUp.length(); i++) {
            if (states[i] == TileState.CORRECT) continue;
            for (int j = 0; j < targetUp.length(); j++) {
                if (!used[j] && guessUp.charAt(i) == targetUp.charAt(j)) {
                    states[i] = TileState.PRESENT;
                    used[j] = true;
                    break;
                }
            }
            if (states[i] == null) {
                states[i] = TileState.ABSENT;
            }
        }

        return new EvaluationResult(states, guessUp.equals(targetUp));
    }

    public static class EvaluationResult {
        private final TileState[] states;
        private final boolean correct;

        public EvaluationResult(TileState[] states, boolean correct) {
            this.states = states;
            this.correct = correct;
        }

        public TileState[] getStates() { return states; }
        public boolean isCorrect() { return correct; }
    }
}
