package com.wzk.entity;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * A dictionary containing a list of words loaded from a file
 */
@Slf4j
public class Dictionary {
    private final Set<String> dictionarySet = Sets.newConcurrentHashSet();

    private final boolean ignoreCase;
    public Dictionary(InputStream fileStream, boolean ignoreCase) {
        log.info("Loading dictionary, should ignore cases? {}", ignoreCase);
        this.ignoreCase = ignoreCase;
        loadDictionary(fileStream, ignoreCase);
    }

    /**
     * Returns all words from the dictionary with the number of characters equal to the length.
     * @param length the number of characters that should be in a word
     * @return all words from the dictionary where each words character count is equal to length
     */
    public List<String> getWordsOfLength(int length) {
        long startTime = System.currentTimeMillis();
        List<String> words = dictionarySet.stream()
            .filter( word -> word.length() == length)
            .collect(Collectors.toList());
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Found {} words of length {} in {}ms",words.size(), length, elapsedTime);
        return words;
    }

    public int getSize() {
        return dictionarySet.size();
    }

    /**
     * Loads a file into the in memory dictionary. The dictionary is a set implementation so any duplicate
     * words are removed. The program has the option of ignoring case when handling words, if switched on all
     * dictionary entries are normalized to lowercase.
     * @param fileStream file to load in to the dictionary.
     * @param ignoreCase whether or not to ignore case.
     */
    private void loadDictionary(InputStream fileStream, boolean ignoreCase) {
        long startTime = System.currentTimeMillis();
        int lineCounter = 0;
        if (fileStream != null) {
            try (final Scanner fileScanner = new Scanner(fileStream)) {
                while(fileScanner.hasNext()) {
                    String line = fileScanner.nextLine();
                    String word = ignoreCase ? line.toLowerCase(Locale.ROOT) : line;
                    dictionarySet.add(word);
                    lineCounter++;
                }
            }
            int dictionarySize = dictionarySet.size();;
            int diff = lineCounter - dictionarySize;
            log.debug("There were {} lines loaded from the file", lineCounter);
            log.trace("Dictionary contains {} entries", dictionarySize);
            log.info("Difference between Dictionary entries and file {}", diff);
        } else {
            log.warn("No dictionary file supplied, starting with an empty dictionary");
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Dictionary loaded in {}ms", elapsedTime);
    }

    /**
     * Add a word to the dictionary. If ignore case is switched on then normalize this word to lowercase.
     * If the word is already in the dictionary then do not attempt to add it.
     * @param word word to add
     * @return whether or not the addition succeeded.
     */
    public boolean addWord(String word) {
        String wordToAdd = word;
        if (ignoreCase) {
            wordToAdd = word.toLowerCase(Locale.ROOT);
            log.debug("Word [{}] has been converted to lowercase [{}]", word, wordToAdd);
        }
        if (dictionarySet.contains(wordToAdd)) {
            log.info("Word [{}] is already in the dictionary, skipping add", wordToAdd);
            return false;
        } else {
            return dictionarySet.add(wordToAdd);
        }
    }

    /**
     * Remove a word from the dictionary. If ignore case is switched on then normalize this word to lowercase.
     * If the word is not already in the dictionary then do not attempt to remove it.
     * @param word word to remove
     * @return whether or not the removal succeeded.
     */
    public boolean removeWord(String word) {
        String wordToRemove = word;
        if (ignoreCase) {
            wordToRemove = word.toLowerCase(Locale.ROOT);
            log.debug("Word [{}] has been converted to lowercase [{}]", word, wordToRemove);
        }
        if (!dictionarySet.contains(wordToRemove)) {
            log.info("Word [{}] is NOT in the dictionary, skipping remove", wordToRemove);
            return false;
        } else {
            return dictionarySet.remove(wordToRemove);
        }
    }

    /**
     * Check if the dictionary contains a word already. If ignore case is switched on then normalize this word to
     * lowercase.
     * @param word word to find.
     * @return whether or not the word exists in the dictionary.
     */
    public boolean containsWord(String word) {
        String wordToFind = word;
        if (ignoreCase) {
            wordToFind = word.toLowerCase(Locale.ROOT);
            log.debug("Word [{}] has been converted to lowercase [{}]", word, wordToFind);
        }
        if (dictionarySet.contains(wordToFind)) {
            log.info("Word [{}] was found", wordToFind);
            return true;
        } else {
            log.info("Word [{}] NOT found", wordToFind);
            return false;
        }
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }
}
