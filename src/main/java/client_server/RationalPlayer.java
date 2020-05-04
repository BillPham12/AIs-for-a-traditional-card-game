// Java implementation for multithreaded chat client
// Save file as Client.java
package client_server;

import cryptography.EncryptSystem;
import robotStrategy.Rational;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;

public class RationalPlayer extends Client
{
    public RationalPlayer(String name) { super(name);
        ServerPort = 3456;
        hand = new ArrayList<String>();
        strategy = new Rational();
        secure_system = new EncryptSystem();
        schedule = new ArrayList<>();
        turns = new ArrayDeque<>();
        deque_for_next_game = new LinkedList<String>();
        new_round = false;
        upgrade_point = false;
    }

    public static void main(String[] args) throws IOException
    {
        // SETTING UP PORT, NAME AND PLAY STRATEGY;
        RationalPlayer player = new RationalPlayer("RATIONALITY");
        Scanner scn = new Scanner(System.in);
        secure_system = new EncryptSystem();
        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream in = new DataInputStream(s.getInputStream());
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    // read the message to deliver.
                    String msg = name + ":::" + scn.nextLine();
                    if(msg.length() > 0){
                        try {
                            // write on the output stream
                            System.out.println("Sending " + msg);
                            out.writeUTF(secure_system.encrypt(public_key,N,msg).toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // readMessage thread
        // Message 00 response to confirm that you have pass your turns
        // Message 01 response to confirm that your turn is valid.
        // Message x---03: x gives up his turn
        // Message x---66: x has played all his cards

        // To Send Message Encrypt<name:::sms>

        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    try {
                        // read the message server sent to this client
                        String msg = in.readUTF();
                        // receiving the public key and N
                        if(num_message == 0)
                        {
                            int x = msg.indexOf(';');
                            N = new BigInteger(msg.substring(0,x));
                            public_key = new BigInteger(msg.substring(x+1));
                            num_message++; }
                        // receive cards
                        else if (num_message == 1){
                            msg = secure_system.decrypt(public_key,N, new BigInteger(msg));
                            msg = msg.substring(3);
                            while(msg.contains(";") && msg.length() > 1){
                                int x = msg.indexOf(";");
                                hand.add(msg.substring(0,x));
                                msg = msg.substring(x+1);
                            }
                            Collections.sort(hand);
                           // System.out.println(hand);
                            num_message++;
                        }
                        // receive the list of players in play order
                        else if (num_message == 2){
                            while(msg.contains(";")){
                                int x = msg.indexOf(";");
                                player.schedule.add(msg.substring(0,x));
                                player.turns.add(msg.substring(0,x));
                                msg = msg.substring(x+1);
                                player.number_of_player++;
                            }
                            if(player.turns.peek().equals(name)){
                                playNewTurn(out,player);
                                player.turns.addLast(player.turns.poll());
                            }
                            num_message++;
                        }
                        else if (player.new_round){
                            player.schedule.clear(); player.turns.clear();
                            while(msg.contains(";")) {
                                int x = msg.indexOf(";");
                                player.schedule.add(msg.substring(0, x));
                                player.turns.add(msg.substring(0, x));
                                msg = msg.substring(x + 1);
                                player.number_of_player++;
                            }
                            if(player.turns.peek().equals(name)){
                                playNewTurn(out,player);
                                player.turns.addLast(player.turns.poll());
                            }
                            player.new_round = false;
                        }
                        // because this is robot then we don't need to anything
                        else if (player.upgrade_point){
                            player.new_round = true;
                            player.upgrade_point = false;
                        }
                        else
                        {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Collections.sort(hand);
                            String message = secure_system.decrypt(public_key,N,new BigInteger(msg));
                            int index = message.indexOf(":::");
                            String enemy = message.substring(0,index);
                            String cards = message.substring(index+3);
                            //("Message received " + message);
                          //  System.out.println(enemy + "----- Next Round is " + player.turns);
                            boolean first_turn = false;
                            // if some one pass remove them form the current schedule
                            if(!enemy.equals("SERVER") && cards.equals("03;")){
                                // remove the current player
                                player.turns.remove(enemy);
                                if(player.turns.size() == 0) reNewTurn(player);

                                // play new turn if the current round is only one left
                                if(player.turns.size() == 1){
                                    if(player.turns.peek().equals(name) &&  !player.finish){
                                        playNewTurn(out,player);
                                        // check if the it plays out of cards
                                        if(player.getHand().size() == 0){
                                            player.finish= true;
                                            deque_for_next_game.addLast(name);
                                        }
                                        // re new the turns; because the robot play, therefore, put the robot to the last of the deque
                                        reNewTurnAgressiveTakingTheLead(player);
                                    }
                                    // re new the turns in case the current play is not the robot.
                                    else reNewTurn(player);
                                }
                                // if this is the turn, then play || otherwise do nothing
                                else if(player.turns.peek().equals(name) && !player.finish){
                                    String meld = convertToMessage(player.play());
                                    // check if the it plays out of cards
                                    if(player.getHand().size() == 0){
                                        player.finish= true;
                                        deque_for_next_game.addLast(name);
                                        msg = name + ":::" + meld;
                                        out.writeUTF(secure_system.encrypt(public_key,N,msg).toString());

                                     //   System.out.println(name + "  play this " + meld);
                                      //  System.out.println("hand left " +player.getHand());
                                        continue;

                                    }

                                    if (meld.equals(" ")){
                                        player.turns.remove(name);
                                        // re new the turns in case the current play is not the robot.
                                        if(player.turns.size() == 1)
                                            reNewTurn(player);

                                    }
                                    else player.turns.addLast(player.turns.poll());

                                    msg = name + ":::" + meld;
                                    out.writeUTF(secure_system.encrypt(public_key,N,msg).toString());
                                  //  System.out.println(name + "  play this " + meld);
                                 //   System.out.println("hand left " +player.getHand());

                                }

                            }
                            // if the server deal for new round then add cards. not do anything yet
                            else if (enemy.equals("SERVER") && cards.length() == 3*13){
                                hand.clear();
                                for(Map.Entry x : list_of_the_player.entrySet()){
                                    list_of_the_player.get(x.getKey()).clear();
                                }
                                while(cards.contains(";") && cards.length() > 1){
                                    int x = cards.indexOf(";");
                                    hand.add(cards.substring(0,x));
                                    cards = cards.substring(x+1);
                                }
                                player.finish = false;
                                player.upgrade_point = true;
                            }
                            // enemy movements && watching
                            else if (!enemy.equals("SERVER")){
                                // update the current meld
                                cards = convertCardsToString(cards);
                                // update knowledge
                                player.updateKnowledge(enemy,cards);
                                player.setCurrentMeld(cards);
                                // update turns
                                if(player.getListOfThePlayer().get(enemy).size() >= 13){
                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    reNewRound(player,enemy);
                                    player.schedule.remove(enemy);
                                    player.setCurrentMeld(" ");
                                    if(player.schedule.size() == 1) continue;
                                }
                                else player.turns.addLast(player.turns.poll());


                                // if this is our turn then play
                                if(player.turns.peek().equals(name) && !player.finish){
                                    String meld = convertToMessage(player.play());
                                    // check if the it plays out of cards
                                    if(player.getHand().size() == 0){
                                        player.finish= true;
                                        deque_for_next_game.addLast(name);
                                    }

                                    if (meld.equals(" ")){
                                        player.turns.remove(name);
                                        // renew the turns
                                        if(player.turns.size() == 1)
                                            reNewTurn(player);
                                    }
                                    else player.turns.addLast(player.turns.poll());
                                    msg = name + ":::" + meld;
                                    out.writeUTF(secure_system.encrypt(public_key,N,msg).toString());

                                  //  System.out.println(name + "  play this " + meld);
                                  //  System.out.println("hand left " +player.getHand());
                                }
                            }
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }

    private static String convertToMessage(String[] play) {
        String s = "";
        if (play == null) return " ";

        if(play[0].equals("00")) return " ";
        for(String card: play) {s += card; s+= ";";}
        return s;
    }

    private static String convertCardsToString(String msg){
        String out ="";
        while(msg.contains(";") && msg.length() > 1){
            int x = msg.indexOf(";");
            out += (msg.substring(0,x));
            msg = msg.substring(x+1);
        }
        return out;
    }
    private static void check_for_Winner(){

    }
    private static void playNewTurn(DataOutputStream out,RationalPlayer player) throws IOException {
        // reset the current meld;
        player.setCurrentMeld(" ");
        String meld = convertToMessage(player.play());
        String msg = name + ":::" + meld;

        out.writeUTF(secure_system.encrypt(public_key,N,msg).toString());
        if(player.getHand().size() == 0){
            player.finish= true;
        }
       // System.out.println(name + "  play this " + meld);
       // System.out.println("hand left " +player.getHand());
    }
    // renew the turn if the robot is not the controller
    private static void reNewTurn(RationalPlayer player){
        ArrayList<String> sample = new ArrayList<>();
        sample.addAll(player.schedule);
        int index_current_player = sample.indexOf(player.turns.poll());
        if(index_current_player == -1) index_current_player = 0;

        player.turns.clear();
        player.turns.addAll(sample.subList(index_current_player,sample.size()));
        player.turns.addAll(sample.subList(0,index_current_player));
      //  System.out.println("Renew Turn is Called " + player.turns);
    }
    private static void reNewRound (RationalPlayer player, String enemy){
        ArrayList<String> sample = new ArrayList<>();
        sample.addAll(player.schedule);
        int index_current_player = sample.indexOf(enemy);
        if(index_current_player == -1) index_current_player = 0;

        player.turns.clear();
        player.turns.addAll(sample.subList(index_current_player+1,sample.size()));
        player.turns.addAll(sample.subList(0,index_current_player));
       // System.out.println("Renew Turn is Called " + player.turns);
    }

    private static void reNewTurnAgressiveTakingTheLead(RationalPlayer player){
        ArrayList<String> sample = new ArrayList<>();
        sample.addAll(player.schedule);
        int index_current_player = sample.indexOf(player.turns.peek());
        if (index_current_player == player.number_of_player-1) index_current_player = 0;
        else index_current_player ++;
        player.turns.clear();
        player.turns.addAll(sample.subList(index_current_player,sample.size()));
        player.turns.addAll(sample.subList(0,index_current_player));
    }

}
