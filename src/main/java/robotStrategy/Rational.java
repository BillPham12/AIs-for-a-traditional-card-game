package robotStrategy;

import client_server.Client;
import client_server.RationalPlayer;
import game.Deck;
import game.Meld;

import java.lang.reflect.Array;
import java.util.*;

public class Rational implements RobotStrategy {
    private static ArrayList<String> hand;
    private static Meld meld;
    private static String[] current_meld;
    private support support_functions = new support();
    private  boolean potential = false;
    private int strategy = 0;
    private List<String> winning_meld ;
    private Map<String,ArrayList> players_data;
    private String current_player;
    private int card_left = 0;
    private List<ArrayList<String>> knowledge;
    private int way = 0, number_of_player = 0;
    @Override
    public String[] play(Client c) {
        number_of_player = c.getNumber_of_player();
        knowledge = new ArrayList<ArrayList<String>>();
        card_left = 0; way = 0;strategy = 0;
        current_player = c.getTurns().peek();
        winning_meld = new ArrayList<>();
        hand = new ArrayList<>(c.getHand());
        players_data = new HashMap<>(c.getListOfThePlayer());
        upgradeKnowledge(players_data);


        Collections.sort(hand, new sort());
        // Decide to play for new turn
        if (c.getCurrentMeld().getValue() == null){
           // System.out.println("1 is call");
            checkForPotentialWin(c);
            String play_meld= "";
            //System.out.println(winning_meld + "--- card left " + card_left);
            if (winning_meld != null && potential && winning_meld.size() <= 3) {
                if(winning_meld.size() == 1){
                    play_meld = winning_meld.get(0); }
                else if (winning_meld.size() == 2 && containATriple(winning_meld)){
                    loop: for (String meld: winning_meld){
                        if (isTriple(meld)) {
                            play_meld = meld; break loop;}
                    }
                }
                else if (winning_meld.size() == 2){
                    if ( isBestCard(knowledge,winning_meld.get(0)) || (isBestPair(knowledge,winning_meld.get(0)) || isBestSequences(knowledge,winning_meld.get(0))))
                        play_meld = winning_meld.remove(0);
                    else if ((isBestPair(knowledge,winning_meld.get(1)) || isBestSequences(knowledge,winning_meld.get(1)))
                            || isBestCard(knowledge,winning_meld.get(1)))
                        play_meld = winning_meld.remove(1);
                    else {
                        Collections.sort(winning_meld,new sort());
                        if (winning_meld.get(0).length()/2 != card_left && card_left == 2)
                            play_meld = winning_meld.remove(0);
                        else if (card_left == 2)
                            play_meld = winning_meld.remove(0).substring(0,2);
                        else if (card_left == 1){
                            for (String meld: winning_meld)
                                if (meld.length() != 2)
                                    play_meld = meld;
                            if(play_meld.equals("")){
                                Collections.sort(winning_meld, new sort());
                                play_meld = winning_meld.get(1);
                            }
                        }
                        else
                            play_meld = winning_meld.remove(0);
                    }
                    // System.out.println(play_meld);
                }
                else{
                    if(card_left == 1){
                        // if there is a meld is not a single meld then just play it
                        for (String meld: winning_meld){
                            if(meld.length() != 2){
                                play_meld = meld;
                            }
                        }
                        // winning meld is only single card
                        if(play_meld.equals("")){
                            Collections.sort(winning_meld,new sort());
                            play_meld = winning_meld.get(winning_meld.size()-1);
                        }
                    }
                    else if (card_left == 2){
                        int i = 1;
                        if (winning_meld.size() == 0 )
                            play_meld = winning_meld.get(0);

                        else{
                            // if there is a meld is not a pair then just play it
                            for (String meld: winning_meld){
                                // tends not to play the highest single card
                                if (i == winning_meld.size() && winning_meld.get(i-1).length() == 2){
                                    Collections.sort(winning_meld,new sort());
                                    //  System.out.println(winning_meld);
                                    if (winning_meld.get(0).length() != 4)
                                        play_meld = winning_meld.get(0);
                                    else
                                        play_meld = winning_meld.get(0).substring(0,2);
                                }
                                else if(meld.length() != 4){
                                    play_meld = meld;
                                }
                                i++;
                            }
                        }
                        // winning meld is only pair, then play the small card
                        //  System.out.println(winning_meld);
                        if(play_meld.equals("")){
                            play_meld = winning_meld.get(0).substring(0,2);
                        }
                    }
                    else{
                        // otherwise play smartly
                        if(winning_meld.size() == 3 && winning_meld.get(1).length() == winning_meld.get(2).length()
                                && ((isBestPair(knowledge,winning_meld.get(2)) || isBestSequences(knowledge,winning_meld.get(2)))||
                                (isBestCard(knowledge,winning_meld.get(2))))) play_meld = winning_meld.remove(1);

                        else if(winning_meld.size() == 3 && winning_meld.get(0).length() == winning_meld.get(1).length()
                                && ((isBestPair(knowledge,winning_meld.get(1)) || isBestSequences(knowledge,winning_meld.get(1)))||
                                (isBestCard(knowledge,winning_meld.get(1))))) play_meld = winning_meld.remove(0);

                        else if (winning_meld.size() == 2)
                        {
                            if ((isBestPair(knowledge,winning_meld.get(0)) || isBestSequences(knowledge,winning_meld.get(0)))
                                    || (isBestCard(knowledge,winning_meld.get(0))))
                                play_meld = winning_meld.remove(0);
                            else if ((isBestPair(knowledge,winning_meld.get(1)) || isBestSequences(knowledge,winning_meld.get(1))
                                    || (isBestCard(knowledge,winning_meld.get(1)))))
                                play_meld = winning_meld.remove(1);
                            else {Collections.sort(winning_meld,new sort());
                                play_meld = winning_meld.remove(0);}
                        }

                        else {
                            Collections.sort(winning_meld,new sort());
                            play_meld = winning_meld.remove(0);
                        }
                    }
                }
                // play and return the play meld.
              //  System.out.println(play_meld);
                String[] out = new String[play_meld.length()/2];
                int i =0;
                while(play_meld.length() > 0){
                    out[i] = play_meld.substring(0,2); i++;
                    c.getHand().remove(play_meld.substring(0,2));
                    play_meld = play_meld.substring(2);
                }
                return out;
            }
            else{
               // System.out.println("2 is call");
                hand = new ArrayList<>(c.getHand());
                Collections.sort(hand, new sort());
                return playTheSmallestMeld(c); }
        }
        // The competitive turn
        else{
            current_meld = Arrays.copyOf(c.getCurrentMeld().getValue(),c.getCurrentMeld().getValue().length);

            List<String> hotGuns = support_functions.findAGun(hand);
            if(hotGuns.size() > 0){
                for (String card: hotGuns)
                    hand.remove(card);
            }

            // sequences, pairs and singles
            Arrays.sort(current_meld, new sort());
            Collections.sort(hand, new sort());
            // Handling sequence
            ArrayList<String> sample = new ArrayList<>(hand);
            List<String> potential_sequences= new ArrayList<>();
            List<String> potential_pairs = new ArrayList<>();
            // list contains single cards after find all sequences and pairs
            List<String> first_Strategy =  support_functions.findAllSequencesThenPairs(new ArrayList<>(hand));
            // list contains single cards after find all pairs and sequences
            List<String> second_Strategy = support_functions.findAllPairsThenSequences(new ArrayList<>(hand));
            List <String> remove_cards;
            // that means second_strategy is better
            int strategy;
            if(first_Strategy.size() > second_Strategy.size()) {remove_cards = second_Strategy;strategy = 2;}
            else {remove_cards = first_Strategy; strategy = 1;}

            if(strategy == 1) {
                if (support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)).size() > 0){
                    potential_sequences = support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)).subList(0, support_functions.num_sequences);
                    for (String seq : potential_sequences) {
                        while (!seq.equals("")) {
                            String str = seq.substring(0, 2);
                            seq = seq.substring(2);
                            hand.remove(str);
                        }
                    }
                }
                potential_pairs = support_functions.findPairsAndTriples(new ArrayList<>(hand));
            }
            else{
                if(support_functions.findPairsAndTriples(new ArrayList<>(hand)).size() > 0){
                    potential_pairs = support_functions.findPairsAndTriples(new ArrayList<>(hand)).subList(0,support_functions.num_pairs);
                    for (String pair: potential_pairs){
                        while(!pair.equals("")){
                            String str = pair.substring(0,2);
                            pair = pair.substring(2);
                            hand.remove(str);
                        }
                    }
                }
                potential_sequences = support_functions.findEfficientlyAllSequences(new ArrayList<>(hand));
            }

/*            System.out.println("all pairs " + potential_pairs + "\nall sequences"+ potential_sequences
            + "\n remove cards" + remove_cards);*/

            List<String> king = new ArrayList<>(); king.addAll(potential_pairs); king.addAll(potential_sequences); king.addAll(remove_cards);


            boolean possible = false;
            if (king.size() <= 3){
                int x = 0;
                for (String meld: king){
                    if (isBestPair(knowledge,meld) || isBestSequences(knowledge,meld) || isBestCard(knowledge,meld))  x++;
                }
                if (x >= king.size()*2/3) possible = true;
            }

            if(current_player.equals(c.getSpecialEnemy())&& c.getNumber_of_player() > 2 && possible == false)
                return new String[]{"00"};

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
                // find potential sequences
                for(int i=0; i < potential_sequences.size();i++){
                    if(support_functions.checkSequences(potential_sequences.get(i),current_meld)
                     && (potential_sequences.get(i).length()/2 == 5 && current_meld.length == 3)){
                        String s = potential_sequences.get(i).substring(4,6);
                        Collections.sort(c.getHand(),new sort());
                        int index = c.getHand().indexOf(s);
                        if(c.getHand().get(index +1).charAt(0) != s.charAt(0) && c.getHand().get(index -1).charAt(0) != s.charAt(0) )
                            return new String[]{"00"};
                        else{
                            String [] output = support_functions.convertAggressively(potential_sequences.get(i),current_meld);
                            for(String ss: output)
                                c.getHand().remove(ss);
                            return output;
                        }
                    }
                    if(support_functions.checkPotentialMeld(potential_sequences.get(i),current_meld)){
                        String [] output = support_functions.convert(potential_sequences.get(i));
                        for(String s: output)
                            c.getHand().remove(s);
                        return output;}
                }
                //playing aggressive.
               /* System.out.println(remove_cards);*/
                int len = current_meld.length;
                // handle special case
                // optimize the behavior of rational player
                for(int i=0; i < potential_sequences.size();i++){
                    if(potential_sequences.get(i).length()/2 >= len){
                        for (String card: remove_cards){
                            String current_card = potential_sequences.get(i).substring((len-1)*2,(len*2));
                            if(card.charAt(0) == potential_sequences.get(i).charAt((len-1)*2)
                            && support_functions.compareSingleCard(card,current_card)){
                                String out = potential_sequences.get(i).substring(0,(len-1)*2);
                                out += card;
                                if(support_functions.checkPotentialMeld(out,current_meld)){
                                    String [] output = support_functions.convert(out);
                                    for(String s: output)
                                        c.getHand().remove(s);
                                    return output;}
                            }
                        }
                    }
                }

                for(int i=0; i < potential_sequences.size();i++){
                    if(support_functions.checkSequences(potential_sequences.get(i),current_meld)){
                        if(potential_sequences.get(i).length()/2 == 5 && current_meld.length == 3){
                            String s = potential_sequences.get(i).substring(4,6);
                            Collections.sort(c.getHand(),new sort());
                            int index = c.getHand().indexOf(s);
                            if(c.getHand().get(index +1).charAt(0) != s.charAt(0) && c.getHand().get(index -1).charAt(0) != s.charAt(0) )
                                return new String[]{"00"};
                            else {
                                String[] output = support_functions.convertSmartly(potential_sequences.get(i), current_meld);
                                for (String ss : output)
                                    c.getHand().remove(ss);
                                if(output.length > 0)
                                    return output;
                            }
                        }
                        else{
                            String [] output = support_functions.convertSmartly(potential_sequences.get(i),current_meld);
                            for(String s: output)
                                c.getHand().remove(s);
                            if(output.length > 0)
                                return output;
                        }
                    }
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
            // Handling pair
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


                if(current_player.equals(c.getSpecialEnemy()) && c.getNumber_of_player() > 2) return new String[]{"00"};
                // find potential pairs
                for(int i=0; i < potential_pairs.size();i++){
                    if(support_functions.checkPotentialMeld(potential_pairs.get(i),current_meld)){
                        String [] output = support_functions.convert(potential_pairs.get(i));
                        for(String s: output)
                            c.getHand().remove(s);
                        return output;
                    }
                }
                // find potential pairs
                for(int i=0; i < potential_sequences.size();i++){
                    List<String> seqs = new ArrayList<>(potential_sequences);
                    List<String> cards = new ArrayList<>();

                    if (potential_sequences.get(i).length() >= 8){
                        cards = new ArrayList<>();
                        String seq =seqs.get(i);
                            while(seq.length() > 0){ cards.add(seq.substring(0,2)); seq = seq.substring(2);}
                    }
                    List<String> cards_used_to_form_up = new ArrayList<>();

                    if(potential_sequences.get(i).length() == 8){
                        cards_used_to_form_up.add(cards.get(0));cards_used_to_form_up.add(cards.get(3));
                    }
                    else if(potential_sequences.get(i).length() == 10){
                        cards_used_to_form_up.add(cards.get(0));cards_used_to_form_up.add(cards.get(4)); }

                    else if(potential_sequences.get(i).length() == 12)
                    {cards_used_to_form_up.add(cards.get(0));cards_used_to_form_up.add(cards.get(1));
                        cards_used_to_form_up.add(cards.get(5));cards_used_to_form_up.add(cards.get(5));}
                    else if(potential_sequences.get(i).length() == 14 )
                    {cards_used_to_form_up.add(cards.get(0));cards_used_to_form_up.add(cards.get(1));
                        cards_used_to_form_up.add(cards.get(5));cards_used_to_form_up.add(cards.get(6));}
                    for (String card: remove_cards){
                        for (String card_in_sequences: cards){
                            if (card.charAt(0) == card_in_sequences.charAt(0)
                            && support_functions.checkPotentialMeld(card+card_in_sequences,current_meld)){
                                String [] output = new String[]{card, card_in_sequences};
                                for(String s: output)
                                    c.getHand().remove(s);
                                return output;
                            }
                        }
                    }
                }

                // find aggressively pairs
                potential_pairs = support_functions.findPairsAndTriples(hand);
                // find potential pairs
                for(int i=0; i < potential_pairs.size();i++){
                    if(support_functions.aggresivelyCheckPotentialMeld(potential_pairs.get(i),current_meld)){
                        String [] output = support_functions.convertSmartly(potential_pairs.get(i),current_meld);
                        if(output.length > 1){
                            for(String s: output)
                                c.getHand().remove(s);
                            return output;
                        }
                    }
                }
                return new String[]{"00"};
            }
            // Handling a card
            else{
                int size = potential_pairs.size();
                // play a 2 instead
/*                System.out.println(remove_cards);
                System.out.println(potential_pairs);*/

                if(current_meld.length == 1 && current_meld[0].charAt(0) == 'L' && hotGuns.size() > 0){
                    String [] output = support_functions.convert(hotGuns.get(0));
                    for(String x: output)
                        c.getHand().remove(x);
                    return output;
                }


                if(remove_cards.size() <= 2 && potential_pairs.size() > 0 && potential_pairs.get(size-1).substring(0,1).equals("L")){
                    String out =support_functions.findASingleCardInAMeld(potential_pairs.get(size-1),current_meld[0]);
                    if(out != null){
                        c.getHand().remove(out);
                        return new String[] {out};}
                }

                for(String card: remove_cards){
                    if(support_functions.compareSingleCard(card,current_meld[0])){
                        c.getHand().remove(card);
                        return new String[]{card};
                    }
                }

                for(int i =0; i < potential_sequences.size();i++){
                    String out =support_functions.findASingleCardInAMeld(potential_sequences.get(i),current_meld[0]);
                    if( out != null){
                        c.getHand().remove(out);
                        return new String[] {out};}
                }

                for(int i =0; i < potential_pairs.size();i++){
                    String out =support_functions.findASingleCardInAMeld(potential_pairs.get(i),current_meld[0]);
                    if(out != null){
                        c.getHand().remove(out);
                        return new String[] {out};}
                }
                return new String[]{"00"};
            }
        }
    }

    private int getKnowledge() {
        int x = 0;
        for (Map.Entry<String,ArrayList> entry: players_data.entrySet()){
            List<String> current = entry.getValue();
            if (current.size() != 13) {
                if(x == 0){x = 13 - current.size();}
                else if (x > (13 - current.size())) x = 13 - current.size();
            }
        }
        return x;
    }

    private boolean containATriple(List<String> winning_meld) {
        for (String meld: winning_meld){
            if(isTriple(meld)) return true;
        }
        return false;
    }
    private boolean isTriple(String meld){
        if(meld.length() == 6 || meld.length() ==  8){
            if (meld.length() == 6 && meld.charAt(0) == meld.charAt(2) && meld.charAt(0) == meld.charAt(4))
                return true;
            else if (meld.length() == 8 && meld.charAt(0) == meld.charAt(2) && meld.charAt(0) == meld.charAt(4)
                    && meld.charAt(0) == meld.charAt(6)) return true;
        }
        return false;
    }

    private boolean isBestCard(List<ArrayList<String>> knowledge, String s) {
        ArrayList<Character> l = new ArrayList<>();
        l.add('3');l.add('4');l.add('5');l.add('6');l.add('7');l.add('8');l.add('9');l.add('X');
        l.add('J');l.add('Q');l.add('K');l.add('A');l.add('L');
        if(s.length() != 2) return false;
        int index = l.indexOf(s.charAt(0));
        if (s.charAt(0) == 'L' && s.charAt(1) == 'H') return true;
        else if ( number_of_player < 3 && index == 12) return true;
        else {
            for (int i = index+1; i < 13;i++){
                if(knowledge.get(i).size() < 3)
                    return false;
            }
        }

        return false;
    }

    private boolean isBestPair(List<ArrayList<String>> current_data, String s) {
        ArrayList<Character> l = new ArrayList<>();
        l.add('3');l.add('4');l.add('5');l.add('6');l.add('7');l.add('8');l.add('9');l.add('X');
        l.add('J');l.add('Q');l.add('K');l.add('A');l.add('L');
        int index = l.indexOf(s.charAt(0));
        String card = s.substring(2);
        if(!support_functions.isPairTriple(s)) return false;
        if(card.charAt(0) == 'L' && card.charAt(1) == 'H') return true;
      //  else if(number_of_player < 3 && index >= 8) return true;
//        else if (hand.size() < 6) return true;

        for (int i = index+1; i < 13;i++){
            if(current_data.get(i).size() <= 2)
                return false;
        }

        return true;
    }

    private boolean isBestSequences(List<ArrayList<String>> current_data, String s) {
        ArrayList<Character> l = new ArrayList<>();
        l.add('3');l.add('4');l.add('5');l.add('6');l.add('7');l.add('8');l.add('9');l.add('X');
        l.add('J');l.add('Q');l.add('K');l.add('A');
        if(!support_functions.isSequence(s)) return false;
        String sample = s;
        String last = sample.substring(sample.length()-2);
        if(last.charAt(0) == 'A' && last.charAt(1) == 'H') return  true;
        else if(number_of_player < 3 && last.charAt(0) == 'A') return true;
        //  else if (hand.size() < 6) return true;

        while(sample.length() != 0){
            String card = sample.substring(0,2);
            int index = l.indexOf(card.charAt(0));
            current_data.get(index).add(card);
            sample = sample.substring(2);
            if (index + s.length()/2 >= 12 && current_data.get(index).size() == 4)
                return true;
        }
        return s.length() / 2 >= 5;
    }


    private void upgradeKnowledge(Map<String, ArrayList> players_data) {
        ArrayList<Character> l1 = new ArrayList<>();
        l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
        l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
        for (int i =0; i < l1.size();i++)
            knowledge.add(new ArrayList<String>());
        for (Map.Entry<String,ArrayList> entry: players_data.entrySet()){
            List<String> current = entry.getValue();
            if (current.size() != 13) {
                if(card_left == 0){card_left = 13 - current.size();}
                else if (card_left > (13 - current.size())) card_left = 13 - current.size();
            }
            for(String card: current){
                char x = card.charAt(0);
                knowledge.get(l1.indexOf(x)).add(card);
            }
        }
        // sort knowledge
        for (List x: knowledge) Collections.sort(x, new sort());
    }

    // If the number of sequences is high =  number of player*(3) --> expect there is no sequence left
    // If the number of pair is high = number of player*(4) --> expect there is no pair
    private void checkForPotentialWin(Client c) {
        HashMap<String,ArrayList<String>> data = new HashMap(c.getListOfThePlayer());
        int total_number_of_sequences = 0, total_number_of_pairs = 0;
        // get the strategies play the most efficiency

        List<String> hotGuns = support_functions.findAGun(hand);
        if(hotGuns.size() > 0){
            for (String card: hotGuns)
                hand.remove(card);
        winning_meld.add(hotGuns.get(0));
        }
        List <String> trashes = new ArrayList<>();
        List<String> first_Strategy =  support_functions.findAllSequencesThenPairs(new ArrayList<>(hand));
        List<String> second_Strategy = support_functions.findAllPairsThenSequences(new ArrayList<>(hand));
        if(first_Strategy.size() >= second_Strategy.size()) {trashes = second_Strategy; strategy = 2;}
        else {strategy = 1; trashes = first_Strategy;}

        if (trashes.size() == 1)
            potential = true;
        else if (trashes.size() < 3)
            potential = checkForHighRankCards(trashes);

        if(trashes.size() == c.getHand().size())
            potential = true;

        winning_meld = new ArrayList<>();
        if(strategy == 1 && potential){
            if(support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)).size() > 0){
                winning_meld.addAll(support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)).subList(0,support_functions.num_sequences));
                for (String str: winning_meld){
                    while (str.length() >0){
                        hand.remove(str.substring(0,2));
                        str = str.substring(2);
                    }
                }}
            winning_meld.addAll(support_functions.findPairsAndTriples(new ArrayList<>(hand)));
            winning_meld.addAll(trashes);
        }
        else if( strategy == 2 && potential){
            if(support_functions.findPairsAndTriples(new ArrayList<>(hand)).size() > 0){
                winning_meld.addAll(support_functions.findPairsAndTriples(new ArrayList<>(hand)).subList(0,support_functions.num_pairs));
                for (String str: winning_meld){
                    while (str.length() >0){
                        hand.remove(str.substring(0,2));
                        str = str.substring(2);
                    }
                }}
            winning_meld.addAll(support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)));
            winning_meld.addAll(trashes);
        }

    }

    private String[] playTheSmallestMeld(Client c) {
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
        int strategy = 0;
        if(first_Strategy.size() <= second_Strategy.size()){
            remove_cards = first_Strategy;
            strategy = 1;}
        else{
            remove_cards = second_Strategy;
            strategy = 2;}
        // remove cards is the list of trash cards
        // Decide what to play singles, pairs, sequences
        List<String> sequences = new ArrayList<>();
        List<String> pairs = new ArrayList<>();
        //    System.out.println(hand + "---" + strategy);
        List<String> sample = new ArrayList<>(hand);
       // System.out.println(support_functions.num_sequences);
        if(strategy == 1){
            if(support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)).size() > 0){
                sequences = support_functions.findEfficientlyAllSequences(new ArrayList<>(hand)).subList(0,support_functions.num_sequences);
                for (String pair: sequences){
                    while(!pair.equals("")){
                        String str = pair.substring(0,2);
                        pair = pair.substring(2);
                        sample.remove(str);
                    }
                }}
            pairs = support_functions.findPairsAndTriples(new ArrayList<>(sample));
        }
        else{
            if(support_functions.findPairsAndTriples(new ArrayList<>(hand)).size() > 0){
                pairs = support_functions.findPairsAndTriples(new ArrayList<>(hand)).subList(0,support_functions.num_pairs);
                for (String pair: pairs){
                    while(!pair.equals("")){
                        String str = pair.substring(0,2);
                        pair = pair.substring(2);
                        sample.remove(str);
                    }
                }
            }
            sequences = support_functions.findEfficientlyAllSequences(new ArrayList<>(sample));
        }
        /*System.out.println("strategy" + strategy + " sequences" + sequences + " --- pairs " + pairs);*/
        int card_left = getKnowledge();
        int toRemove; boolean special = false;
        if (sequences.size()  != 0 || pairs.size() != 0){
            if(card_left == 1){
                if (sequences.size() >= 2 && sequences.get(0).length() == sequences.get(1).length()) toRemove = 2;
                else if (pairs.size()  > sequences.size() +1) toRemove = 3;
                else
                    toRemove = support_functions.theSmallestMeld(new ArrayList<>(),sequences, pairs);

            }
            else if (card_left == 2){
                if (remove_cards.size() >= 2 && support_functions.isLowMeld(remove_cards.get(0))){
                    toRemove = 1;}
                else  {toRemove = support_functions.theSmallestMeld(remove_cards,sequences, new ArrayList<>());}
            }
            else{
                toRemove = support_functions.theSmallestMeld(remove_cards,sequences,pairs);
            }
        }
        else{ special = true; toRemove = 1;}
        //.out.println("number to remove is " + toRemove);

        // System.out.println(support_functions.compareSingleCard("3C","3C"));
        if(toRemove == 1){
            // is special then remove at the last
            if(special && card_left == 1){
                c.getHand().remove(remove_cards.get(remove_cards.size()-1));
                return new String[]{remove_cards.get(remove_cards.size()-1)};
            }
            else{
                c.getHand().remove(remove_cards.get(0));
                return new String[]{remove_cards.get(0)};}
        }
        else if (toRemove == 2){
            String str = sequences.get(0);
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
        return null;
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
    // 1st check for the number of player and check for the rank of the meld
    //
    private boolean HaveToPlay(Client c,String[] current_meld){
        boolean high_rank = support_functions.checkHighRankMeld(current_meld);
        boolean decide = true;
        if(high_rank)
            decide = false;
        int num_players =c.getNumber_of_player();
        int num = 0;
        for (Map.Entry mapElement : c.getListOfThePlayer().entrySet()) {
            String key = (String)mapElement.getKey();
            if(c.getListOfThePlayer().get(key).size() > 7)
                num++;
        }
        if(num >= num_players/2){
            return true;}
        if(c.getHand().size() < 8)
            decide = true;
        return decide;
    }

    private boolean checkForHighRankCards(List<String> str){
        ArrayList<Character> l = new ArrayList<>();
        l.add('3');l.add('4');l.add('5');l.add('6');l.add('7');l.add('8');l.add('9');l.add('X');
        l.add('J');l.add('Q');l.add('K');l.add('A');l.add('L');
        for (String card: str)
            if (l.indexOf(card.charAt(0)) < 7)
                return false;
        return true;

    }
    private boolean allIsPair(List<String> melds){
        for (int i =0; i < melds.size()-1;i++){
            if(melds.get(i).length() == 2 || melds.get(i).length() != 4)
                return false;
            if (melds.get(i).charAt(0) != melds.get(i).charAt(2))
                return false;
        }
        return true;
    }
}