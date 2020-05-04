package game;

import java.security.SecureRandom;
import java.util.*;
public class Deck {
    private ArrayList<Card> deck_of_cards;
    public Deck(){ deck_of_cards = new ArrayList<Card>();setUp(); shuffle();}

    // set up cards and
    private void setUp() {
        deck_of_cards.clear();
        String[] chars = {"H","C","D","S"};
        for(int i =0; i < 4; i++){
            for(int y = 2; y < 15; y++){
                Card a = new Card(chars[i],y);
                deck_of_cards.add(a);
            }
        }
    }

    // shuffle cards
    private void shuffle(){
        // secure random
        SecureRandom rand = new SecureRandom();
        for(int i =0; i < deck_of_cards.size(); i++){
            Card x = deck_of_cards.get(i);
            int random = rand.nextInt(deck_of_cards.size());
            // swapping positions
            deck_of_cards.set(i,deck_of_cards.get(random));
            deck_of_cards.set(random,x);
        }
    }
    // pop out
    public Card pop() {
        return deck_of_cards.remove(0);
    }
    public String toString(){
        String str = "";
        for (int i =0; i < deck_of_cards.size();i++)
            str += deck_of_cards.get(i);
        return str;
    }
    public int getSize() { return deck_of_cards.size();}

    public Card[] specialDeal(){
        Card[] output = new Card[13];
        if (deck_of_cards == null || deck_of_cards.size() == 0)
            return null;
        else{
            setUp();
            List<Card> test = new ArrayList<>();
            // Bias
            // 2 2
            test.add(deck_of_cards.get(0)); test.add(deck_of_cards.get(13));
            // 3 3 4 6
            test.add(deck_of_cards.get(1));test.add(deck_of_cards.get(14));test.add(deck_of_cards.get(2+26));
            test.add(deck_of_cards.get(4+39));
            //  8 9 X J Q
            test.add(deck_of_cards.get(19));test.add(deck_of_cards.get(33));test.add(deck_of_cards.get(19+2));
            test.add(deck_of_cards.get(9));test.add(deck_of_cards.get(10+13));
            // 2 K A
            test.add(deck_of_cards.get(11 + 13));test.add(deck_of_cards.get(11+26));

            int i =0;
            System.out.println(test);
            for (Card card: test) {
                deck_of_cards.remove(card);
                output[i++] = card;
            }
            shuffle();
            return output;
        }
    }
    private Card getCard(String str){
        for (Card card: deck_of_cards)
            if (card.toString().contains(str))
                return card;
        return null;
    }
}
