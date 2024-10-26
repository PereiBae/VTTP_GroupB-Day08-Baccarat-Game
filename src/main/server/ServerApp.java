package main.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.server.BaccaratEngine;

public class ServerApp {
    public static void main(String[] args) throws IOException {

        ExecutorService thrPool = Executors.newFixedThreadPool(4);

        int port = 3000;
        int numDecks = 1;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            numDecks = Integer.parseInt(args[1]);
        } else {
            System.err.println("Invalid number of arguments expected");
            System.exit(0);
        }
        ServerSocket server = new ServerSocket(port);

        System.out.println(">>> Waiting for connection...");

        // Write the cards into cards.db file and shuffle the cards

        String cardPath = "/Users/brandonpereira/Code/day08/src/main/server/CardDatabase";
        File cardDatabase = new File(cardPath, "cards.db");
        FileWriter writer = new FileWriter(cardDatabase);
        BufferedWriter bw = new BufferedWriter(writer);

        // Generate and shuffle the specified number of decks
        for (int i = 0; i < numDecks; i++) {
            Decks deck = new Decks(); // Create a deck
            deck.shuffle(); // Shuffle the deck

            // Write each card of the deck to the file
            List<Cards> cards = deck.getCards();
            for (Cards card : cards) {
                bw.write(card.toString() + "\n");
            }
        }
        bw.flush();
        bw.close();
        writer.close();

        resetGameHistory();

        while (true) {

            Socket sock = server.accept();

            System.out.println(">>> Got a new connection!");

            new Thread(new BaccaratEngine(sock)).start();

        }

    }

    private static void resetGameHistory() throws IOException{
        FileWriter fileWriter = new FileWriter("/Users/brandonpereira/Code/day08/src/main/server/GameHistory/game _history.csv", false);
        fileWriter.write("0");
        System.out.println("Game History reset.");
    }

}
