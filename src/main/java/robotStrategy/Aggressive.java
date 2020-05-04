package robotStrategy;
import client_server.Client;
import game.Meld;

import java.util.*;

public class Aggressive implements RobotStrategy {
    private static ArrayList<String> hand;
    private static Meld meld;
    private static String[] current_meld;
    private support support_functions = new support();

    @Override
    /*
        Find the most appropriate competitive meld
        A single card --> choose the one which is not being use to form up a pair and or a sequence.
            If there is none --> choose the lowest one
        Play aggressively strategy
       If it is first turn: It will play the lowest melds (single, pair, sequences)

       In the competitive, it will play against by choosing meld as small as possible.
        The strategy will be allowed to break up sequences or pairs to play against
     */

    public String[] play(Client c) {
        hand = new ArrayList<>();
        hand.addAll(c.getHand());
        Collections.sort(hand, new sort());
        //sort to find sequences, pairs, single cards --> choose the best one
        //sort to find pairs, sequences, single cards -->  choose the best one
        if(c.getCurrentMeld().getValue() == null || c.getCurrentMeld().getValue().length == 0){
            List<String> hotGuns = support_functions.findAGun(hand);
            if(hotGuns.size() > 0){
                for (String card: hotGuns)
                    hand.remove(card);
            }

            // list contains single cards after find all seqs and pairs
            List<String> first_Strategy =  support_functions.findAllSequencesThenPairs(new ArrayList<>(hand));
            // list contains single cards after find all pairs and seqs
            List<String> second_Strategy = support_functions.findAllPairsThenSequences(new ArrayList<>(hand));
            List <String> remove_cards;
            // that means second_strategy is better
            int d;
            if(first_Strategy.size() > second_Strategy.size()){
                remove_cards = second_Strategy;d = 2;}
            else{ d= 1;
                remove_cards = first_Strategy;}
            // remove cards is the list of trash cards
            // Decide what to play singles, pairs, sequences
            List<String >pairs = new ArrayList<>();
            List<String >sequences= new ArrayList<>();

            if( d == 1) {
                sequences = support_functions.findEfficientlyAllSequences(hand);
                if (sequences.size() > 0){
                    sequences = sequences.subList(0, support_functions.num_sequences);
                List<String> remove = new ArrayList<>(sequences);
                for (String meld : remove) {
                    while (meld.length() != 0) {
                        hand.remove(meld.substring(0, 2));
                        meld = meld.substring(2);
                    }
                }
            }
                pairs = support_functions.findPairsAndTriples(hand);
            }
            else {
                pairs = support_functions.findPairsAndTriples(hand);
                if (pairs.size() > 0){
                    pairs = pairs.subList(0, support_functions.num_pairs);
                List<String> remove = new ArrayList<>(pairs);
                for (String meld : remove) {
                    while (meld.length() != 0) {
                        hand.remove(meld.substring(0, 2));
                        meld = meld.substring(2);
                    }
                }
            }
                sequences = support_functions.findEfficientlyAllSequences(hand);
            }

            int toRemove = support_functions.theSmallestMeld(remove_cards,sequences,pairs);

/*
            System.out.println(remove_cards);
            System.out.println(d);
            System.out.println(sequences);
            System.out.println(pairs);
            System.out.println(toRemove);
*/

            if(toRemove == 1){
                c.getHand().remove(remove_cards.get(0));
                return new String[]{remove_cards.get(0)};
            }
            else if (toRemove == 2){
                String str =sequences.get(0);
                String[] output = new String[str.length()/2];
                Object[] list = support_functions.convertMeldToCards(hand,str).toArray();
                int i =0;
                for(Object x:list){
                    output[i] = (String) x;i++;
                    c.getHand().remove(x);
                }
                return output;
            }

            else if (toRemove == 3){
                String str = pairs.get(0);
                String[] output = new String[str.length()/2];
                Object[] list = support_functions.convertMeldToCards(c.getHand(),str).toArray();
                int i =0;
                for(Object x:list){
                    output[i] = (String) x;
                    c.getHand().remove(x);
                    i++;
                }
                return output;
            }
        }
        current_meld = Arrays.copyOf(c.getCurrentMeld().getValue(),c.getCurrentMeld().getValue().length);
        // sequences, pairs and singles
        List<String> hotGuns = support_functions.findAGun(hand);
        if(hotGuns.size() > 0){
            for (String card: hotGuns)
                hand.remove(card);
        }

        Arrays.sort(current_meld, new sort());
        Collections.sort(hand, new sort());
        if(support_functions.isHotGun(current_meld)){
            if(hotGuns.size() >0){
                String [] output = support_functions.convert(hotGuns.get(0));
                meld  = new Meld();
                meld.setValue(current_meld);
                if(meld.checkMeld(output)){
                    for(String s: output)
                        c.getHand().remove(s);
                    return output;
                }

            }
            return new String[]{"00"};
        }
        else if(support_functions.isSequence(current_meld)){
            List<String> potential_sequences = support_functions.findEfficientlyAllSequences(hand);
            // find potential sequences
            for(int i=0; i < potential_sequences.size();i++){
                if(support_functions.checkPotentialMeld(potential_sequences.get(i),current_meld)){
                    String [] output = support_functions.convert(potential_sequences.get(i));
                    for(String s: output)
                        c.getHand().remove(s);
                    return output;}
            }
            //playing aggressive.
            potential_sequences.clear();
            potential_sequences = support_functions.findASequences(hand,current_meld.length);
            for(int i=0; i < potential_sequences.size();i++){
                if(support_functions.checkSequences(potential_sequences.get(i),current_meld)){
                    String [] output = support_functions.convertAggressively(potential_sequences.get(i),current_meld);
                    for(String s: output)
                        c.getHand().remove(s);
                    return output;}
            }
            return new String[]{"00"};
        }
        else if (support_functions.isPairTriple(current_meld)){
            if(current_meld.length == 2 && current_meld[0].charAt(0) == 'L' && hotGuns.size() > 0
            && hotGuns.get(0).length()/4 >= 4){
                String [] output = support_functions.convert(hotGuns.get(0));
                for(String x: output)
                    c.getHand().remove(x);
                return output;
            }
            else if(current_meld.length == 3 && current_meld[0].charAt(0) == 'L' && hotGuns.size() > 0
                    && hotGuns.get(0).length()/4 >= 5){
                String [] output = support_functions.convert(hotGuns.get(0));
                for(String x: output)
                    c.getHand().remove(x);
                return output;
            }

            List<String> potential_pairs = support_functions.findPairsAndTriples(hand);
            // find potential pairs
            for(int i=0; i < potential_pairs.size();i++){
                if(support_functions.checkPotentialMeld(potential_pairs.get(i),current_meld)){
                    String [] output = support_functions.convert(potential_pairs.get(i));
                    for(String s: output)
                        c.getHand().remove(s);
                    return output;
                }
            }
            // find aggressively pairs
            // find potential pairs
            for(int i=0; i < potential_pairs.size();i++){
                if(support_functions.aggresivelyCheckPotentialMeld(potential_pairs.get(i),current_meld)){
                    String [] output = support_functions.convertAggressivelyPair(potential_pairs.get(i),current_meld.length);
                    for(String s: output)
                        c.getHand().remove(s);
                    return output;
                }
            }
            return new String[]{"00"};
        }
        else{
            if(current_meld.length == 1 && current_meld[0].charAt(0) == 'L' && hotGuns.size() > 0){
                String [] output = support_functions.convert(hotGuns.get(0));
                for(String x: output)
                    c.getHand().remove(x);
                return output;
            }


            // list contains single cards after find all seqs and pairs
            List<String> first_Strategy =  support_functions.findAllSequencesThenPairs(new ArrayList<>(hand));
            // list contains single cards after find all pairs and seqs
            List<String> second_Strategy = support_functions.findAllPairsThenSequences(new ArrayList<>(hand));
            List <String> remove_cards;
            // that means second_strategy is better
            if(first_Strategy.size() > second_Strategy.size()) remove_cards = second_Strategy;
            else remove_cards = first_Strategy;
            for(String card: remove_cards){
                if(current_meld.length == 0) {
                    c.getHand().remove(card);
                    return new String[]{card};
                }
                if(support_functions.compareSingleCard(card,current_meld[0])){
                    c.getHand().remove(card);
                    return new String[]{card};
                }
            }
            List<String> potential_sequences = support_functions.findEfficientlyAllSequences(hand);
            for(int i =0; i < potential_sequences.size();i++){
                String out =support_functions.findASingleCardInAMeld(potential_sequences.get(i),current_meld[0]);
                if( out != null){
                    c.getHand().remove(out);
                    return new String[] {out};}
            }
            List<String> potential_pairs = support_functions.findPairsAndTriples(hand);
            for(int i =0; i < potential_pairs.size();i++){
                String out =support_functions.findASingleCardInAMeld(potential_pairs.get(i),current_meld[0]);
                if(out != null){
                    c.getHand().remove(out);
                    return new String[] {out};}
            }
            return new String[]{"00"};
        }
    }

    // sort class from 3 to L (2)
    public class sort implements Comparator<String> {
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

