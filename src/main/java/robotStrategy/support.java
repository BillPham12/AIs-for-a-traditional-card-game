package robotStrategy;

import game.Meld;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.net.ServerSocket;
import java.util.*;

public class support {
    int num_pairs = 0;
    int num_sequences = 0;
    // if b - a == 1 return true;
    // else return false;
    // a and b is a card
    public boolean compareSequence(String card_a, String card_b) {
        ArrayList<Character> l1 = new ArrayList<>();
        for (int i = 51; i < 58; i++) l1.add((char) i);
        l1.add('X');
        l1.add('J');
        l1.add('Q');
        l1.add('K');
        l1.add('A');
        return l1.indexOf(card_b.charAt(0)) - l1.indexOf(card_a.charAt(0)) == 1;
    }

    // if b - a == 0
    // return true;
    // else return false;
    public boolean comparePair(String a, String b) {
        ArrayList<Character> l1 = new ArrayList<>();
        for (int i = 51; i < 58; i++) l1.add((char) i);
        l1.add('X');
        l1.add('J');
        l1.add('Q');
        l1.add('K');
        l1.add('A');
        l1.add('L');
        return l1.indexOf(b.charAt(0)) - l1.indexOf(a.charAt(0)) == 0;
    }

    // check the coming meld is the sequence or not
    public boolean isHotGun(String[] coming_meld) {
        char[] l = new char[]{'3', '4', '5', '6', '7', '8', '9', 'X', 'J', 'Q', 'K', 'A', 'L'};

        List<Character> list = new ArrayList<>();
        for (char x: l) list.add(x);
        if ( coming_meld == null || coming_meld.length % 2 !=0 || coming_meld.length < 4) return false;

        if(coming_meld.length == 4){
            int x = 99;
            for (String card: coming_meld){
                if (x == 99) {
                    x = list.indexOf(card.charAt(0));}
                else{
                    if (x != list.indexOf(card.charAt(0)))
                        return false;
                }
            }
            return true;
        }
        else{
            for( int i =0; i < coming_meld.length; i+=2){
                String x = coming_meld[i], y = coming_meld[i+1];;
                if(list.indexOf(x.charAt(0)) != list.indexOf(y.charAt(0)))
                    return false;
            }
            return true;
        }
    }
    public List<String> findAGun(List<String> hand){
        List<String> output = new ArrayList<>();
        List<String> pairs = findPairsAndTriples(new ArrayList<>(hand));
        for (String pair: pairs){
            if (isHotGun(pair)){
                output.add(pair);
                return output;
            }
        }
        Collections.sort(pairs, new sort());
        List<String> hot = new ArrayList<>();
        Set<String> set = new HashSet<>();
        int index = 0;
        for (int i =0; i < pairs.size()-1;i++){
            String x = pairs.get(i).substring(0,2);
            String y = pairs.get(i+1).substring(0,2);
            if(compareSequence(x,y)){
                if(set.add(x))hot.add(pairs.get(i).substring(0,4));
                if(set.add(y)) hot.add(pairs.get(i+1).substring(0,4));
            }
            else{
                if(hot.size() >= 3){
                    String s = "";
                    for (String pair: hot){
                        while(pair.length() > 0){
                            s += pair.substring(0,2);
                            pair = pair.substring(2);
                        }
                    }
                    output.add(s);
                    return output;
                }
                else{
                    hot = new ArrayList<>(); set = new HashSet<>();
                }
            }

        }
        if(hot.size() >= 3){
            String s = "";
            for (String pair: hot){
                while(pair.length() > 0){
                    s += pair.substring(0,2);
                    pair = pair.substring(2);
                }
            }
            output.add(s);
            return output;
        }
        else output = new ArrayList<>();
        return output;
    }

    public boolean isHotGun(String meld) {
        String[] coming_meld = new String[meld.length()/2];
        int i =0;
        while(meld.length() != 0)
        {
            String card = meld.substring(0,2);
            coming_meld[i] = card;
            meld = meld.substring(2);
            i = i +1;
        }
        return isHotGun(coming_meld);
    }

    // check the coming meld is the sequence or not
    public boolean isSequence(String[] coming_meld) {
        char[] l = new char[]{'3', '4', '5', '6', '7', '8', '9', 'X', 'J', 'Q', 'K', 'A', 'L'};
        int last = coming_meld.length -1;
        for (int i = 0; i < last; i++){
            if (!compareSequence(coming_meld[i],coming_meld[i+1]))
                return false;
        }
        return coming_meld.length >= 3;
    }



    public boolean isSequence(String meld) {
        String[] coming_meld = new String[meld.length()/2];
        int i =0;
        while(meld.length() != 0)
        {
            String card = meld.substring(0,2);
            coming_meld[i] = card;
            meld = meld.substring(2);
            i = i +1;
        }
        return isSequence(coming_meld);
    }

    // check the coming meld is a pair or a triple
    public boolean isPairTriple(String[] coming_meld) {
        if(coming_meld.length < 2) return false;
        char con = coming_meld[0].charAt(0);
        for (int i = 0; i < coming_meld.length - 1; i++)
            if (con != coming_meld[i].charAt(0))
                return false;
        return true;
    }
    public boolean isPairTriple(String meld) {
        String[] coming_meld = new String[meld.length()/2];
        int i =0;
        while(meld.length() != 0)
        {
            String card = meld.substring(0,2);
            coming_meld[i] = card;
            meld = meld.substring(2);
            i = i +1;
        }
        return isPairTriple(coming_meld);
    }

    // check 2 sorted lists as input
    // return true if sequence > current_meld
    public boolean checkPotentialMeld(String meld, String[] current_meld) {
        String[] seq = new String[meld.length() / 2];
        int i = 0;
        while (!meld.equals("")) {
            seq[i] = meld.substring(0, 2);
            meld = meld.substring(2);
            i++;
        }
        int x = current_meld.length - 1;
        if (seq.length == current_meld.length) {
            return compareSingleCard(seq[x], current_meld[x]);
        } else return false;
    }

    // In case we know that check potential sequences is not in case.
    // then check the sequences, is it potentially a good sequence to play off.
    // if it is bigger then choose it.
    public boolean checkSequences(String sequence, String[] current_meld) {
        String[] seq = new String[sequence.length() / 2];
        int i = 0;
        while (!sequence.equals("")) {
            seq[i] = sequence.substring(0, 2);
            sequence = sequence.substring(2);
            i++;
        }
        int x = current_meld.length - 1;
        if(seq.length >= current_meld.length) {
            for (i = x; i < seq.length; i++) {
                if (compareSingleCard(seq[i], current_meld[x])) return true;
            }
        }
        return false;
    }
    // Convert a string input into an array of String
    public String[] convert(String s) {
        String[] seq = new String[s.length() / 2];
        int i = 0;
        while (!s.equals("")) {
            seq[i] = s.substring(0, 2);
            s = s.substring(2);
            i++;
        }
        return seq;
    }
    // convert aggressively a string input into an array of String
    // play a sequences [x] > current meld by breaking the sequences [x] into pieces
    public String[] convertAggressively(String sequence,String[] current_meld) {
        String[] seq = new String[sequence.length() / 2];
        int i = 0;
        while (!sequence.equals("")) {
            seq[i] = sequence.substring(0, 2);
            sequence = sequence.substring(2);
            i++;
        }
        int x = current_meld.length - 1;
        int index = 0;
        if(seq.length >= current_meld.length) {
            loop: for (i = x; i < seq.length; i++){
                if (compareSingleCard(seq[i], current_meld[x])){
                    index = i;
                    break loop;
                }
            }
        }
        String[] out = new String[current_meld.length];
        i = 0;
        for(x =index-(current_meld.length-1); x < index+1;x++){
            out[i] = seq[x];
            i++;
        }
        return out;
    }

    // convert smartly a string input into an array of String
    // play a sequences [x] > current meld by breaking the sequences [x] into pieces
    public String[] convertSmartly(String sequence,String[] current_meld) {
        String[] seq = new String[sequence.length() / 2];
        String sample = sequence;
        int i = 0;
        while (!sequence.equals("")) {
            seq[i] = sequence.substring(0, 2);
            sequence = sequence.substring(2);
            i++;
        }
        int x = current_meld.length - 1;
        int index = 0;
        if(seq.length >= current_meld.length) {
            loop: for (i = x; i < seq.length; i++){
                if (compareSingleCard(seq[i], current_meld[x])){
                    index = i;
                    break loop;
                }
            }
        }
        String[] out = new String[current_meld.length];
        if (seq.length - current_meld.length >= 2)
            return new String[]{"00"};
        i = 0;
        for(x =index-(current_meld.length-1); x < index+1;x++){
            out[i] = seq[x];
            i++;
        }
        return out;
    }

    // return true if a > b
    // else return false
    public boolean compareSingleCard(String a, String b){
        return comp(a, b) > 0;
    }
    // support function for compare
    private int comp(String a, String b) {
        ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
        ArrayList<Character> l1 = new ArrayList<>();
        l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
        l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
        if(a.charAt(0) == b.charAt(0)) return (l.indexOf(a.charAt(1)) - l.indexOf(b.charAt(1)));
        else if (l1.contains(a.charAt(0)) && !l1.contains(b.charAt(0))) return 1;
        else if (!l1.contains(a.charAt(0)) && l1.contains(b.charAt(0))) return -1;
        else return l1.indexOf(a.charAt(0)) - l1.indexOf(b.charAt(0));
    }

    // find all potential sequences from hand and store them in an array list
    public List<String> findEfficientlyAllSequences(List<String> hand){
        ArrayList<String> str = new ArrayList<>(); Set<String> set = new HashSet<>();
        List<String> sample = new ArrayList<>(hand);
        String s = "";
        int num =  0;
        while(true){
            for (int i =0; i < sample.size() -1;i++){
                if(s.equals("")) {
                    if (set.add(sample.get(i)))
                        s += sample.get(i);
                }

                if (compareSequence(sample.get(i),sample.get(i+1))){
                    if(set.add(sample.get(i+1)))
                        s += sample.get(i+1);
                }
                else if (i+2 < sample.size() && compareSequence(sample.get(i),sample.get(i+2))){
                    if(set.add(sample.get(i+2))){
                        s += sample.get(i+2); i = i+1;}
                }
                else if (i+3 < sample.size() && compareSequence(sample.get(i),sample.get(i+3))){
                    if(set.add(sample.get(i+3))){
                        s += sample.get(i+3); i = i+2;}
                }
                else if (i+4 < sample.size() && compareSequence(sample.get(i),sample.get(i+4))){
                    if(set.add(sample.get(i+4))){
                        s += sample.get(i+4); i = i+3;}
                }
                else{
                    if(s.length() >= 6) str.add(s);
                    s = "";}
            }
            if(s.length() >= 6) str.add(s);
            num++;
            // return 3 times to make sure that no sequences are missing.
            if (num  < 3){
                s = "";
                ArrayList<String> list = new ArrayList<>(str);
                for (String meld: list){
                    while (meld.length() > 2){
                        sample.remove(meld.substring(0,2));
                        meld = meld.substring(2);
                    }
                }
            }
            else break;
        }
        // trashed cards
        sample = new ArrayList<>(hand);
        for (String seq: str){
            String x =seq;
            while (x.length() > 0){
                sample.remove(x.substring(0,2));
                x = x.substring(2);
            }
        }
        List<String> output = new ArrayList<>();

        for (String seq: str){
            List<String> cards = new ArrayList<>();

            String x =seq;
            // store cards in the sequence to a list
            while (x.length() > 0){
                cards.add(x.substring(0,2));
                x = x.substring(2);
            }

            // check for the possible removed index
            int first_index = 0, second_index = 3;
            if (seq.length()/2 > 3 && sample.size() > 0){
                first_index = seq.length()/2 - 3;
                second_index += first_index;
                List<String> seeking = new ArrayList<>(sample);
                for (int i =0; i< first_index; i++){
                    seeking.add(cards.get(i));
                }
                Collections.sort(seeking, new sort());
                // find the possible sequence from the combination of trash cards and the cards in the meld
                List<String> new_sequences = findEfficientlyAllSequences(seeking);
                for (String new_seq: new_sequences){
                    String remove =new_seq;
                    while(remove.length()> 0){
                        String card = remove.substring(0,2);
                        remove = remove.substring(2);
                        if(cards.contains(card)) cards.remove(card);
                        else if (sample.contains(card)) sample.remove(card);
                    }
                }
                // if there is a new_sequence. then update the output
                if (new_sequences.size() != 0){
                    output.addAll(findEfficientlyAllSequences(cards));
                    output.addAll(new_sequences);
                }
                else output.add(seq);
            }
            else output.add(seq);
        }
        Collections.sort(output, new sort());
        return output;
    }

    // find all potential sequences from hand and store them in an array list
    public List<String> findASequences(List<String> hand,int length){
        ArrayList<String> str = new ArrayList<>(); Set<String> set = new HashSet<>();
        String s = "";
        for (int i =0; i < hand.size() -1;i++){
            if(s.equals("")) {
                if (set.add(hand.get(i)))
                    s += hand.get(i);
            }

            if (compareSequence(hand.get(i),hand.get(i+1))){
                if(s.length()/2 == length -1 && i+2 < hand.size()  && comparePair(hand.get(i+1),hand.get(i+2))){
                    if(set.add(hand.get(i+2))){
                        s += hand.get(i+2); i= i+1;}
                }
                else if(set.add(hand.get(i+1)))
                    s += hand.get(i+1);
            }
            else if (i+2 < hand.size() && compareSequence(hand.get(i),hand.get(i+2))){
                if(set.add(hand.get(i+2))){
                    s += hand.get(i+2); i = i+1;}
            }
            else{
                if(s.length() >= 6) str.add(s);
                s = "";}
        }
        if(s.length() >= 6) str.add(s);
        return str;
    }

    // find all potential pairs from hand and store them in an array list
    public List<String> findPairsAndTriples(List<String> hand){
        ArrayList<String> str = new ArrayList<>(); Set<String> set = new HashSet<>();
        String s = "";
        for(int i =0; i < hand.size()-1;i++){
            if(s.equals("")) {if (set.add(hand.get(i))) s += hand.get(i);}
            if (comparePair(hand.get(i),hand.get(i+1))){
                set.add(hand.get(i+1));
                s += hand.get(i+1);
            }
            else{
                if(s.length() >= 4) str.add(s);
                s = "";}

        }
        if(s.length() >= 4) str.add(s);
        return str;
    }
    // Find all pairs then find all sequences
    // FIND all pairs from 1 to max -> then find all sequences
    // Then choose the best one. 
    public List<String> findAllPairsThenSequences(List<String> hand) {
        // find all pairs and triples
        List<String > pairs = findPairsAndTriples(new ArrayList<>(hand));
        num_pairs = 0;
        List<String > output = null;
        int the_lowest_one = 99;
        for(int i =1; i < pairs.size()+1;i++){
            List<String > sample = new ArrayList<>(hand);
            List<String> sub_pairs = pairs.subList(0,i);
            for (String pair: sub_pairs){
                while(!pair.equals("")){
                   String str = pair.substring(0,2);
                    pair = pair.substring(2);
                    sample.remove(str);
                }
            }
            // find all sequences first
            List<String > seqs = findEfficientlyAllSequences(sample);
            for(String seq: seqs){
                while(!seq.equals("")){
                    String str = seq.substring(0,2);
                    seq = seq.substring(2);
                    sample.remove(str);
                }
            }
                if (the_lowest_one > sample.size()){
                    num_pairs = i;
                    the_lowest_one = sample.size();
                    output = sample;
                }
            }
        //System.out.println("Testing" + output);
        if(output == null) return hand;
        return output;
    }
    // Find all Sequences then find all pairs
    public List<String> findAllSequencesThenPairs(List<String> hand) {
        // find all sequences first
        List<String > sequences = findEfficientlyAllSequences(new ArrayList<>(hand));
        num_sequences = 0;
        List<String > output = null;
        int the_lowest_one = 99;
        for(int i =1; i < sequences.size()+1;i++) {
            List<String> sample = new ArrayList<>(hand);
            List<String> sub_sequences = sequences.subList(0, i);
            for (String seq : sub_sequences) {
                while (!seq.equals("")) {
                    String str = seq.substring(0, 2);
                    seq = seq.substring(2);
                    sample.remove(str);
                }
            }
            // find all pairs and triples
            List<String> pairs = findPairsAndTriples(sample);
            for (String pair : pairs) {
                while (!pair.equals("")) {
                    String str = pair.substring(0, 2);
                    pair = pair.substring(2);
                    sample.remove(str);
                }
            }
            if (the_lowest_one > sample.size()) {
                num_sequences = i;
                the_lowest_one = sample.size();
                output = sample;
            }
        }
        //System.out.println("Testing" + output);
        if(output == null) return hand;
        return output;
    }
    // convert sequences/pairs into cards
    public List convertMeldToCards(List<String> hand, String meld){
        List<String> x = new ArrayList<String>();
            while(!meld.equals("")){
                String s = meld.substring(0,2);
                hand.remove(s);
                meld = meld.substring(2);
                x.add(s);
            }
        return x;
    }
    // find a single card in a sequences
    public String findASingleCardInAMeld(String seq,String card){
        String s; int length = seq.length();
        String head = seq.substring(0,2), tail = seq.substring(length-2,length);
        if (length == 5 || length== 3) return null;
        if(compareSingleCard(head,card)) return head;
        else if (compareSingleCard(tail,card)) return tail;
        else return null;
    }



    // compare smallest sequences, pairs, single cards to play them first.
    public int theSmallestMeld(List<String> remove_cards, List<String> smallest_seq, List<String> smallest_pair) {
            if(remove_cards.size() == 0){
                if(smallest_seq.size() == 0) return 3;
                else if (smallest_pair.size() == 0) return 2;
                else {
                    if(smallest_pair.get(0).substring(0,2).equals(smallest_seq.get(0).substring(0,2))) return 2;
                    else if(compareSingleCard(smallest_pair.get(0).substring(0,2),smallest_seq.get(0).substring(0,2))) return 2;
                    else return 3;
                }
            }
            else if (smallest_pair.size() == 0){
                if(smallest_seq.size() == 0) return 1;
                else if (remove_cards.size() == 0) return 2;
                else {
                    if(smallest_seq.get(0).substring(0,2).equals(remove_cards.get(0).substring(0,2)))  return 2;
                    else if(compareSingleCard(smallest_seq.get(0).substring(0,2),remove_cards.get(0))) return 1;
                    else return 2;
                }
            }
            else if (smallest_seq.size() == 0){
                if(smallest_pair.size() == 0) return 1;
                else if (remove_cards.size() == 0) return 3;
                else {
                    if(smallest_pair.get(0).substring(0,2).equals(remove_cards.get(0).substring(0,2)))  return 3;
                    else if(compareSingleCard(smallest_pair.get(0).substring(0,2),remove_cards.get(0))) return 1;
                    else return 3;
                }
            }
            else{
                int min;
                if(smallest_pair.get(0).substring(0,2).equals(smallest_seq.get(0).substring(0,2)))  min = 2;
                else if(compareSingleCard(smallest_pair.get(0).substring(0,2),smallest_seq.get(0).substring(0,2))) min =2;
                else min = 3;

                if(min == 2) {
                    if(smallest_seq.get(0).substring(0,2).equals(remove_cards.get(0).substring(0,2)))  return 2;
                    else if (compareSingleCard(smallest_seq.get(0).substring(0, 2),remove_cards.get(0).substring(0, 2))) return 1;
                    else return 2;
                }
                else {
                    if(smallest_pair.get(0).substring(0,2).equals(remove_cards.get(0).substring(0,2)))  return 3;
                    if (compareSingleCard(smallest_pair.get(0).substring(0, 2),remove_cards.get(0).substring(0, 2))) return 1;
                    else return 3;
                }

            }
    }

    public boolean isLowMeld(String s) {
        char[] l = new char[]{ '3', '4', '5', '6', '7', '8', '9', 'X', 'J', 'Q', 'K', 'A', 'L'};
        int i =0;
        if(s.length() == 2){
            loop: for (char x : l){
                if(x == s.charAt(0)) break loop;
                else i = i +1;}
            return i < 8;
        }

        loop: for (char x : l){
            if(x == s.charAt(0)) break loop;
            else i = i+1;
        }
        return i < 8;
    }
    public boolean isVeryLowMeld(String s) {
        char[] l = new char[]{ '3', '4', '5', '6', '7', '8', '9', 'X', 'J', 'Q', 'K', 'A', 'L'};
        int i =0;
        loop: for (char x : l){
            if(x == s.charAt(0))
                break loop;
            else i = i +1;
        }
        return i <= 2;
    }

    public boolean isLowMeldForRational(String s) {

        char[] l = new char[]{ '3', '4', '5', '6', '7', '8', '9', 'X', 'J', 'Q', 'K', 'A', 'L'};
        int i =0;
        // less than 10 for a pair or a single card
        if(s.length() == 2 || s.length() == 4){
            loop: for (char x : l){
                if(x == s.charAt(0)) break loop;
                else i = i +1;}
            return i < 7;
        }
        // for a sequences
        loop: for (char x : l){
            if(x == s.charAt(0)) break loop;
            else i = i+1;
        }
        return i < 8;
    }
    public boolean aggresivelyCheckPotentialMeld(String meld, String[] current_meld) {
        String[] seq = new String[meld.length() / 2];
        int i = 0;
        while (!meld.equals("")) {
            seq[i] = meld.substring(0, 2);
            meld = meld.substring(2);
            i++;
        }
        int x = current_meld.length - 1;
        if (seq.length > current_meld.length) {
            return compareSingleCard(seq[x], current_meld[x]);
        } else return false;
    }

    public String[] convertAggressivelyPair(String s, int size) {
        String[] seq = new String[size];
        int i = 0;
        while (i < size) {
            seq[i] = s.substring(0, 2);
            s = s.substring(2);
            i++;
        }
        return seq;
    }

    public boolean checkHighRankMeld(String[] current_meld) {
        int length = current_meld.length;
        if(current_meld.length == 1) return !isLowMeld(current_meld[0]);
        else return !isLowMeld(current_meld[length-1]);
    }

    public boolean isBlackPig(String[] current_meld) {
        ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
        ArrayList<Character> l1 = new ArrayList<>();
        l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
        l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
        if(current_meld != null && current_meld.length == 1 && current_meld[0].charAt(0) == 'L'){
            int index = l.indexOf(current_meld[0].charAt(1));
            if(index <= 1) return true;
        }
        return false;
    }
    public boolean isRedPig(String[] current_meld) {
        ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
        ArrayList<Character> l1 = new ArrayList<>();
        l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
        l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
        if(current_meld != null  && current_meld.length == 1 && current_meld[0].charAt(0) == 'L'){
            int index = l.indexOf(current_meld[0].charAt(1));
            if(index > 1) return true;
        }
        return false;
    }

    public boolean isPigs(String[] current_meld) {
        ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
        ArrayList<Character> l1 = new ArrayList<>();
        l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
        l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');

        if(current_meld != null && current_meld.length >= 4 && current_meld[0].charAt(0) == 'L')
            return true;
        return false;
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