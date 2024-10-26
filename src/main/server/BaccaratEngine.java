package main.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

public class BaccaratEngine implements Runnable {
    private final Socket sock;
    private List<String> cards;
    private volatile List<String> gameHistory = new ArrayList<>();

    public BaccaratEngine(Socket s) throws IOException {
        this.sock = s;
        this.cards = loadCards();
    }

    @Override
    public void run() {
        try {

            InputStream is = sock.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            DataInputStream dis = new DataInputStream(is);

            OutputStream os = sock.getOutputStream();
            Writer writer = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(writer);
            DataOutputStream DOS = new DataOutputStream(os);

            String inputLine;
            // BigInteger betAmount = BigInteger.ZERO;
            // BigInteger balance = BigInteger.ZERO;
            String betAmount = "";
            String balance = "";
            UserWriter user = null;
            boolean playing = false;
            String userFilePath = "/Users/brandonpereira/Code/day08/src/main/server/UserDataBase";

            while ((inputLine = br.readLine()) != null) {
                String[] commandParts = inputLine.split(" ");
                String command = commandParts[0];
                String username = "";
                switch (command) {
                    case "login":
                        System.out.println(inputLine);
                        username = commandParts[1];
                        balance = commandParts[2];
                        user = new UserWriter(username, balance);
                        user.loadUser(userFilePath);
                        user.saveUser(userFilePath);
                        bw.write("User " + username + " logged in with balance: " + balance + "\n");
                        bw.flush();
                        break;
                    case "bet":
                        betAmount = commandParts[1];
                        if (Long.parseLong(betAmount) < Long.parseLong(balance)) {
                            bw.write("Bet of " + betAmount + " placed.\n");
                            bw.flush();
                            System.out.println("Bet placed message flushed to client.");
                        } else {
                            bw.write("Not enough $ in balance to place a bet");
                            bw.flush();
                        }
                        break;
                    case "deal":
                        String side = commandParts[1];
                        String result = dealCards(side);
                        System.out.println(result);
                        if (result.contains("wins")) {
                            if ((side.equals("B") && result.contains("Banker wins"))
                                    || (side.equals("P") && result.contains("Player wins"))) {
                                String newBalance = Long.toString(Long.parseLong(balance) + Long.parseLong(betAmount));
                                System.out.println(newBalance);
                                balance = newBalance;
                                user.setBalance(balance);
                                user.saveUser(userFilePath);
                                bw.write("Bet won. Balance updated: " + newBalance + "\n");
                                bw.flush();
                                System.out.println("Bet won message flushed to client.");
                            } else {
                                String newBalance = Long.toString(Long.parseLong(balance) - Long.parseLong(betAmount));
                                System.out.println(newBalance);
                                balance = newBalance;
                                user.setBalance(balance);
                                bw.write("Bet lost. Balance remaining: " + newBalance + "\n");
                                user.saveUser(userFilePath);
                                bw.flush();
                                System.out.println("Bet lost message flushed to client.");
                            }
                        } else {
                            bw.write("It's a draw. Bet refunded.\n");
                            bw.flush();
                            System.out.println(balance);
                            System.out.println("Bet refunded message flushed to client.");
                        }

                        break;
                    case "exit":
                        playing = true;
                        System.exit(1);
                        sock.close();
                        break;
                    default:
                        bw.write("Unknown command: " + command + "\n");
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> loadCards() throws IOException {
        FileReader reader = new FileReader("/Users/brandonpereira/Code/day08/src/main/server/CardDatabase/cards.db");
        BufferedReader cardReader = new BufferedReader(reader);
        cards = new ArrayList<>();
        String line = "x";
        while ((line = cardReader.readLine()) != null) {
            cards.add(line);
        }
        return cards;
    }

    private String dealCards(String side) throws IOException {

        if (cards.size() < 4) {
            return "Not Enough Cards To Deal";
        }

        int playerSum = 0;
        int bankerSum = 0;
        List<String> playerCards = new ArrayList<>();
        List<String> bankerCards = new ArrayList<>();

        // Draw initial 2 cards for Player and Banker
        for (int i = 0; i < 2; i++) {
            String playerCard = cards.remove(0);
            playerCards.add(playerCard);
            playerSum += getCardValue(playerCard);

            String bankerCard = cards.remove(0);
            bankerCards.add(bankerCard);
            bankerSum += getCardValue(bankerCard);
        }

        if (playerSum <= 15) {
            String playerCard = cards.remove(0);
            playerCards.add(playerCard);
            playerSum += getCardValue(playerCard);
        }

        if (bankerSum <= 15) {
            String bankerCard = cards.remove(0);
            bankerCards.add(bankerCard);
            bankerSum += getCardValue(bankerCard);
        }

        String result;
        System.out.println("playerSum: " + playerSum);
        System.out.println("bankerSum: " + bankerSum);

        if (playerSum >= 10 && playerSum < 20) {
            playerSum -= 10;
        }

        if (playerSum >= 20 && playerSum<30) {
            playerSum -= 20;
        }

        if (playerSum >= 30) {
            playerSum -= 30;
        }

        if (bankerSum >= 10 && bankerSum < 20) {
            bankerSum -= 10;
        }

        if (bankerSum >= 20 && bankerSum <30) {
            bankerSum -= 20;
        }

        if (bankerSum >= 30) {
            bankerSum -= 30;
        }

        if (playerSum > bankerSum) {
            result = "Player wins with " + playerSum + " points.";
        } else if (bankerSum > playerSum) {
            result = "Banker wins with " + bankerSum + " points.";
        } else {
            result = "Draw";
        }

        synchronized (gameHistory){
            if (result.contains("Banker wins")) {
                gameHistory.add("B");
            } if(result.contains("Player wins")) {
                gameHistory.add("P");
            }else if(result.contains("Draw")){
                gameHistory.add("D");
            }

            // Write to history if game count reaches 6
            if (gameHistory.size() == 6) {
                writeGameHistory(new ArrayList<>(gameHistory));
                gameHistory.clear();
            }
        }

        // Save the updated cards list back to "cards.db"
        saveCards();

        // Construct response string
        String playerCardsString = "P|" + String.join("|", playerCards);
        String bankerCardsString = "B|" + String.join("|", bankerCards);

        return playerCardsString + "," + bankerCardsString + " - " + result;

    }

    private int getCardValue(String card) {
        int value = Integer.parseInt(card.split(" ")[1]);
        return value;
    }

    private synchronized void saveCards() {
        // Save the shuffled cards to a data file named "cards.db"
        try (FileWriter writer = new FileWriter(
                "/Users/brandonpereira/Code/day08/src/main/server/CardDatabase/cards.db")) {
            for (String card : cards) {
                writer.write(card + "\n");
            }
            System.out.println("Shuffled cards saved to cards.db");
        } catch (IOException e) {
            System.out.println("Error writing to cards.db: " + e.getMessage());
        }
    }

    private static synchronized void writeGameHistory(List<String> gameHistorySnapshot) throws IOException{
        FileWriter csvWriter = new FileWriter("/Users/brandonpereira/Code/day08/src/main/server/GameHistory/game _history.csv", true);
        synchronized(gameHistorySnapshot){
            if(!gameHistorySnapshot.isEmpty()){
                csvWriter.append(String.join(",", gameHistorySnapshot)).append("\n");
                csvWriter.flush();
                csvWriter.close();
            }
        }
    }
}
