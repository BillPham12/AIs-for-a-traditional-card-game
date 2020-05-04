package robotStrategy;

import client_server.Client;
import game.Meld;

import java.util.*;

/*
The robot tends to play the small melds if it is its turn as the aggressive robot does

The ranking melds is consider as J and up to 2.
But in the competitive, the robot will tends to keep the high ranking melds and play the low & medium ranking melds.

When the robot has played 5 cards or other players has played more than or equals to 6 cards , it will play aggressively

 */
public class Patient implements  RobotStrategy{
    private static ArrayList<String> hand;
    private static Aggressive aggressive = new Aggressive();
    private static String[] current_meld;
    private support support_functions = new support();
    private static Meld meld;
    @Override
    public String[] play(Client c) {
        if(c.getHand().size() < 7) return aggressive.play(c);
        else{
            /*System.out.println(checkPlayNormal(c));*/
            if(!checkPlayNormal(c) || c.getCurrentMeld().getValue() == null){
                return aggressive.play(c);}
            // else statement for competitive
            else{
                hand = new ArrayList<>(c.getHand());
                /*System.out.println(hand);*/
                current_meld = Arrays.copyOf(c.getCurrentMeld().getValue(),c.getCurrentMeld().getValue().length);
                // sequences, pairs and singles
                Arrays.sort(current_meld, new sort());
                Collections.sort(hand, new sort());

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
               /* System.out.println(c.getCurrentMeld());*/
                int d;
                if(first_Strategy.size() > second_Strategy.size()){
                    remove_cards = second_Strategy;d = 2;}
                else{ d= 1;
                    remove_cards = first_Strategy;}
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

                // case for sequences
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
                    List<String> potential_sequences =sequences;
                    // find potential sequences
                    for(int i=0; i < potential_sequences.size();i++){
                        if(support_functions.checkPotentialMeld(potential_sequences.get(i),current_meld)){
                            if(support_functions.isLowMeld(potential_sequences.get(i))) {
                               String [] output = support_functions.convert(potential_sequences.get(i));
                                for(String s: output)
                                    c.getHand().remove(s);
                             return output;}
                        }
                    }
                    //playing aggressive.
                    potential_sequences.clear();
                    potential_sequences = support_functions.findASequences(hand,current_meld.length);
                    for(int i=0; i < potential_sequences.size();i++){
                        if(support_functions.isLowMeld(potential_sequences.get(i)) && support_functions.checkSequences(potential_sequences.get(i),current_meld)){
                            String [] output = support_functions.convertAggressively(potential_sequences.get(i),current_meld);
                            for(String s: output)
                                c.getHand().remove(s);
                            return output;}
                    }
                    return new String[]{"00"};
                }
                // case for pair
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

                    List<String> potential_pairs = pairs;
                    // find potential pairs
                    for(int i=0; i < potential_pairs.size();i++){
                        if(support_functions.checkPotentialMeld(potential_pairs.get(i),current_meld)){
                            if(support_functions.isLowMeld(potential_pairs.get(i))) {
                                String[] output = support_functions.convert(potential_pairs.get(i));
                                for (String s : output)
                                    c.getHand().remove(s);
                                return output;
                            }
                        }
                    }
                    return new String[]{"00"};
                }
                // case for a single card
                else{

                    if(current_meld.length == 1 && current_meld[0].charAt(0) == 'L' && hotGuns.size() > 0){
                        String [] output = support_functions.convert(hotGuns.get(0));
                        for(String x: output)
                            c.getHand().remove(x);
                        return output;
                    }

                    if(!support_functions.isLowMeld(current_meld[0]))
                        return new String[]{"00"};

                    for(String card: remove_cards){
                        if(support_functions.compareSingleCard(card,current_meld[0])){
                            c.getHand().remove(card);
                            return new String[]{card};
                        }
                    }

                    for(int i =0; i < sequences.size();i++){
                        String out =support_functions.findASingleCardInAMeld(sequences.get(i),current_meld[0]);
                        if( out != null){
                            c.getHand().remove(out);
                            return new String[] {out};}
                    }

                    for(int i =0; i < pairs.size();i++){
                        String out =support_functions.findASingleCardInAMeld(pairs.get(i),current_meld[0]);
                        if(out != null){
                            c.getHand().remove(out);
                            return new String[] {out};}
                    }
                    return new String[]{"00"};
                }
            }
        }

    }
    private boolean checkPlayNormal(Client c){
        int num_players =c.getNumber_of_player();
        int decide = 0;
        for (Map.Entry mapElement : c.getListOfThePlayer().entrySet()) {
            String key = (String)mapElement.getKey();
            if(c.getListOfThePlayer().get(key).size() > 6)
                 decide++;
        }
        int x = 0;
        if (num_players == 1) x = 1;
        else x = num_players/2;
        return decide < x;
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
