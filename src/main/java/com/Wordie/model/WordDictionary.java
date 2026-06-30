package com.Wordie.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WordDictionary {
    private final String[] words;
    private final Set<String> lookup;

    public WordDictionary(String[] words) {
        this.words = words;
        this.lookup = new HashSet<>(Arrays.asList(words));
    }

    public boolean isValid(String word) {
        return lookup.contains(word.toLowerCase());
    }

    public String getWord(int index) {
        return words[index];
    }

    public int size() {
        return words.length;
    }
}
