package com.wzk.network;

import static com.wzk.network.QueryProtocol.ADD_WORD;
import static com.wzk.network.QueryProtocol.REMOVE_WORD;
import static com.wzk.network.QueryProtocol.FIND_ANAGRAM;
import static com.wzk.network.QueryProtocol.MENU;
import static com.wzk.network.QueryProtocol.RETURN_TO_MENU;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.wzk.entity.Dictionary;
import com.wzk.service.AnagramFinder;

import lombok.extern.slf4j.Slf4j;

/**
 * Exposes the {@link AnagramFinder} and {@link Dictionary} over a socket to clients.
 * Using {@link SocketClient} a user can send requests to the server to find anagrams for a word or to add and remove
 * words from the in memory dictionary. Each client is allocated a new thread so the server is able to handle
 * requests from multiple clients.
 */
@Slf4j
public class SocketServer extends Thread {
    private ServerSocket serverSocket;
    private final AnagramFinder anagramFinder;
    private final Dictionary dictionary;
    private final int serverPort;

    public SocketServer(AnagramFinder anagramFinder, Dictionary dictionary, int serverPort) {
        this.anagramFinder = anagramFinder;
        this.dictionary = dictionary;
        this.serverPort = serverPort;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(this.serverPort);
            start();
        } catch (IOException ex) {
            log.error("Could not create server socket on port {}", serverPort, ex);
        }
    }

    /**
     * Waits for a client to connect, upon receiving a connection establishes input and output with the client
     * in a new thread. The thread continues to listen to the client, when an input is received, it uses the
     * {@link QueryProtocol} to look up the clients state and what the response should be. A user can choose to; find
     * anagrams, add words, remove words or quit.
     */
    @Override
    public void run() {
        while(true) {
            log.info("Waiting for a client ....");
            try{
                final Socket socket = serverSocket.accept();
                final String clientIpPort = socket.getInetAddress().getHostAddress()+":"+socket.getPort();

                log.info("Client connected, IP and port: {}", clientIpPort);
                new Thread(() ->{
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                        QueryProtocol qp = new QueryProtocol(clientIpPort);
                        String initialMsg = qp.deriveMenuResponse(null);
                        output.println(initialMsg);
                        String clientInput;
                        while ((clientInput = input.readLine()) != null) {
                            log.info("client {} says {}", clientIpPort, clientInput);
                            // by default the response should be failed since this would indicate the program
                            // was not able to derive the appropriate response to the request.
                            String response = "Failed to generate response for input: "+clientInput;
                            if (MENU == qp.getCurrentState()) {
                                response = qp.deriveMenuResponse(clientInput);
                            } else {
                                if ((qp.getCurrentState() == ADD_WORD || qp.getCurrentState() == REMOVE_WORD ||
                                    qp.getCurrentState() == FIND_ANAGRAM) && RETURN_TO_MENU.equals(clientInput)) {
                                    response = qp.returnToMenu();
                                } else {
                                    if (ADD_WORD == qp.getCurrentState()) {
                                        boolean didSucceed = dictionary.addWord(clientInput);
                                        response = qp.deriveAddWordResponse(clientInput, didSucceed);
                                    } else if (REMOVE_WORD == qp.getCurrentState()) {
                                        boolean didSucceed = dictionary.removeWord(clientInput);
                                        response = qp.deriveRemoveWordResponse(clientInput, didSucceed);
                                    } else if (FIND_ANAGRAM == qp.getCurrentState()) {
                                        List<String> anagrams = anagramFinder.findAnagrams(clientInput);
                                        response = qp.deriveFindAnagramResponse(clientInput, anagrams);
                                    }
                                }
                            }
                            output.println(response);
                        }
                        input.close();
                        output.close();
                        socket.close();
                    } catch (IOException ex) {
                        log.error("Connection with client failed, {}", clientIpPort, ex);
                    }
                }).start();
            } catch (IOException ex) {
                log.error("Unable to establish connection with client", ex);
            }
        }
    }
}
