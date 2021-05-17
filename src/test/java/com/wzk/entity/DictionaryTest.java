package com.wzk.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

class DictionaryTest {

    @Test
    void testDictionaryLoadsFromFile() {
        InputStream fileStream = DictionaryTest.class.getClassLoader().getResourceAsStream("test-word-file.txt");
        Dictionary dictionary = new Dictionary(fileStream, true);
        assertNotNull(dictionary);
    }

    @Test
    void testEmptyDictionaryLoadsWhenNullFilename() {
        Dictionary dictionary = new Dictionary(null, true);
        assertEquals(0, dictionary.getSize());
    }

    @Test
    void testDictionaryRemovesDuplicates() {
        InputStream fileStream = DictionaryTest.class.getClassLoader().getResourceAsStream("duplicate-words.txt");
        Dictionary dictionary = new Dictionary(fileStream, true);
        assertEquals(2, dictionary.getSize());
    }

    @Test
    void testDictionaryIgnoresCaseWhenLoading() {
        InputStream fileStream = DictionaryTest.class.getClassLoader()
            .getResourceAsStream("mixed-case-words.txt");
        Dictionary dictionary = new Dictionary(fileStream, true);
        assertEquals(4, dictionary.getSize());
    }

    @Test
    void testAddWordToCaseSensitiveDictionary() {
        Dictionary caseSensitiveDict = new Dictionary(null, false);

        assertFalse(caseSensitiveDict.containsWord("Hello"));
        assertFalse(caseSensitiveDict.containsWord("hello"));

        caseSensitiveDict.addWord("Hello");
        caseSensitiveDict.addWord("hello");

        assertEquals(2, caseSensitiveDict.getSize());
    }

    @Test
    void testAddWordToCaseInsensitiveDictionary() {
        Dictionary caseInsensitiveDict = new Dictionary(null, true);

        assertFalse(caseInsensitiveDict.containsWord("Hello"));
        assertFalse(caseInsensitiveDict.containsWord("hello"));

        caseInsensitiveDict.addWord("Hello");
        caseInsensitiveDict.addWord("hello");

        assertTrue(caseInsensitiveDict.containsWord("Hello"));
        assertTrue(caseInsensitiveDict.containsWord("hello"));
        assertTrue(caseInsensitiveDict.containsWord("heLLo"));
        assertEquals(1, caseInsensitiveDict.getSize());
    }

    @Test
    void testRemoveWordCaseSensitiveDictionary() {
        InputStream fileStream = DictionaryTest.class.getClassLoader().getResourceAsStream("mixed-case-words.txt");
        Dictionary caseSensitiveDict = new Dictionary(fileStream, false);
        assertEquals(6, caseSensitiveDict.getSize());
        caseSensitiveDict.removeWord("Mate");
        assertEquals(5, caseSensitiveDict.getSize());
        assertTrue(caseSensitiveDict.containsWord("matE"));
    }

    @Test
    void testRemoveWordCaseInsensitiveDictionary() {
        InputStream fileStream = DictionaryTest.class.getClassLoader().getResourceAsStream("mixed-case-words.txt");
        Dictionary caseSensitiveDict = new Dictionary(fileStream, true);
        assertEquals(4, caseSensitiveDict.getSize());
        caseSensitiveDict.removeWord("Mate");
        assertEquals(3, caseSensitiveDict.getSize());
        assertFalse(caseSensitiveDict.containsWord("matE"));
    }
}