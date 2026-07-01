package com.Wordie;

import javax.swing.SwingUtilities;

import com.Wordie.controller.GameController;
import com.Wordie.model.Difficulty;
import com.Wordie.model.GameModel;
import com.Wordie.model.GuessEvaluator;
import com.Wordie.model.RandomWordPicker;
import com.Wordie.model.WordBank;
import com.Wordie.model.WordDictionary;
import com.Wordie.model.WordPicker;
import com.Wordie.view.GameFrame;
import com.Wordie.view.KeyboardPanel;
import com.Wordie.view.TilePanel;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordDictionary dict = new WordDictionary(WordBank.WORDS);
            WordPicker picker = new RandomWordPicker(dict);
            GuessEvaluator evaluator = new GuessEvaluator();
            GameModel model = new GameModel(evaluator, picker.pickWord(), Difficulty.MEDIUM);

            TilePanel tilePanel = new TilePanel(GameModel.MAX_GUESSES, GameModel.WORD_LENGTH);
            KeyboardPanel keyboardPanel = new KeyboardPanel();
            GameFrame frame = new GameFrame(tilePanel, keyboardPanel);

            new GameController(model, frame, dict, picker);
            frame.setVisible(true);
        });
    }
}
