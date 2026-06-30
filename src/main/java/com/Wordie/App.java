package com.Wordie;

import com.Wordie.controller.GameController;
import com.Wordie.model.*;
import com.Wordie.view.*;
import javax.swing.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordDictionary dict = new WordDictionary(WordBank.WORDS);
            WordPicker picker = new RandomWordPicker(dict);
            GuessEvaluator evaluator = new GuessEvaluator();
            GameModel model = new GameModel(evaluator, picker.pickWord());

            TilePanel tilePanel = new TilePanel(GameModel.MAX_GUESSES, GameModel.WORD_LENGTH);
            KeyboardPanel keyboardPanel = new KeyboardPanel();
            GameFrame frame = new GameFrame(tilePanel, keyboardPanel);

            new GameController(model, frame, dict, picker);
            frame.setVisible(true);
        });
    }
}
