package main.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Decks {

    private int currentCard;
    private List<Cards> card;

    public Decks(){
        card = new ArrayList<>();

        // Loop through ranks and suits, but only generate numericCode and value
        for (int rankIndex = 0; rankIndex < 13; rankIndex++) {
            for (int suitIndex = 0; suitIndex < 4; suitIndex++) {
                // Calculate the numeric code (rankIndex + 1 + suitIndex * 0.1)
                double numericCode = (rankIndex + 1) + (suitIndex + 1) * 0.1;

                // Assign value (Ace is 1, 10/Jack/Queen/King are 10, others are face value)
                int value;
                if (rankIndex == 0) {
                    value = 1; // Ace
                } else if (rankIndex >= 9) {
                    value = 10; // 10, Jack, Queen, King
                } else {
                    value = rankIndex + 1; // 2 to 9 are face value
                }

                // Add the card with its numericCode and value
                card.add(new Cards(numericCode, value));
            }
        }
    }

    // Shuffle method that ensures equal probability of each card ending up in any position in the array
    public void shuffle() {
        Collections.shuffle(card); // Shuffles the list of cards
    }

    public Cards drawCard() {
        if (!card.isEmpty()) {
            return card.remove(0); // Removes and returns the top card (index 0)
        } else {
            return null; // Return null if the deck is empty
        }
    }

    public List<Cards> getCards() {
        return card;
    }

}

