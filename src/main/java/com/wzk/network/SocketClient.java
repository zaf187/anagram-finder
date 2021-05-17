package com.wzk.network;

import static com.wzk.network.QueryProtocol.EXIT_PHRASE;
import static com.wzk.network.QueryProtocol.MSG_SENTINEL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a client communicating with a server over a socket, the server should be the one implemented
 * in {@link SocketServer}
 */
@Slf4j
public class SocketClient extends Thread {
    private final String server;
    private final int serverPort;

    public SocketClient(String server, int serverPort) {
        this.server = server;
        this.serverPort = serverPort;
    }

    public void startClient() {
        start();
    }

    /**
     * Opens input and output to the server using the established socket. Reads input from the user and writes it to
     * the server, then it waits and reads response messages from the server before prompting the user for
     * further input. Upon entering the exit phrase, this program exits.
     */
    @Override
    public void run() {
        PrintWriter output = null;
        BufferedReader input = null;
        BufferedReader clientTerminal = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = null;
        try{
            clientSocket = new Socket(server, serverPort);
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String clientInput;
            log.info("Beginning communication with server");
            readServerResponse(input);
            while (!EXIT_PHRASE.equalsIgnoreCase(clientInput = clientTerminal.readLine())) {
                output.println(clientInput);
                readServerResponse(input);
            }
            clientSocket.close();
            log.info("Ending communication with server");
        } catch (IOException ex) {
            log.error("Failed to establish connectivity with server {}:{}", server, serverPort, ex);
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    log.error("Failed to close socket", ex);
                }
            }
        }
    }

    /**
     * Continues to read a response from the server and print to the standard out
     * until it receives a message terminator.
     * @param input the input stream from the server.
     * @throws IOException when there is a problem with the input stream of the server.
     */
    private void readServerResponse(BufferedReader input) throws IOException {
        String serverResponse;
        int lineCounter = 0;
        while(!MSG_SENTINEL.equals(serverResponse = input.readLine())) {
            System.out.println(serverResponse);
            lineCounter++;
        }
        log.info("Received {} lines from the server", lineCounter);
    }
}
