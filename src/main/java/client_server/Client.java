package client_server;

import java.math.BigInteger;
import java.util.*;

import game.Meld;
import cryptography.EncryptSystem;
import robotStrategy.RobotStrategy;
public class Client {

    protected static Deque deque_for_next_game;

    protected static int ServerPort = 0;
    // hand of the player
    protected static ArrayList<String> hand;
    // player name
    protected static String name;
    // cards have been played
    protected static HashMap<String,ArrayList<String>> list_of_the_player;
    protected static HashMap<String,Integer> points;
    // data
    protected static HashMap<String,ArrayList<String>> seq_data;
    protected static HashMap<String,ArrayList<String>> pair_data;
    // winning strategy

    // robot strategy
    protected static RobotStrategy strategy;
    // current meld
    protected static Meld current_meld;
    // public key
    protected static BigInteger public_key;
    protected static BigInteger N;
    //secure systems
    protected static EncryptSystem secure_system = new EncryptSystem();
    //
    protected static int num_message = 0;
    // check for the current turn
    protected int point = 0;
    protected boolean newRound = false;
    protected int number_of_player = 0;
    protected int my_turn;
    protected boolean finish = false;
    protected ArrayList<String> schedule;
    protected Deque<String> turns;
    protected boolean upgrade_point;
    protected boolean new_round;

    // constructor for player;
    public Client(String name){ Client.name = name; hand = new ArrayList<>();current_meld = new Meld();
    list_of_the_player = new HashMap<String,ArrayList<String>>();
    schedule = new ArrayList<>();
    points = new HashMap<String, Integer>();
    }
    // get Player name;
    public String getName(){return name;}
    // getPlayer hand;
    public ArrayList<String> getHand(){return hand;}
    public HashMap<String,ArrayList<String>> getListOfThePlayer(){ return list_of_the_player;}

    // change player name
    public void changeName(String new_name){
        name = new_name;}
    public HashMap<String,Integer> getPoints(){return points;}
     public void setCurrentMeld(String meld){
        if(meld.equals(" ")) current_meld = new Meld();
        else{
            String[] hello;
            if(meld.length() == 2) hello = new String[1];
            else hello = new String[meld.length()/2];
            int i = 0;
            while(!meld.equals("")){
                hello[i] = meld.substring(0,2);
                meld = meld.substring(2);
                i++;
            }
            if(current_meld == null) current_meld = new Meld(hello);
            else current_meld.setValue(hello);}
    }
    // clear hand
    public void clearHand(){hand.clear();}
    //increase point
    public void increasePoint(){point++;}
    //get point
    public int getPoint(){return point;}
    //
    public int getNumber_of_player(){return schedule.size();}

    // sort player hand
    protected void sortBySuit(boolean x){
        if (x) Collections.sort(hand,new Client.sort_Suit());
        else Collections.sort(hand, new Client.sort_Value());}

    public void setHand(String[] input) {hand.clear(); Collections.addAll(hand, input);}


    public String[] play() { return strategy.play(this);}

    public Meld getCurrentMeld() { return current_meld;}

    public void setListOfThePlayer(String enemy, String s) {
        if(!schedule.contains(enemy))
            schedule.add(enemy);
        ArrayList sample = new ArrayList();
        if(!list_of_the_player.containsKey(enemy)){
            StringTokenizer str = new StringTokenizer(s,";");
            while(str.hasMoreTokens()){
                sample.add(str.nextToken());
            }
            list_of_the_player.put(enemy,sample);
        }
        else{
            StringTokenizer str = new StringTokenizer(s,";");
            while(str.hasMoreTokens()){
                list_of_the_player.get(enemy).add(str.nextToken());
            }
        }

    }

    public void updateKnowledge(String enemy,String meld) {
        if(meld.equals("9999")) {list_of_the_player.put(enemy,new ArrayList<String>());}
        else if(meld.equals(" ")) current_meld = new Meld();
        else {
            int i = 0;
            while (!meld.equals("")) {
                if (list_of_the_player.get(enemy) != null)
                    list_of_the_player.get(enemy).add(meld.substring(0, 2));
                else {
                    list_of_the_player.put(enemy, new ArrayList<String>());
                    list_of_the_player.get(enemy).add(meld.substring(0, 2));
                }
                meld = meld.substring(2);
                i++;
            }
        }
    }
    public void renewKnowledge(){
        for (Map.Entry<String, ArrayList<String>> entry : list_of_the_player.entrySet())
            entry.getValue().clear();
    }

    public void setRounds(String[] list) {
        for (String str: list) {
            if(!schedule.contains(str))
                schedule.add(str);
        }
    }

    public String getSpecialEnemy() {
        int index = schedule.indexOf(name);
        if(index == 0)
            return schedule.get(schedule.size()-1);
        else
            return schedule.get(index-1);
    }

    public void setNextTurn(String patience) {
        turns.clear(); turns.add(patience);
    }

    public void clearList() {
        list_of_the_player = new HashMap<String,ArrayList<String>>();
    }

    public Deque<String> getTurns() {
        return turns;}

    // sort by suit class
    protected class sort_Suit implements Comparator<String>
    { public int compare(String a, String b) { return a.compareTo(b); }}
    // sort by value class
    protected class sort_Value implements Comparator<String>
    { public int compare(String a, String b) { return (int) a.charAt(0) - (int) b.charAt(0); }}

    public String toString(){
        String str = "";
        str += "Client Name " + name;
        return str;
    }
}
