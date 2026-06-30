package com.Wordie.model;

import java.util.Random;

public class RandomWordPicker implements WordPicker {
    private final WordDictionary dictionary;
    private final Random random;

    public RandomWordPicker(WordDictionary dictionary) {
        this.dictionary = dictionary;
        this.random = new Random();
    }

    @Override
    public String pickWord() {
        return dictionary.getWord(random.nextInt(dictionary.size()));
    }
}
