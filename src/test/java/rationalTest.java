import client_server.AggressivePlayer;
import client_server.PatientAndRationalPlayer;
import client_server.RationalPlayer;
import junit.framework.TestCase;
import org.junit.Assert;
import robotStrategy.Rational;
import robotStrategy.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class rationalTest extends TestCase {
    public void testMoves() {
        RationalPlayer rationalPlayer= new RationalPlayer("RATIONALITY");
        // if it is its turn, then play
        // if it is its first turn, play the lowest ranking cards

        // play a single smallest card
        String[] input = {"3H","4S","4H","5S","6H","6S"};
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3H","4S","5S","6S"},rationalPlayer.play());

        // play a smallest pair
        input = new String[]{"3C", "3S", "6C", "6H", "7H", "9S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3C"},rationalPlayer.play());

        input = new String[]{"3C", "3S", "4H", "6H", "7H", "9S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3C"},rationalPlayer.play());


        //play a smallest triple
        input = new String[]{"3S", "3H", "3D", "7H", "7S", "7C"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3D","3H"},rationalPlayer.play());

        // play a smallest sequence
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);

        Assert.assertArrayEquals(new String[]{"3H","4C","5C","6C","7H","8H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS","QS","KH","AH"},rationalPlayer.play());

        // play a single card
        input = new String[]{"3H", "4C", "6C", "7H", "8H","JS","QS","KH","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3H"},rationalPlayer.play());

        // play a single card
        input = new String[]{"3H", "5C", "6C", "7H", "8H","JS","QS","KH","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"5C","6C","7H","8H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS","QS","KH","AH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"3H"},rationalPlayer.play());

        // when enemy has 2 cards left, will not play a pair
        rationalPlayer.setListOfThePlayer("ENEMY","XS;XH;XS;XH;XS;XH;XS;XH;XS;XH;XH");
        input = new String[]{"3H", "3S","3C","4H","5H","JS"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","4H","5H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"3C"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"3H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS"},rationalPlayer.play());

        input = new String[]{"7S", "7H","JS","JC","KH","KC"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"7S"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"7H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JC"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"KC","KH"},rationalPlayer.play());



        input = new String[]{"7S", "7H","9H","XC","KH","KC"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"9H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"7S"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"7H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"XC"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"KC","KH"},rationalPlayer.play());


        // WHEN enemy has 1 cards left, will not play a single card
        rationalPlayer.clearList();
        rationalPlayer.setListOfThePlayer("ENEMY","XH");
        input = new String[]{"3H", "3S","3C","4H","5H","JS"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","4H","5H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"3C","3H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS"},rationalPlayer.play());

        rationalPlayer.clearList();
        rationalPlayer.setListOfThePlayer("ENEMY","XH");
        input = new String[]{"3H", "3S","4C","4H","5H","JS"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"4C","4H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"5H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS"},rationalPlayer.play());

        rationalPlayer.clearList();
        rationalPlayer.setListOfThePlayer("ENEMY","XH");
        input = new String[]{"3H", "3S","4C","4H","5H","5S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"4C","4H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"5S","5H"},rationalPlayer.play());


    }
    /* efficiently use a single card to play over
     if it wins play a small ranking card.
     otherwise pass its turn
    */
    public void test_competitive_single_card() {
        RationalPlayer rationalPlayer= new RationalPlayer("RATIONALITY");
        // if it is its first turn, play the lowest ranking cards

        String[] str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};
        rationalPlayer.setRounds(str);
        // play a 4 of heart
        rationalPlayer.clearHand();
        String[] input = {"3H","4S","4H","5S","6H","6S"};
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("AGGRESSIVE");
        rationalPlayer.setCurrentMeld("3S");
        Assert.assertArrayEquals(new String[]{"4H"},rationalPlayer.play());


        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7H", "3S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("7S");
        Assert.assertArrayEquals(new String[]{"7H"},rationalPlayer.play());

        //Can't play
        str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};
        rationalPlayer.setRounds(str);
        input = new String[]{"3S", "3H", "3D", "7H", "7S", "LS"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("LC");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());

        // play an ace of diamond
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AD"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"AD"},rationalPlayer.play());

        // ADVANCED PLAY RULES
       str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};

        rationalPlayer.setRounds(str);
        Assert.assertSame(rationalPlayer.getSpecialEnemy(),"PATIENCE");
        // NOT COMPETITIVE WITH UPPER HAND
        input = new String[]{"4H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AD"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());


        // IF IT HAS POTENTIAL TO WIN THE GAME THEN IT PLAYS AGGRESSIVE
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AD"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.updateKnowledge("PATIENCE","KCKSKD");
        rationalPlayer.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"AD"},rationalPlayer.play());
        rationalPlayer.setCurrentMeld(" ");
        Assert.assertArrayEquals(new String[]{"3H", "4C", "5C", "6C", "7H", "8H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS","QS","KH"},rationalPlayer.play());

        // IF THE TURN DOESN'T HAVE THE UPPER HAND --> PLAY
        rationalPlayer = new RationalPlayer("RATIONALITY");
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AD"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        str= new String[]{"AGGRESSIVE","RATIONALITY"};
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("AGGRESSIVE");
        rationalPlayer.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"AD"},rationalPlayer.play());
    }

    /* efficiently use a pair card to play over
        if it wins play a pair
      otherwise pass its turn
      */
    public void test_competitive_pair() {
        RationalPlayer rationalPlayer= new RationalPlayer("RATIONALITY");
        // if it is its first turn, play the lowest ranking cards
        String[] str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};

        rationalPlayer.setRounds(str);
        // play a pair of 3
        String[] input = {"3D","3H","4H","5S","6H","6S"};
        rationalPlayer.clearHand();
        rationalPlayer.setNextTurn("AGGRESSIVE");
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("3S3C");
        Assert.assertArrayEquals(new String[]{"3D","3H"},rationalPlayer.play());

        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7C", "7D"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("7S7H");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());


        //Can play
        input = new String[]{"3S", "3H", "3D", "7H", "AH", "AS"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("ACAD");
        Assert.assertArrayEquals(new String[]{"AS","AH"},rationalPlayer.play());

        // ADVANCED PLAY RULES
        str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};

        rationalPlayer.setRounds(str);
        Assert.assertEquals(rationalPlayer.getSpecialEnemy(),"PATIENCE");
        // IF IT HAS POTENTIAL TO WIN THE GAME THEN IT PLAYS AGGRESSIVE
        input = new String[]{"3C","3H","JS","JC","KC","KH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setCurrentMeld("XHXC");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());


        // NOT COMPETITIVE WITH UPPER HAND
        input = new String[]{"9H","8H","JS","JC","KC","KH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setCurrentMeld("XHXC");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());

        // IF THE TURN DOESN'T HAVE THE UPPER HAND --> PLAY
        rationalPlayer = new RationalPlayer("RATIONALITY");
        input = new String[]{"9H","8H","JS","JC","KC","KH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        str = new String[]{"PATIENCE","RATIONALITY"};
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setRounds(str);
        rationalPlayer.setCurrentMeld("XHXC");
        Assert.assertArrayEquals(new String[]{"JS","JC"},rationalPlayer.play());
    }
    /* efficiently use a triple card to play over
        if it wins play a triple
      otherwise pass its turn
     */
    public void test_competitive_triple() {
        RationalPlayer rationalPlayer= new RationalPlayer("RATIONALITY");
        // if it is its first turn, play the lowest ranking cards
        String[] str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};

        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("AGGRESSIVE");
        // play a pair of 3
        String[] input = {"4S","4C","4H","6H","6S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("3S3C3D");
        Assert.assertArrayEquals(new String[]{"4S","4C","4H"},rationalPlayer.play());

        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7C", "7D"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("7S7D7H");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());


        //Can play
        input = new String[]{"5S", "5C", "5D", "7H", "7S", "7C"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("3C3D3H");
        Assert.assertArrayEquals(new String[]{"5S","5C","5D"},rationalPlayer.play());

    }
    /* efficiently use a triple card to play over
        if it wins play a triple
      otherwise pass its turn
     */
    public void test_competitive_sequences() {

        RationalPlayer rationalPlayer= new RationalPlayer("RATIONALITY");
        // if it is its first turn, play the lowest ranking cards

        String[] str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};

        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("AGGRESSIVE");

        // play a sequences of 3
        String[] input = {"3C","4D","5H","5S","6H","6S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"3C","4D","5H"},rationalPlayer.play());

        // play a sequence of 4, if an element of a sequence is a part of a pair, then choose the low ranking one
        input = new String[]{"8H", "9S", "XH", "6H", "7C", "7D"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("7S8H9CXS");
        Assert.assertArrayEquals(new String[]{"7C","8H","9S","XH"},rationalPlayer.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"5H", "6C", "7H", "7S", "8S","9H"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"5H","6C","7H"},rationalPlayer.play());

        // contains a sequences of 5 and trash cards decide not to play
        input = new String[]{"XH","6H","7H","8H","9H","3C","4D"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());


        // ADVANCED PLAY RULES
        str= new String[]{"RATIONALITY","AGGRESSIVE","PATIENCE"};

        rationalPlayer.setRounds(str);
        Assert.assertEquals(rationalPlayer.getSpecialEnemy(),"PATIENCE");
        // IF IT HAS POTENTIAL TO WIN THE GAME THEN IT PLAYS AGGRESSIVE
        input = new String[]{"3C","4H","5S","QS","KC","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setCurrentMeld("XHJSQC");
        Assert.assertArrayEquals(new String[]{"QS","KC","AH"},rationalPlayer.play());
        rationalPlayer.setCurrentMeld(" ");
        Assert.assertArrayEquals(new String[]{"3C","4H","5S"},rationalPlayer.play());


        // NOT COMPETITIVE WITH UPPER HAND
        input = new String[]{"7H","8H","XC","QS","KC","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setCurrentMeld("XHJSQC");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());
        rationalPlayer.setCurrentMeld(" ");
        Assert.assertArrayEquals(new String[]{"7H"},rationalPlayer.play());


        // IF THE TURN DOESN'T HAVE THE UPPER HAND --> PLAY
        rationalPlayer = new RationalPlayer("RATIONALITY");
        input = new String[]{"XH","8H","XC","QS","KC","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        str = new String[]{"PATIENCE","RATIONALITY"};
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("PATIENCE");
        rationalPlayer.setCurrentMeld("XHJSQC");
        Assert.assertArrayEquals(new String[]{"QS","KC","AH"},rationalPlayer.play());
        rationalPlayer.setCurrentMeld(" ");
        Assert.assertArrayEquals(new String[]{"8H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"XC","XH"},rationalPlayer.play());

    }

    // see a pig going around, if it is able to kill that pig
    // do it
    // see a pig going around, if it is able to kill that pig
    // do it
    public void test_personality(){
        RationalPlayer rationalPlayer= new RationalPlayer("SPECIAL");
        // if it is its first turn, play the lowest ranking cards

        String[] str= new String[]{"HUMAN","SPECIAL"};

        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");

        // play a sequences of 3
        String[] input = {"3C","3D","5S","5H","6D","7C","8C","9C","KH","LC","LD","QS","QD"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3C","3D"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"5S","5H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"6D","7C","8C","9C"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"QS","QD"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LC","LD"},rationalPlayer.play());

        input = new String[]{"3C","3D","7C","7H","8C","8H","XS","XH","AS","AD","AH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3C","3D"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"7C","7H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"8C","8H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"AS","AD","AH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"XS","XH"},rationalPlayer.play());

        input = new String[]{"LC","LH","QS","KC"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"QS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LC","LH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"KC"},rationalPlayer.play());


        input = new String[]{"LC","LH","8H","9C","XH","3C"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"3C"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LC","LH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"8H","9C","XH"},rationalPlayer.play());
    }
    public void test_debug(){
        RationalPlayer rationalPlayer= new RationalPlayer("SPECIAL");
        // if it is its first turn, play the lowest ranking cards

        String[] str= new String[]{"HUMAN","SPECIAL"};

        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");

        String[] input = new String[]{ "4D", "4S","5H", "5D", "LC", "7S", "8H","9C","XS", "QH", "KC","AS","AC"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"4S","4D"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"5D","5H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"7S", "8H","9C","XS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"AC"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"QH", "KC","AS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LC"},rationalPlayer.play());

        input = new String[]{"8S","XS","LH"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"XS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"8S"},rationalPlayer.play());


        input = new String[]{ "4D", "4S","5H", "5D", "LC", "7S", "8H","9C","XS", "QH", "KC","AS","AC"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        // card left is 2
        rationalPlayer.setListOfThePlayer("HUMAN","XH;XH;XH;XH;XH;XH;XH;XH;XH;XH;XH");
        Assert.assertArrayEquals(new String[]{"7S", "8H","9C","XS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"QH", "KC","AS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"AC"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"4S"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"4D"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LC"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"5D","5H"},rationalPlayer.play());


        input = new String[]{"XD","JC","AC","QC","8H","QD","KS","4D","6C","5S","LD","4C","XH"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.setCurrentMeld("3S3C");
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());

        input = new String[]{"6C","6H","6S","KD","KH","JS"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"6S","6C","6H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"KD","KH"},rationalPlayer.play());

        input = new String[]{"6C","6H","KD","KH","JS"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        // hand left is 1
        rationalPlayer.setListOfThePlayer("ENEMY","XS;XH;XS;XH;XS;XH;XH;XS;XH;XS;XH;XS;");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"KD","KH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"6C","6H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"JS"},rationalPlayer.play());


        input = new String[]{"3C","4S","5S","6H","8D","8H","KC","JH","QD","AC"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"AC"},rationalPlayer.play());

        input = new String[]{"KH","KC","KD","7H","7K"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"KC","KD","KH"},rationalPlayer.play());

        input = new String[]{"3C", "3D", "4C", "5H", "7H", "9S", "KH", "LH", "XD"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"3C","4C","5H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"3D"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"7H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"9S"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"KH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"XD"},rationalPlayer.play());

        input = new String[]{"AS","KH","QS","XC"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"QS","KH","AS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"XC"},rationalPlayer.play());

        input = new String[]{"QD","6H","4H","3D","3C","5C","5H","5D"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"3C","4H","5C","6H"},rationalPlayer.play());

        input = new String[]{"9H", "AS", "JH", "JS", "KH", "QS"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.setCurrentMeld("XCXD");
        Assert.assertArrayEquals(new String[]{"JS","JH"},rationalPlayer.play());

        input = new String[]{"3H", "6C", "6S", "7C", "7S", "8D", "8H", "9C", "9D", "9S", "QH"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"3H"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"6S","7S","8D","9S"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"6C","7C","8H"},rationalPlayer.play());


        input = new String[]{"5C","5H","AS","LH"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        Assert.assertArrayEquals(new String[]{"AS"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"LH"},rationalPlayer.play());
        Assert.assertArrayEquals(new String[]{"5C","5H"},rationalPlayer.play());

        input = new String[]{"4H","AS","KD","9H","4S","JH","KC","QS","XD","5D","9D","3D","9S"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.setCurrentMeld("4C4D");
        Assert.assertArrayEquals(new String[]{"9D","9H"},rationalPlayer.play());

        input = new String[]{"7D", "7H", "8C", "8H", "9H", "KD", "KS", "LC", "LS", "XD"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        str= new String[]{"HUMAN","SPECIAL"};
        rationalPlayer.setRounds(str);

        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);

        rationalPlayer.setListOfThePlayer("HUMAN","XS;XH;XS;XH;XS;XH;XH;XS;XH;XS;XH;XS;");
        rationalPlayer.setRounds(str);
        rationalPlayer.setCurrentMeld("QC");
        Assert.assertArrayEquals(new String[]{"LS"},rationalPlayer.play());

        input = new String[]{"XH", "XD","3C"};
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld(" ");
        Assert.assertArrayEquals(new String[]{"XD","XH"},rationalPlayer.play());


        input = new String[]{"6H","7D", "7H", "8H", "9H", "XH", "QS","KC"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        str= new String[]{"HUMAN","SPECIAL"};
        rationalPlayer.setRounds(str);

        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.clearHand();
        rationalPlayer.setCurrentMeld("7S7C");
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"7H","7D"},rationalPlayer.play());

        input = new String[]{"XH","7D", "7H", "8H", "9H", "3C","4H","5H"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        str= new String[]{"HUMAN","SPECIAL"};
        rationalPlayer.setRounds(str);

        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.clearHand();
        rationalPlayer.setCurrentMeld("7S7C");
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"7D","7H"},rationalPlayer.play());

        input = new String[]{"XH","7D", "7H", "8H", "9H", "3C","4H","5H"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        str= new String[]{"HUMAN","SPECIAL"};
        rationalPlayer.setRounds(str);

        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.clearHand();
        rationalPlayer.setCurrentMeld("7S7C");
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"7D","7H"},rationalPlayer.play());

        input = new String[]{"5S", "6C", "7D", "8H", "9C", "9S", "JH", "XD", "XS"};
        rationalPlayer = new RationalPlayer("SPECIAL");
        str= new String[]{"HUMAN","SPECIAL"};
        rationalPlayer.setRounds(str);

        rationalPlayer.setNextTurn("HUMAN");
        rationalPlayer.clearHand();
        rationalPlayer.setCurrentMeld("5D6H7S8D");
        rationalPlayer.setHand(input);
        Assert.assertArrayEquals(new String[]{"00"},rationalPlayer.play());
    }
    public void test_sequences(){
        String[] input = new String[]{"6H","7D","8H", "9H", "XH", "8S", "9C"};
        support test = new support();
        List<String> l = Arrays.asList(input);

        String[] out = new String[]{"6H7D8S9C","8H9HXH"};
        List<String> output = Arrays.asList(out);

        Assert.assertEquals(output,test.findEfficientlyAllSequences(l));

        input = new String[]{"6H","7D","8H", "9H", "XH", "7H", "8C"};

        l = Arrays.asList(input);

        out = new String[]{"6H7D8C","8H9HXH"};
        output = Arrays.asList(out);
        Assert.assertEquals(output,test.findEfficientlyAllSequences(l));
    }
    public void test_punish_others(){
        // see a pig then kill it
        RationalPlayer rationalPlayer= new RationalPlayer("RATIONALITY");
        // if it is its first turn, play the lowest ranking cards
        support sup = new support();

        String[] str= new String[]{"HUMAN","RATIONALITY"};
        rationalPlayer.setRounds(str);
        rationalPlayer.setNextTurn("HUMAN");

        // WRONG CASES
        String[] input = {"3C","4D","5H","5S","6H","6S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        input = new String[]{"4C","4D","4D"};



        input = new String[]{"4C","4D","5H","5S","6H","6S","8S","8H"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("LD");
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H"},rationalPlayer.play());

        // play a sequence of 4, if an element of a sequence is a part of a pair, then choose the low ranking one
        input = new String[]{"7H", "7S", "7C", "7D"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("LS");
        Assert.assertArrayEquals(new String[]{"7S", "7C", "7D", "7H"},rationalPlayer.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"4C","4D","5H","5S","6H","6S","7C","7S"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("LSLD");
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H","7S","7C"},rationalPlayer.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"4C","4D","5H","5S","6H","6S","7C","7S","8S","8C","9S","XH","JS","3H"};
        rationalPlayer.clearHand();
        rationalPlayer.setHand(input);
        rationalPlayer.setCurrentMeld("LSLCLD");
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H","7S","7C","8S","8C"},rationalPlayer.play());

    }

}
