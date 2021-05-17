package com.wzk.network;

import java.util.List;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles the communication protocol between the client and server to facilitate more complex interactions
 */
@Slf4j
class QueryProtocol {
    public static final String EXIT_PHRASE = "quit program";
    public static final String MSG_SENTINEL = "^";
    public static final String RETURN_TO_MENU = "<<";

    public static final int MENU = 0;
    public static final int ADD_WORD = 1;
    public static final int REMOVE_WORD = 2;
    public static final int FIND_ANAGRAM = 3;

    private int currentState = MENU;
    private final String clientId;

    public QueryProtocol(String clientId) {
        this.clientId = clientId;
    }

    public int getCurrentState() {
        return currentState;
    }

    /**
     * Prints the menu if the request is null, if the request is any of the provided menu options, it changes
     * the protocol state to that menu option and provides an adequate response for the user. If the user enters
     * an invalid option returns an informative message.
     * @param request the users input on the client side.
     * @return a response showing the menu or prompting the user for further input.
     */
    public String deriveMenuResponse(String request) {
        StringBuilder responseBuilder = new StringBuilder();
        if (currentState == MENU) {
            if (request == null) {
                responseBuilder.append("Welcome to the Anagram Service.\n");
                responseBuilder.append("Select an Option:\n");
                responseBuilder.append("[A] Add a word\n");
                responseBuilder.append("[D] Delete a word\n");
                responseBuilder.append("[P] Print Anagrams\n");
                responseBuilder.append("Type "+EXIT_PHRASE+" to quit\n");
                log.debug("Printing menu for client {}", clientId);
            } else if ("a".equalsIgnoreCase(request)) {
                responseBuilder.append("Enter a word to ADD to the dictionary:\n");
                currentState = ADD_WORD;
                log.debug("Client {} selected add word", clientId);
            } else if ("d".equalsIgnoreCase(request)) {
                responseBuilder.append("Enter a word to DELETE from the dictionary:\n");
                currentState = REMOVE_WORD;
                log.debug("Client {} selected remove word", clientId);
            } else if ("p".equalsIgnoreCase(request)) {
                responseBuilder.append("Enter a word to find its anagrams:\n");
                currentState = FIND_ANAGRAM;
                log.debug("Client {} selected find anagram", clientId);
            } else {
                responseBuilder.append("Invalid request ["+request+"]\n");
                responseBuilder.append("Expecting:\n");
                responseBuilder.append("Menu Option [A],[D] or [P]\n");
                log.debug("Client {} entered invalid request", clientId);
            }
        }
        responseBuilder.append(MSG_SENTINEL);
        return responseBuilder.toString();
    }

    /**
     * Prints a response to the user for when they have tried to add a word.
     * @param word word being added.
     * @param didSucceed whether or not the addition succeeded.
     * @return a response to return to the user.
     */
    public String deriveAddWordResponse(String word, boolean didSucceed) {
        StringBuilder responseBuilder = new StringBuilder();
        if (didSucceed) {
            responseBuilder.append("Word ["+word+"] was added.\n");
            responseBuilder.append("Add another? or type '<<' to go back.\n");
            log.debug("Add word succeeded for client {}", clientId);
        } else {
            responseBuilder.append("Failed to add Word ["+word+"]. Try another word?\n");
            log.debug("Add word failed for client {}", clientId);
        }
        responseBuilder.append(MSG_SENTINEL);
        return responseBuilder.toString();
    }

    /**
     * Prints a response to the user for when they have tried to remove a word.
     * @param word word being removed.
     * @param didSucceed whether or not the removal succeeded.
     * @return a response to return to the user.
     */
    public String deriveRemoveWordResponse(String word, boolean didSucceed) {
        StringBuilder responseBuilder = new StringBuilder();
        if (didSucceed) {
            responseBuilder.append("Word ["+word+"] was removed.\n");
            responseBuilder.append("Remove another? or type '<<' to go back.\n");
            log.debug("Remove word succeeded for client {}", clientId);
        } else {
            responseBuilder.append("Failed to remove Word ["+word+"]. Try another word?\n");
            log.debug("Remove word failed for client {}", clientId);
        }
        responseBuilder.append(MSG_SENTINEL);
        return responseBuilder.toString();
    }

    /**
     * Prints the anagrams found for a word entered by the user.
     * @param word word to find anagrams of.
     * @param anagrams the list of anagrams found for the word.
     * @return a formatted response displaying the anagrams to the user.
     */
    public String deriveFindAnagramResponse(String word, List<String> anagrams) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Found "+anagrams.size()+" anagrams for word "+word+"\n");
        responseBuilder.append(Joiner.on(",").join(anagrams)+"\n");
        responseBuilder.append("Find another? or type '<<' to go back.\n");
        responseBuilder.append(MSG_SENTINEL);
        return responseBuilder.toString();
    }

    /**
     * Resets the users interactions back to the top menu.
     * @return Prints the initial menu to the user.
     */
    public String returnToMenu() {
        currentState = MENU;
        log.debug("Returning client {} to top menu", clientId);
        return deriveMenuResponse(null);
    }
}
