// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java
package client_server;
import cryptography.KeyGenerator;
import cryptography.EncryptSystem;
import game.Card;
import game.Deck;
import game.GameModel;
import javafx.util.Pair;
import robotStrategy.support;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.net.*;
import  java.util.Scanner;

// Server class
public class Server
{
    private static MongoClient mongoClient;
    // Vector to store active clients
    private static List<ClientHandler> schedule;
    private static  support support;
    private static List<ClientHandler> original;
    private static String punished_player;
    private static String awarded_player;
    private static GameModel game;
    private static KeyGenerator keyGenerator;
    private static ServerSocket[] list_sS;
    private static Socket[] list_s;
    private static int attempt = 3;
    private static Deque<ClientHandler> deque_for_next_game;
    private static Deque<ClientHandler> turn_checker;
    private static List<String> toRecord = new ArrayList<>();
    private static int[] records = {0,0,0,0};
    private static int[] aggressive = {0,0,0,0},
            human = {0,0,0,0},
            rational = {0,0,0,0},
            p_rational = {0,0,0,0};
    private static int point = 1;
    private static int punishment = 0;
    // counter for clients
    static int num_clients = 0, limit = 10000;
    // check valid move

    public static void main(String[] args) throws IOException
    {
        // vector to hold players
        schedule = new LinkedList<>(); original = new LinkedList<>();

        turn_checker = new ArrayDeque<>();
        deque_for_next_game = new LinkedList<>();
        // set up the game model
        game = new GameModel();
        // server is listening on port 1234
        keyGenerator = new KeyGenerator();
        // get number of players
        String input = "";
        /*l: while(true){
            if (fool) {System.out.println("Please enter number of players: ");}
            else System.out.println("You enter wrong input!!! Please enter ONE single number less or equals to 4");
             input = new Scanner(System.in).nextLine();
            if(input.length() > 1) fool = false;
            else{
                if((int)input.charAt(0) > 49 && (int)input.charAt(0) <= 52) break l;
            }
        }*/
        System.out.println("----------------------CREATING THE SERVER--------------------");
        //num_clients = Integer.parseInt(input);

        // CREATING A LIST OF PORTS FOR SERVER SOCKETS
        ServerSocket ss = new ServerSocket(1234), ss2 = new ServerSocket(2345),
                ss3 = new ServerSocket(3456),ss4 = new ServerSocket(2222);
        list_sS = new ServerSocket[]{ss, ss2, ss3, ss4};

        // CREATE A LIST OF SOCKETS
        Socket s = null, s1 = null,s2= null,s3 = null;
        list_s = new Socket[]{s, s1, s2, s3};

        // client request
        num_clients = 4;
        // GENERATE ASYMMETRIC KEYS PAIR FOR CLIENTS.
        Pair<BigInteger, Pair<BigInteger, BigInteger>>[] list_keys = new Pair[num_clients];
        for(int i =0; i < num_clients; i++)
            list_keys[i]= keyGenerator.generate();

        //  RUN A FOR LOOP TO ACCEPT THE INCOMING REQUEST CLIENTS
        while (game.isOnGame())
        {
            for(int i =0; i < num_clients;i++){
                list_s[i] = list_sS[i].accept();
                System.out.println("New client request received : " + list_s[i]);
                // IN & OUT STREAMING
                DataInputStream in = new DataInputStream(list_s[i].getInputStream());
                DataOutputStream out = new DataOutputStream(list_s[i].getOutputStream());
                String n = "";
                if (i ==0 )  n = "AGGRESSION";
                else if (i == 1) n = "HUMAN"; // PATIENCE --> CORRECT ORDER
                else if (i == 2) n = "RATIONALITY";// RATIONALITY --> CORRECT ORDER
                else if (i == 3) n = "TERMINATOR";
                ClientHandler ch1 = new ClientHandler(list_s[i],n, in, out,list_keys[i].getValue().getValue(),list_keys[i].getKey());
                // SENDING PUBLIC KEY TO CLIENT
                ch1.out.writeUTF(list_keys[i].getKey() + ";" + list_keys[i].getValue().getKey());
                // CREATING A THREAD AND ADD IT INTO THE LIST
                Thread t = new Thread(ch1);
                schedule.add(ch1);
                t.start();
            }
            // sort for the first round; the one who has high ranking card will play first.
            firstRound();
            String turns = "";
            punished_player = ""; awarded_player ="";
            for (ClientHandler ch: schedule) turns += ch.name + " -->";
            System.out.println(turns);
            original.addAll(schedule);
            System.out.println("Dealing card!");
            for(ClientHandler mc: schedule){
                String sms = "::";
               /* if(mc.name.equals("RATIONALITY"))
                    sms += convert(game.dealSpecical());
                else*/
                sms += convert(game.deal());
                mc.out.writeUTF(mc.encrypt(sms));
            }
            // Send knowledge to clients, to let them know that how many people are playing the game
            // send the list of name to player
            String names = "";
            for (ClientHandler c: schedule) names +=c.name +";";
            for(ClientHandler mc: schedule)
                mc.out.writeUTF(names);
            turn_checker.addAll(schedule);
        }
    }
    public static class ClientHandler implements Runnable
    {
        private EncryptSystem secure_system = new EncryptSystem(); // ENCRYPT AND DECRYPT
        Scanner scn = new Scanner(System.in);
        private String name; // NAME OF THE PLAYER
        private final DataInputStream in; // INPUT STREAM
        private final DataOutputStream out; // OUTPUT STREAM
        private Socket s; // SOCKET NUMBER
        public BigInteger N; // NUMBER N FOR ENCRYPTION
        private BigInteger private_key; // THE PUBLIC KEY
        private int played_card = 0;

        // CONSTRUCTOR
        public ClientHandler(Socket s, String name,
                             DataInputStream dis, DataOutputStream dos, BigInteger d, BigInteger N) {
            this.in = dis; this.out = dos;this.name = name;this.s = s; this.N = N; private_key = d;
            support = new support();
        }

        @Override
        public void run() {
            //RECEIVED MESSAGE
            String received;
            while (true)
            {
                try {
                    // RECEIVE THE MESSAGE
                    received = in.readUTF();

                    //PRINT IT OUT
                    String message = decrypt(received);
                    // DECRYPT AND PRINT THE MESSAGE OUT
                    System.out.println("Receiving the message: " + message);

                    // CONVERT THE MESSAGE INTO VALID INPUT SYNTAX
                    int index = message.indexOf(":::");
                    String name = message.substring(0,index);
                    message = message.substring(index+3);

                    String[] input = convertMessageToInput(message);
                    // check for valid meld.
                    // If the meld is valid; then update the game model : Boards
                    System.out.println("---------CHECKING FOR VALID TERM---------");
                    String current_player = turn_checker.peek().name;
                    System.out.println("Here is the current meld "+ game.getMeld());
                    System.out.println(name + "------" + current_player + "\n\n");
                    if(input != null && game.validMeld(input) && name.equals(turn_checker.peek().name)){
                        calculatingPunishedPoint(name,input);

                        game.setCurrentMeld(input);
                        played_card += input.length;
                        attempt = 3;
                        if(played_card == 13){
                            game.setCurrentMeld(null);
                            ClientHandler sample = null;
                            // if the current client attempt their hand, then add this clients into the new deque for the next game

                            for(ClientHandler mc : original) {
                                // Let's other know that the current player has play all the card
                                if (mc.name.equals(name)) {
                                    mc.out.writeUTF(mc.encrypt("SERVER:::01"));
                                    sample = mc;
                                    turn_checker.remove(mc);
                                    if(!punished_player.equals("") && !awarded_player.equals("")){
                                        int point = punishment-2;
                                        if(punished_player.equals("AGGRESSION")) records[0]-= point;
                                        else if(punished_player.equals("RATIONALITY")) records[2] -= point;
                                        else if(punished_player.equals("TERMINATOR")) records[3] -= point;
                                        else{records[1] -= point; }

                                        if(awarded_player.equals("AGGRESSION")) records[0]+= point;
                                        else if(awarded_player.equals("RATIONALITY")) records[2] += point;
                                        else if(awarded_player.equals("TERMINATOR")) records[3] += point;
                                        else{records[1]+= point; }
                                    }
                                    punishment = 0;
                                    punished_player = ""; awarded_player ="";

                                    deque_for_next_game.addLast(sample);
                                    reNewTurn(sample);
                                    System.out.println(name + " played all of his cards");
                                } else {
                                    // let's other know that the sender play MELD
                                    String sms = name + ":::" + message;
                                    mc.out.writeUTF(mc.encrypt(sms));
                                }
                            }
                            lop: for (ClientHandler mc: schedule)
                                if(mc.name.equals(name)){
                                    schedule.remove(mc); break lop;}

                            // Testing points
                            if(schedule.size() == 3){
                                if(name.equals("AGGRESSION")){
                                    aggressive[0] += 1;
                                    records[0] += 2;}
                                else if(name.equals("HUMAN")) {
                                    human[0] += 1;
                                    records[1] += 2;}
                                else if(name.equals("RATIONALITY")){
                                    rational[0] += 1;
                                    records[2] += 2;}
                                else if(name.equals("TERMINATOR")){
                                    p_rational[0] += 1;
                                    records[3] += 2;}

                            }
                            if (schedule.size() == 2){
                                if(name.equals("AGGRESSION")){
                                    aggressive[1] += 1;
                                    records[0] += 1;}
                                else if(name.equals("HUMAN")) {
                                    human[1] += 1;
                                    records[1] +=1;}
                                else if(name.equals("RATIONALITY")){
                                    records[2] += 1;
                                    rational[1] += 1;
                                }
                                else if(name.equals("TERMINATOR")){
                                    records[3] += 1;
                                    p_rational[1] += 1;
                                }
                            }
                            else if (schedule.size() == 1){
                                if(name.equals("AGGRESSION")){
                                    records[0] -= 1;
                                    aggressive[2] += 1;
                                }
                                else if(name.equals("HUMAN")) {
                                    records[1] -= 1;
                                    human[2] += 1;
                                }
                                else if(name.equals("RATIONALITY")){
                                    records[2] -= 1;
                                    rational[2] += 1;
                                }
                                else if(name.equals("TERMINATOR")){
                                    p_rational[2] += 1;
                                    records[3] -= 1;}
                            }

                            // define the round for next round:
                            if(deque_for_next_game.size() == 1){
                                ArrayList<ClientHandler> l = new ArrayList<>();
                                ArrayList<ClientHandler> out = new ArrayList<>();
                                l.addAll(original);
                                int index_current_player = l.indexOf(sample);

                                out.addAll(l.subList(index_current_player,original.size()));
                                out.addAll(l.subList(0,index_current_player));
                                original.clear(); original.addAll(out);
                            }

                            // re-new the game if there is one player left.
                            if(deque_for_next_game.size() == num_clients-1){

                                if(turn_checker.peek().name.equals("AGGRESSION")) {
                                    aggressive[3] += 1;
                                    records[0] -= 2;}
                                else if (turn_checker.peek().name.equals("HUMAN")){
                                    human[3] += 1;
                                    records[1] -=2;}
                                else if(turn_checker.peek().name.equals("RATIONALITY")) {
                                    records[2] -= 2;
                                    rational[3] += 1;
                                }
                                else if(turn_checker.peek().name.equals("TERMINATOR")){
                                    records[3] -=2;
                                    p_rational[3] += 1;
                                }

                                System.out.println("DEALING FOR NEW GAMES!!!!");
                                game.reStart(); schedule.clear();
                                schedule.addAll(original);
                                turn_checker.clear();turn_checker.addAll(schedule);
                                deque_for_next_game.clear();

                                System.out.println("A wins: " + records[0] +
                                        "---- H wins: " + records[1] +
                                        "----- R win: " + records[2] +
                                        "----- T win: " + records[3]);
                                //insert data
                                //insertData();

                                limit--;
                                if(limit ==0)
                                    break;

                                try {
                                    Thread.sleep(2500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                System.out.println("Limit left " + limit);
                                point = 2;
                                // Dealing cards for new round
                                for(ClientHandler clients: schedule){
                                    clients.played_card = 0;
                                    String sms  = "SERVER:::";
                                    sms += convert(game.deal());
                                    clients.out.writeUTF(clients.encrypt(sms));
                                }
                                // upgrade points to clients
                                for(ClientHandler clients: schedule){
                                    clients.played_card = 0;
                                    String sms  = "";
                                    int i =0;
                                    for (int point: records){
                                        if (i == 0) sms += "AGGRESSION;" + point+";";
                                        if (i == 1) sms += "HUMAN;" + point+";";
                                        if (i == 2) sms += "RATIONALITY;" + point+";";
                                        if (i == 3) sms += "TERMINATOR;" + point+";";
                                        i+= 1;
                                    }
                                    clients.out.writeUTF(sms);
                                }
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // then update robots turns

                                // reset the current meld
                                game.setCurrentMeld(null);
                                // Update's robot turns
                                String names = "", str ="";
                                for (ClientHandler c: schedule) {names +=c.name +";"; str += c.name + "---";}
                                System.out.println("New round " + str);
                                for(ClientHandler mc: schedule)
                                    mc.out.writeUTF(names);

                                continue;
                            }
                        }

                        else{
                            turn_checker.addLast(turn_checker.poll());
                            //
                            for(ClientHandler mc: original){
                                // send the sender that the move is valid
                                if(mc.name.equals(name))
                                    mc.out.writeUTF(mc.encrypt("SERVER:::01"));
                                    // let's other know that the sender play MELD
                                else{
                                    //  System.out.println("Sending acknowledge to others");
                                    String sms = name + ":::" +message;
                                    // System.out.println("SENDING TO " + mc.name + " SMS: " +sms);
                                    mc.out.writeUTF(mc.encrypt(sms));
                                }
                            }
                        }

                    }
                    // if the current players pass its turn; then response as ::00 and let's the next one know what to play
                    else if (message.equals(" ") && name.equals(turn_checker.peek().name)){
                        //  System.out.println(name + " quit his turns");
                        for(ClientHandler mc : schedule)
                            // reply to sender
                            if(mc.name.equals(name))
                                mc.out.writeUTF(mc.encrypt("SERVER:::00"));
                        // Let's other know that the current player pass his turns
                        turn_checker.poll();
                        for(ClientHandler mc : schedule)
                            if(!mc.name.equals(name))
                                mc.out.writeUTF(mc.encrypt(name+":::03"));
                        if(turn_checker.size() == 1) {
                            if(!punished_player.equals("") && !awarded_player.equals("")){
                                int point = punishment-2;
                                if(punished_player.equals("AGGRESSION")) records[0]-= point;
                                else if(punished_player.equals("RATIONALITY")) records[2] -= point;
                                else if(punished_player.equals("TERMINATOR")) records[3] -= point;
                                else{records[1] -= point; }

                                if(awarded_player.equals("AGGRESSION")) records[0]+= point;
                                else if(awarded_player.equals("RATIONALITY")) records[2] += point;
                                else if(awarded_player.equals("TERMINATOR")) records[3] += point;
                                else{records[1]+= point; }
                            }
                            punishment = 0;
                            punished_player = ""; awarded_player ="";
                            int i = schedule.indexOf(turn_checker.peek());
                            int j = 0;
                            ArrayList<ClientHandler> sample = new ArrayList<>();
                            for (ClientHandler client: schedule){
                                if (j > i) turn_checker.addLast(client);
                                else if(j < i) sample.add(client);
                                j++; }
                            turn_checker.addAll(sample);
                            game.setCurrentMeld(null);}
                    }
                    //If the message is invalid, then decrease their attempt by 1
                    // Each player will have 3 invalid attempts, then they will lose their turns.
                    // UPDATE THE MOVE FOR OTHER PLAYERS
                    else if (name.equals(turn_checker.peek().name)){
                        attempt--;
                        if(attempt ==0){
                            // reset the current meld;
                            attempt = 3;
                            turn_checker.poll();
                            game.setCurrentMeld(null);
                            // let's the next player his turns
                            // response to the current player that he loses his turn
                            for(ClientHandler mc : schedule){
                                // reply to sender
                                if(mc.name.equals(name)){
                                    mc.out.writeUTF(mc.encrypt("SERVER:::00"));
                                }
                                // let's player now his turn is ready
                                else
                                    mc.out.writeUTF(mc.encrypt(name+":::03"));
                            }
                            // re new turn
                            if(turn_checker.size() == 1){
                                int i = schedule.indexOf(turn_checker.peek());
                                int j = 0;
                                ArrayList<ClientHandler> sample = new ArrayList<>();
                                for (ClientHandler client: schedule){
                                    if (j > i) turn_checker.addLast(client);
                                    else if(j < i) sample.add(client);
                                    j++; }
                                turn_checker.addAll(sample);
                            }

                        }
                        else{
                            System.out.println("INVALID INPUT  ------ "  + message);
                            for(ClientHandler mc : turn_checker)
                                if(mc.name.equals(name)) mc.out.writeUTF(mc.encrypt("SERVER:::03"));
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // calculating punishments
        private void calculatingPunishedPoint(String player,String[] input) {
            if(support.isBlackPig(game.getCurrent_meld())){
                if(punishment == 0){
                    punishment += 1;
                    punished_player = turn_checker.peekLast().name;}
                if(support.isBlackPig(input)) {punishment += 1;punished_player = player;}
                else if (support.isRedPig(input)) {punishment += 2;punished_player = player;}
                else if(support.isHotGun(input)){punishment += 2;awarded_player = player;}
            }
            else if (support.isRedPig(game.getCurrent_meld())){
                if(punishment == 0){
                    punishment += 2;
                    punished_player = turn_checker.peekLast().name;}
                if (support.isRedPig(input)) {punishment += 2;punished_player = player;}
                else if(support.isHotGun(input)){punishment += 2;awarded_player = player;}
            }
            else if(support.isHotGun(game.getCurrent_meld())){
                if(support.isHotGun(input)){punishment += 2;
                    punished_player = awarded_player;
                    awarded_player = player;}
            }
            else if(support.isPigs(game.getCurrent_meld())){
                if(punishment == 0){
                    for(String pig: game.getCurrent_meld())
                        if(support.isBlackPig(new String[]{pig}))
                            punishment++;
                        else punishment += 2;
                    punished_player = turn_checker.peekLast().name;
                }
                if (support.isPigs(input)) {
                    for(String pig: input)
                        if(support.isBlackPig(new String[]{pig}))
                            punishment++;
                        else punishment += 2;
                    punished_player = player;}
                else if(support.isHotGun(input)){punishment += 2;awarded_player = player;}
            }
            if(support.isHotGun(input)){
                System.out.println("PUNISHMENT " + punished_player + "-----"+ awarded_player);

            }

        }

        // THE CONVERT MESSAGE FUNCTION
        private String[] convertMessageToInput(String message){
            ArrayList<String> list = new ArrayList<>();
            StringTokenizer str = new StringTokenizer(message,";");
            while(str.hasMoreTokens()){
                list.add(str.nextToken());
            }
            String[] input = new String[list.size()];
            list.toArray(input);
            if (input.length >= 1) return input;
            else return null;
        }

        private String encrypt(String msg){return secure_system.encrypt(private_key,N,msg).toString();}
        private String decrypt(String msg){return secure_system.decrypt(private_key,N,new BigInteger(msg));}
    }
    public static String convert(Card[] list){
        String str = "";
        for(Card c: list)
            str += c.toString();
        return str;
    }
    private static void firstRound() {
        Deck x = new Deck();

        Map<Card, ClientHandler> map = new TreeMap<Card,ClientHandler>(new Comparator<Card>() {
            @Override
            public int compare(Card a, Card b) {
                if(a.getValue() != b.getValue()) return a.getValue() - b.getValue();
                return a.getSuit().compareTo(b.getSuit());}
        });
        for(ClientHandler p : schedule)
            map.put(x.pop(),p);
        schedule.clear();
        for(Map.Entry<Card, ClientHandler> a : map.entrySet())
            schedule.add(a.getValue());

        for (ClientHandler y: schedule)
            toRecord.add(y.name);

    }
    private static void reNewTurn(ClientHandler x){
        ArrayList<ClientHandler> sample = new ArrayList<>();
        sample.addAll(schedule);
        int index_current_player = sample.indexOf(x);
        if(index_current_player == -1) index_current_player = 0;
        turn_checker.clear();
        turn_checker.addAll(sample.subList(index_current_player+1,sample.size()));
        turn_checker.addAll(sample.subList(0,index_current_player));
        String str = "";
        for(ClientHandler xx : turn_checker) str += xx.name + "--";
        /*  System.out.println("Here is the new round: \n" + str);*/
    }
    private static void insertData(){
        try {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            mongoClient = new MongoClient();
            //data base name
            DB database = mongoClient.getDB("test");
            // data base collections
            DBCollection collection = database.getCollection("test");

            // insert data
            BasicDBObject data = new BasicDBObject();
            data.append("testType", "4players");
            data.append("first_player", "AggressionPlayer");
            data.append("second_player", "PatientPlayer");
            data.append("third_player", "RationalPlayer");
            data.append("forth_player", "RationalPlayer");
            data.append("first_point", records[0]);
            data.append("second_point", records[1]);
            data.append("third_point", records[2]);
            data.append("forth_point", records[3]);
            collection.insert(data);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}


