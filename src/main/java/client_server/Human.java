package client_server;

import cryptography.EncryptSystem;

import java.util.*;

public class Human extends Client
{
    public Human(String name) { super(name);
        hand = new ArrayList<String>();
        secure_system = new EncryptSystem();
        schedule = new ArrayList<>();
        turns = new ArrayDeque<>();
        deque_for_next_game = new LinkedList<String>();
        new_round = false;
    }
    public static class sort implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
            ArrayList<Character> l1 = new ArrayList<>();
            l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
            l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
            if(a.charAt(0) == b.charAt(0)) return (l.indexOf(a.charAt(1)) - l.indexOf(b.charAt(1)));
            else if (l1.contains(a.charAt(0)) && !l1.contains(b.charAt(0))) return 1;
            else if (!l1.contains(a.charAt(0)) && l1.contains(b.charAt(0))) return -1;
            else return l1.indexOf(a.charAt(0)) - l1.indexOf(b.charAt(0));
        }
    }

}