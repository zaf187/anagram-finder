package com.wzk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.wzk.entity.Dictionary;

class AnagramFinderTest {

    private static Dictionary testDictionary;
    private static AnagramFinder anagramFinder;

    @BeforeAll
    static void initAll() {
        InputStream fileStream = AnagramFinderTest.class.getClassLoader().getResourceAsStream("test-word-file.txt");
        testDictionary = new Dictionary(fileStream, true);
        anagramFinder = new AnagramFinder(testDictionary);
    }

    @Test
    void testConvertedSortedStringIsEqualLength() {
        String targetWord = "test";
        String expectedWord = "estt";
        String actualWord = anagramFinder.convertToSortedString(targetWord);
        assertNotEquals(targetWord, actualWord);
        assertEquals(expectedWord, actualWord);
        assertEquals(targetWord.length(), actualWord.length());
    }

    @Test
    void testFindAnagramWithWord() {
        String targetWord = "bat";
        List<String> expectedAnagrams = Arrays.asList("tab");
        List<String> actualAnagrams = anagramFinder.findAnagrams(targetWord);
        assertEquals(1, actualAnagrams.size());
        assertEquals(expectedAnagrams, actualAnagrams);
    }

    @Test
    void testFindAnagramWithSymbols() {
        String targetSymbols = ",.90.";
        List<String> expectedAnagrams = Arrays.asList("90..,", ".90,.");
        List<String> actualAnagrams = anagramFinder.findAnagrams(targetSymbols);
        assertEquals(2, actualAnagrams.size());
        Collections.sort(expectedAnagrams);
        Collections.sort(actualAnagrams);
        assertEquals(expectedAnagrams, actualAnagrams);
    }

    @Test
    void testFindNoAnagrams() {
        String noAnagramWord = "refuge";
        List<String> expectedAnagrams = Collections.emptyList();
        List<String> actualAnagrams = anagramFinder.findAnagrams(noAnagramWord);
        assertEquals(0, actualAnagrams.size());
        assertEquals(expectedAnagrams, actualAnagrams);
    }

    @Test
    void testFindAnagramsCaseInsensitive() {
        String targetWord = "evil";
        List<String> expectedAnagrams = Arrays.asList("live");
        List<String> actualAnagrams = anagramFinder.findAnagrams(targetWord);
        assertEquals(1, actualAnagrams.size());
        assertEquals(expectedAnagrams, actualAnagrams);
    }
}