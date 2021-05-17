package com.wzk.network;

import static com.wzk.network.QueryProtocol.ADD_WORD;
import static com.wzk.network.QueryProtocol.REMOVE_WORD;
import static com.wzk.network.QueryProtocol.FIND_ANAGRAM;
import static com.wzk.network.QueryProtocol.MENU;
import static com.wzk.network.QueryProtocol.MSG_SENTINEL;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class QueryProtocolTest {

    @Test
    void testQueryProtocolStartsInMenuState() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        assertEquals(MENU, qp.getCurrentState());
    }

    @Test
    void testResponseForInitialRequestIsMenu() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        String menuResponse = qp.deriveMenuResponse(null);
        assertTrue(menuResponse.contains("[A]"));
        assertTrue(menuResponse.contains("[D]"));
        assertTrue(menuResponse.contains("[P]"));
    }

    @Test
    void testChoosingAddWordChangesCurrentState() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        String addWordResponse = qp.deriveMenuResponse("a");
        assertEquals(ADD_WORD, qp.getCurrentState());
        assertTrue(addWordResponse.contains("Enter a word to ADD"));
    }

    @Test
    void testChoosingDeleteWordChangesCurrentState() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        String deleteWordResponse = qp.deriveMenuResponse("d");
        assertEquals(REMOVE_WORD, qp.getCurrentState());
        assertTrue(deleteWordResponse.contains("Enter a word to DELETE"));
    }

    @Test
    void testChoosingFindAnagramChangesCurrentState() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        String findAnagramResponse = qp.deriveMenuResponse("p");
        assertEquals(FIND_ANAGRAM, qp.getCurrentState());
        assertTrue(findAnagramResponse.contains("Enter a word to find its anagrams"));
    }

    @Test
    void testEnteringInvalidInputPrintsMenuReminder() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        String invalidRequestResponse = qp.deriveMenuResponse("NOT_A_VALID_INPUT_FOR_CURRENT_STATE");
        assertEquals(MENU, qp.getCurrentState());
        assertTrue(invalidRequestResponse.contains("Invalid request"));
    }

    @Test
    void testAddWordSucceedsResponse() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        assertEquals(MENU, qp.getCurrentState());
        qp.deriveMenuResponse("a");
        assertEquals(ADD_WORD, qp.getCurrentState());
        String successResponse = qp.deriveAddWordResponse("word", true);
        assertTrue(successResponse.contains("was added"));
    }

    @Test
    void testAddWordFailsResponse() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        assertEquals(MENU, qp.getCurrentState());
        qp.deriveMenuResponse("a");
        assertEquals(ADD_WORD, qp.getCurrentState());
        String failureResponse = qp.deriveAddWordResponse("word", false);
        assertTrue(failureResponse.contains("Failed to add"));
        assertEquals(ADD_WORD, qp.getCurrentState());
    }

    @Test
    void testRemoveWordSucceedsResponse() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        assertEquals(MENU, qp.getCurrentState());
        qp.deriveMenuResponse("d");
        assertEquals(REMOVE_WORD, qp.getCurrentState());
        String successResponse = qp.deriveRemoveWordResponse("word", true);
        assertTrue(successResponse.contains("was removed"));
    }

    @Test
    void testRemoveWordFailsResponse() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        assertEquals(MENU, qp.getCurrentState());
        qp.deriveMenuResponse("d");
        assertEquals(REMOVE_WORD, qp.getCurrentState());
        String failureResponse = qp.deriveRemoveWordResponse("word", false);
        assertTrue(failureResponse.contains("Failed to remove"));
        assertEquals(REMOVE_WORD, qp.getCurrentState());
    }

    @Test
    void testFindAnagramResponse() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        assertEquals(MENU, qp.getCurrentState());
        qp.deriveMenuResponse("p");
        assertEquals(FIND_ANAGRAM, qp.getCurrentState());
        String anagramResponse = qp.deriveFindAnagramResponse("word", Arrays.asList("rowd"));
        assertTrue(anagramResponse.contains("anagrams for word"));
    }

    @Test
    void testAllResponseMessagesEndWithASentinel() {
        QueryProtocol qp = new QueryProtocol("client-id-1");
        String menuResponse = qp.deriveMenuResponse(null);
        String addWordSelected = qp.deriveMenuResponse("a");
        String wordAddFail = qp.deriveAddWordResponse("word", false);
        String wordAddSuccess = qp.deriveAddWordResponse("word", true);
        String removeWordSelected = qp.deriveMenuResponse("d");
        String wordRemoveFail = qp.deriveRemoveWordResponse("word", false);
        String wordRemoveSuccess = qp.deriveRemoveWordResponse("word", true);
        String findAnagramSelected = qp.deriveMenuResponse("p");
        String anagramResponse = qp.deriveFindAnagramResponse("word", Arrays.asList("rowd"));
        String invalidResponse = qp.deriveMenuResponse("null");

        assertTrue(menuResponse.endsWith(MSG_SENTINEL));
        assertTrue(addWordSelected.endsWith(MSG_SENTINEL));
        assertTrue(wordAddFail.endsWith(MSG_SENTINEL));
        assertTrue(wordAddSuccess.endsWith(MSG_SENTINEL));
        assertTrue(removeWordSelected.endsWith(MSG_SENTINEL));
        assertTrue(wordRemoveFail.endsWith(MSG_SENTINEL));
        assertTrue(wordRemoveSuccess.endsWith(MSG_SENTINEL));
        assertTrue(findAnagramSelected.endsWith(MSG_SENTINEL));
        assertTrue(anagramResponse.endsWith(MSG_SENTINEL));
        assertTrue(invalidResponse.endsWith(MSG_SENTINEL));

    }
}