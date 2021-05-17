package com.wzk.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.wzk.entity.Dictionary;

import lombok.extern.slf4j.Slf4j;

/**
 * Uses a Dictionary to find anagrams for a given word
 */
@Slf4j
public class AnagramFinder {

    private final Dictionary dictionary;
    public AnagramFinder(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Given a target word, order the characters in the target word alphabetically, find all the words in the dictionary
     * with the same length as the target word, order the characters in each dictionary word alphabetically,
     * return all words from the dictionary where the sorted dictionary word matches the sorted target word.
     * This method is case insensitive.
     * @param targetWord the word to find anagrams of
     * @return the anagrams for the target word
     */
    public List<String> findAnagrams(String targetWord) {
        String normalizedWord = targetWord;
        if (dictionary.isIgnoreCase()) {
            normalizedWord = targetWord.toLowerCase(Locale.ROOT);
            log.debug("Word {} was converted to lowercase {}", targetWord, normalizedWord);
        }
        long startTime = System.currentTimeMillis();
        List<String> wordsMatchingLength = dictionary.getWordsOfLength(normalizedWord.length());
        String sortedString = convertToSortedString(normalizedWord);
        List<String> anagrams = wordsMatchingLength.stream()
            .filter( word -> {
                String charSortedString = convertToSortedString(word);
                return charSortedString.equals(sortedString); })
            .collect(Collectors.toList());
        anagrams.remove(normalizedWord); // don't return the word entered by the user as an anagram
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Found {} anagrams for word '{}', took {}ms", anagrams.size(), targetWord, elapsedTime);
        return anagrams;
    }

    /**
     * Takes the target word and sorts the characters in the word alphabetically, returns a String representation of
     * this sorted character array.
     * @param targetWord the word to convert
     * @return the target word with its characters sorted alphabetically
     */
    public String convertToSortedString(String targetWord) {
        char[] charArray = targetWord.toCharArray();
        Arrays.sort(charArray);
        return String.valueOf(charArray);
    }
}
