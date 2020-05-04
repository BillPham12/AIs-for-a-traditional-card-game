import client_server.AggressivePlayer;
import client_server.PatientPlayer;
import game.Meld;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import robotStrategy.support;

public class patientTest extends TestCase {
    public static void testMoves() {
        PatientPlayer patience= new PatientPlayer("PATIENCE");
        // if it is its turn, then play
        // if it is its first turn, play the lowest ranking cards

        // play a single smallest card
        String[] input = {"3H","4S","4H","5S","6H","6S"};
        patience.setHand(input);
        Assert.assertArrayEquals(new String[]{"3H","4S","5S","6S"},patience.play());
        // play a smallest pair
        input = new String[]{"3C", "3S", "6C", "6H", "7H", "9S"};
        patience.clearHand();
        patience.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3C"},patience.play());

        input = new String[]{"3C", "3S", "4H", "6H", "7H", "9S"};
        patience.clearHand();
        patience.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3C"},patience.play());


        //play a smallest triple
        input = new String[]{"3S", "3H", "3D", "7H", "7S", "7C"};
        patience.clearHand();
        patience.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3D","3H"},patience.play());

        // play a smallest sequence
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AH"};
        patience.clearHand();
        patience.setHand(input);
        Assert.assertArrayEquals(new String[]{"3H","4C","5C","6C","7H","8H"},patience.play());

    }
    /* efficiently use a single card to play over
     if it wins play a small ranking card.
     otherwise pass its turn
    */
    public void test_competitive_single_card() {
        PatientPlayer patience= new PatientPlayer("PATIENCE");
        // if it is its first turn, play the lowest ranking cards


        // play a 3 of heart
        patience.clearHand();
        String[] input = {"3H","4S","4H","5S","6H","6S"};
        patience.setHand(input);
        patience.setCurrentMeld("3S");
        Assert.assertArrayEquals(new String[]{"4H"},patience.play());


        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7H", "3S"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("7S");
        Assert.assertArrayEquals(new String[]{"7H"},patience.play());

        //Can't play
        input = new String[]{"3S", "3H", "3D", "7H", "7S", "LS"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("LC");
        Assert.assertArrayEquals(new String[]{"00"},patience.play());

        // not tend to play
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AD"};
        patience.clearHand();
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;XS;");
        patience.setHand(input);
        patience.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"00"},patience.play());

        // tend to play a AH
        input = new String[]{"JS","QS","KH","AH","AS"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("AC");
        Assert.assertArrayEquals(new String[]{"AH"},patience.play());

    }

    /* efficiently use a pair card to play over
        if it wins play a pair
      otherwise pass its turn
      */
    public void test_competitive_pair() {
        PatientPlayer patience= new PatientPlayer("PATIENCE");
        // if it is its first turn, play the lowest ranking cards

        // play a pair of 3
        String[] input = {"3D","3H","4H","5S","6H","6S","JS","JH","JC"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3S3C");
        Assert.assertArrayEquals(new String[]{"3D","3H"},patience.play());

        // not tend to play because its hand is 10
        input = new String[]{"3D", "3H", "4H", "5S", "6H", "6S", "JS", "JH", "JC"};
        patience.clearHand();
        patience.setHand(input);
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;XS");
        patience.setCurrentMeld("7H7S");
        Assert.assertArrayEquals(new String[]{"00"},patience.play());

        // play because enemy hand is 10
        patience = new PatientPlayer("PATIENCE");
        input = new String[]{ "3H", "4H", "5S", "6H", "6S", "JS", "JH", "JC"};
        patience.clearHand();
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;XS;XS;XS");
        patience.setHand(input);
        patience.setCurrentMeld("7H7S");
        Assert.assertArrayEquals(new String[]{"JS","JC"},patience.play());

        // tend to play because its hand is 8
        input = new String[]{ "4H", "5S", "6H", "6S", "JS", "JH", "JC"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("7H7S");
        Assert.assertArrayEquals(new String[]{"JS","JC"},patience.play());


        // can't play because does have the bigger meld
        input = new String[]{"3C", "3S", "6C", "6H", "7C", "7D"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("7S7H");
        Assert.assertArrayEquals(new String[]{"00"},patience.play());


        //Can play
        input = new String[]{"3S", "3H", "3D", "7H", "AH", "AS"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("ACAD");
        Assert.assertArrayEquals(new String[]{"AS","AH"},patience.play());
    }
    /* efficiently use a triple card to play over
        if it wins play a triple
      otherwise pass its turn
     */
    public void test_competitive_triple() {
        PatientPlayer patience= new PatientPlayer("PATIENCE");
        // if it is its first turn, play the lowest ranking cards

        // play a pair of 3
        String[] input = {"4S","4C","4H","5S","6H","6S"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3S3C3D");
        Assert.assertArrayEquals(new String[]{"4S","4C","4H"},patience.play());

        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7C", "7D"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("7S7D7H");
        Assert.assertArrayEquals(new String[]{"00"},patience.play());


        //Can play
        input = new String[]{"5S", "5C", "5D", "7H", "7S", "7C"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3C3D3H");
        Assert.assertArrayEquals(new String[]{"5S","5C","5D"},patience.play());


        //Can play
        input = new String[]{"5S", "5C", "5D", "7H", "7S", "7C","XS","JS","QS","KH"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3C3D3H");
        Assert.assertArrayEquals(new String[]{"5S","5C","5D"},patience.play());


        // not to play because its hand is 13 and the meld to play is high ranking card
        input = new String[]{"5S", "5C", "5D", "7H", "7S", "7C","XS","JS","QS","KH"};
        patience.clearHand();
        patience.setHand(input);
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;");
        patience.setCurrentMeld("8H8S8C");
        Assert.assertArrayEquals(new String[]{"00"},patience.play());


        // tend to play because the enemy has played 8 cards on its hand
        patience = new PatientPlayer("PATIENCE");
        input = new String[]{"5S", "5C", "5D", "7H", "7S", "7C","XS","JS","QS","KH", "JD","JC","JH"};
        patience.clearHand();
        patience.setHand(input);
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;XS;XS");
        patience.setCurrentMeld("8H8S8C");
        Assert.assertArrayEquals(new String[]{"JS","JC","JD"},patience.play());


        // tend to play
        input = new String[]{"XS","JS","QS","KH", "JD","JC","JH"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("8H8S8C");
        Assert.assertArrayEquals(new String[]{"JS","JC","JD"},patience.play());
    }
    /* efficiently use a triple card to play over
        if it wins play a triple
      otherwise pass its turn
     */
    public void test_competitive_sequences() {

        PatientPlayer patience= new PatientPlayer("PATIENCE");
        // if it is its first turn, play the lowest ranking cards

        // play a sequences of 3
        String[] input = {"3C","4D","5H","5S","6H","6S"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"3C","4D","5H"},patience.play());

        // play a sequence of 4, if an element of a sequence is a part of a pair, then choose the low ranking one
        input = new String[]{"8H", "9S", "XH", "6H", "7C", "7D"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("7S8H9CXS");
        Assert.assertArrayEquals(new String[]{"7C","8H","9S","XH"},patience.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"5H", "6C", "7H", "7S", "8S","9H"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"5H","6C","7H"},patience.play());


        // play normally
        input = new String[]{"3C","3S","3H","KS","QS", "5H", "6C", "7H", "7S", "8S","9H"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"5H","6C","7H"},patience.play());


        // hands has 11 cards and the cost is expensive then tend to play
        input = new String[]{"3C","3S","3H","KS","QS", "5H", "6C", "7H", "7S", "8S","AC"};
        patience = new PatientPlayer("PATIENCE");
        patience.clearHand();
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;XS;XS");
        patience.setHand(input);
        patience.setCurrentMeld("9SXHJH");
        Assert.assertArrayEquals(new String[]{"QS","KS","AC"},patience.play());


        // hands has 11 cards and the enemy has played 7 cards then tend to play
        patience= new PatientPlayer("PATIENCE");
        input = new String[]{"3C","3S","3H","KS","QS", "5H", "6C", "7H", "7S", "8S","AC"};
        patience.clearHand();
        patience.setListOfThePlayer("ENEMY","XS;XS;XS;XS;XS;XS;XS");
        patience.setHand(input);
        patience.setCurrentMeld("9SXHJH");
        Assert.assertArrayEquals(new String[]{"QS","KS","AC"},patience.play());


        // hands has 8 cards and then tends to play
        input = new String[]{"KS","QS", "5H", "6C", "7H", "7S", "8S","AC"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("9SXHJH");
        Assert.assertArrayEquals(new String[]{"QS","KS","AC"},patience.play());


    }

    // see a pig going around, if it is able to kill that pig
    // do it
    public void test_punish_others(){
        // see a pig then kill it
        PatientPlayer patience= new PatientPlayer("PATIENCE");
        // if it is its first turn, play the lowest ranking cards
        support sup = new support();
        // WRONG CASES
        String[] input = {"3C","4D","5H","5S","6H","6S"};
        patience.clearHand();
        patience.setHand(input);
        Assert.assertEquals(false,sup.isHotGun(input));
        input = new String[]{"4C","4D","4D"};
        Assert.assertEquals(false,sup.isHotGun(input));



        input = new String[]{"4C","4D","5H","5S","6H","6S","8S","8H"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("LD");
        Assert.assertEquals(true,sup.isHotGun(input));
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H"},patience.play());

        // play a sequence of 4, if an element of a sequence is a part of a pair, then choose the low ranking one
        input = new String[]{"7H", "7S", "7C", "7D"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("LS");
        Assert.assertEquals(true,sup.isHotGun(input));
        Assert.assertArrayEquals(new String[]{"7S", "7C", "7D", "7H"},patience.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"4C","4D","5H","5S","6H","6S","7C","7S"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("LSLD");
        Assert.assertEquals(true,sup.isHotGun(input));;
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H","7S","7C"},patience.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"4C","4D","5H","5S","6H","6S","7C","7S","8S","8C","9S","XH","JS"};
        patience.clearHand();
        patience.setHand(input);
        patience.setCurrentMeld("LSLCLD");
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H","7S","7C","8S","8C"},patience.play());



    }



}
