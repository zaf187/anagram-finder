package com.wzk;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Joiner;
import com.wzk.entity.Dictionary;
import com.wzk.network.SocketClient;
import com.wzk.network.SocketServer;
import com.wzk.service.AnagramFinder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnagramService {
    public static void main(String[] args) {
        String opMode = "local";
        if (args.length > 0) {
            opMode = args[0];
        }
        log.info("Starting in mode: {}", opMode);

        InputStream fileStream = AnagramService.class.getClassLoader().getResourceAsStream("words.txt");

        if ("server".equalsIgnoreCase(opMode)) {
            Dictionary dictionary = new Dictionary(fileStream, true);
            AnagramFinder anagramFinder = new AnagramFinder(dictionary);
            SocketServer server = new SocketServer(anagramFinder, dictionary, 5555);
            server.startServer();
        } else if ("client".equalsIgnoreCase(opMode)) {
            SocketClient client = new SocketClient("localhost", 5555);
            client.startClient();
        } else {
            Dictionary dictionary = new Dictionary(fileStream, true);
            AnagramFinder anagramFinder = new AnagramFinder(dictionary);
            log.info("Ready to accept words, Enter a word:");
            Scanner inputReader = new Scanner(System.in);
            while(inputReader.hasNextLine()) {
                List<String> anagrams = anagramFinder.findAnagrams(inputReader.nextLine());
                log.info("Anagrams: {}", Joiner.on(",").join(anagrams));
                log.info("Try another? :");
            }
        }
    }
}
