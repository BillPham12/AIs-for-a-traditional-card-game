package game;

import java.util.*;
import client_server.Client;
public class GameModel {
    private Deck deck;
    // game Rule
    private Meld meld;
    private boolean onGame = false;
    private List<Client> players;
    public GameModel(){deck = new Deck(); meld = new Meld(); players = new ArrayList<>();onGame = true;}
    //set current meld
    public void setCurrentMeld(String[] meld){this.meld.setValue(meld);}
    // get current meld
    public String[] getCurrent_meld() {return meld.getValue();}
    public Meld getMeld(){return this.meld.getMeld();}
    // add player
    public boolean addPlayer(Client p) {
        return players.add(p);}
    // check for valid meld
    public boolean validMeld(String[] meld) {return this.meld.checkMeld(meld);}
    // size of deck
    public int sizeOfDeck(){return deck.getSize();}

    // Dealing card
    public void reStart(){ meld = new Meld(); onGame = true; deck = new Deck();}
    public boolean isOnGame(){return onGame;}

    public Card[] deal(){
        Card[] cards = new Card[13];
        int i =0;
        while(i < 13){
            cards[i] = deck.pop();
            i++;
        }
        return cards;
    }
    // Decide who will go first
    // Decide their schedule by dealing a card for each player. Then compare them to each other based on the value of the
    // card, if there are two card having the same value, the turn will be decided based on the suit.
    public void firstRound(){
        Deck x = new Deck();
        Map<Card, Client> map = new HashMap<>();
        for(Client p : players)
            map.put(x.pop(),p);
        map.entrySet().stream().sorted(new sortMap());
        players.clear();
        for(Map.Entry<Card,Client> a : map.entrySet())
            players.add(a.getValue());
    }

    public List<Client> getPlayer() {
        return players; }

    public Card[] dealSpecical() {
        return deck.specialDeal();
    }

    // compare class to support first round function
    private class sortMap implements Comparator<Map.Entry<Card,Client>>
    { public int compare(Map.Entry<Card,Client> a, Map.Entry<Card,Client> b) {
        if(a.getKey().getValue() != b.getKey().getValue()) return a.getKey().getValue() - b.getKey().getValue();
        return a.getKey().getSuit().compareTo(b.getKey().getSuit());}}
}
