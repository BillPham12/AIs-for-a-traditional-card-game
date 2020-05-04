import junit.framework.TestCase;
import client_server.AggressivePlayer;
import org.junit.Assert;
import robotStrategy.support;

public class aggressiveTest extends TestCase {
    public static void testMoves() {
        AggressivePlayer aggressive= new AggressivePlayer("AGGRESSIVE");
        // if it is its turn, then play
        // if it is its first turn, play the lowest ranking cards

        // play a single smallest card
        String[] input = {"3H","4S","4H","5S","6H","6S"};
        aggressive.setHand(input);
        Assert.assertArrayEquals(new String[]{"3H","4S","5S","6S"},aggressive.play());

        // play a smallest pair
        input = new String[]{"3C", "3S", "6C", "6H", "7H", "9S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3C"},aggressive.play());

        input = new String[]{"3C", "3S", "4H", "6H", "7H", "9S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3C"},aggressive.play());


        //play a smallest triple
        input = new String[]{"3S", "3H", "3D", "7H", "7S", "7C"};
        aggressive.clearHand();
        aggressive.setHand(input);
        Assert.assertArrayEquals(new String[]{"3S","3D","3H"},aggressive.play());

        // play a smallest sequence
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AH"};
        aggressive.clearHand();
        aggressive.setHand(input);

        Assert.assertArrayEquals(new String[]{"3H","4C","5C","6C","7H","8H"},aggressive.play());
    }
    /* efficiently use a single card to play over
     if it wins play a small ranking card.
     otherwise pass its turn
    */
    public void test_competitive_single_card() {
        AggressivePlayer aggressive= new AggressivePlayer("AGGRESSIVE");
        // if it is its first turn, play the lowest ranking cards

        // play a 3 of heart
        aggressive.clearHand();
        String[] input = {"3H","4S","4H","5S","6H","6S"};
        aggressive.setHand(input);
        aggressive.setCurrentMeld("3S");
        Assert.assertArrayEquals(new String[]{"4H"},aggressive.play());


        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7H", "3S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("7S");
        Assert.assertArrayEquals(new String[]{"7H"},aggressive.play());

        //Can't play
        input = new String[]{"3S", "3H", "3D", "7H", "7S", "LS"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("LC");
        Assert.assertArrayEquals(new String[]{"00"},aggressive.play());

        // play an ace of diamond
        input = new String[]{"3H", "4C", "5C", "6C", "7H", "8H","JS","QS","KH","AD"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("AS");
        Assert.assertArrayEquals(new String[]{"AD"},aggressive.play());
    }

    /* efficiently use a pair card to play over
        if it wins play a pair
      otherwise pass its turn
      */
    public void test_competitive_pair() {
        AggressivePlayer aggressive= new AggressivePlayer("AGGRESSIVE");
        // if it is its first turn, play the lowest ranking cards

        // play a pair of 3
        String[] input = {"3D","3H","4H","5S","6H","6S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("3S3C");
        Assert.assertArrayEquals(new String[]{"3D","3H"},aggressive.play());

        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7C", "7D"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("7S7H");
        Assert.assertArrayEquals(new String[]{"00"},aggressive.play());


        //Can play
        input = new String[]{"3S", "3H", "3D", "7H", "AH", "AS"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("ACAD");
        Assert.assertArrayEquals(new String[]{"AS","AH"},aggressive.play());
    }
    /* efficiently use a triple card to play over
        if it wins play a triple
      otherwise pass its turn
     */
    public void test_competitive_triple() {
        AggressivePlayer aggressive= new AggressivePlayer("AGGRESSIVE");
        // if it is its first turn, play the lowest ranking cards

        // play a pair of 3
        String[] input = {"4S","4C","4H","5S","6H","6S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("3S3C3D");
        Assert.assertArrayEquals(new String[]{"4S","4C","4H"},aggressive.play());

        // play seven of heart
        input = new String[]{"3C", "3S", "6C", "6H", "7C", "7D"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("7S7D7H");
        Assert.assertArrayEquals(new String[]{"00"},aggressive.play());


        //Can play
        input = new String[]{"5S", "5C", "5D", "7H", "7S", "7C"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("3C3D3H");
        Assert.assertArrayEquals(new String[]{"5S","5C","5D"},aggressive.play());

    }
    /* efficiently use a triple card to play over
        if it wins play a triple
      otherwise pass its turn
     */
    public void test_competitive_sequences() {

        AggressivePlayer aggressive= new AggressivePlayer("AGGRESSIVE");
        // if it is its first turn, play the lowest ranking cards

        // play a sequences of 3
        String[] input = {"3C","4D","5H","5S","6H","6S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"3C","4D","5H"},aggressive.play());

        // play a sequence of 4, if an element of a sequence is a part of a pair, then choose the low ranking one
        input = new String[]{"8H", "9S", "XH", "6H", "7C", "7D"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("7S8H9CXS");
        Assert.assertArrayEquals(new String[]{"7C","8H","9S","XH"},aggressive.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"5H", "6C", "7H", "7S", "8S","9H"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("3S4C5D");
        Assert.assertArrayEquals(new String[]{"5H","6C","7H"},aggressive.play());


    }

    // see a pig going around, if it is able to kill that pig
    // do it
    public void test_punish_others(){
        // see a pig then kill it
        AggressivePlayer aggressive= new AggressivePlayer("AGGRESSIVE");
        // if it is its first turn, play the lowest ranking cards
        support sup = new support();
        // WRONG CASES
        String[] input = {"3C","4D","5H","5S","6H","6S"};
        aggressive.clearHand();
        aggressive.setHand(input);
        Assert.assertEquals(false,sup.isHotGun(input));
        input = new String[]{"4C","4D","4D"};
        Assert.assertEquals(false,sup.isHotGun(input));



        input = new String[]{"4C","4D","5H","5S","6H","6S","8S","8H"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("LD");
        Assert.assertEquals(true,sup.isHotGun(input));
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H"},aggressive.play());

        // play a sequence of 4, if an element of a sequence is a part of a pair, then choose the low ranking one
        input = new String[]{"7H", "7S", "7C", "7D"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("LS");
        Assert.assertEquals(true,sup.isHotGun(input));
         Assert.assertArrayEquals(new String[]{"7S", "7C", "7D", "7H"},aggressive.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"4C","4D","5H","5S","6H","6S","7C","7S","7H"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("LSLD");
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H","7S","7C"},aggressive.play());

        // contains 2 sequences, play the low ranking one
        input = new String[]{"4C","4D","5H","5S","6H","6S","7C","7S","8S","8C"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("LSLCLD");
        Assert.assertEquals(true,sup.isHotGun(input));
        Assert.assertArrayEquals(new String[]{"4C","4D","5S","5H","6S","6H","7S","7C","8S","8C"},aggressive.play());


        // contains 2 sequences, play the low ranking one
        input = new String[]{"9S","9C","XS","XC","JC","JD"};
        aggressive.clearHand();
        aggressive.setHand(input);
        aggressive.setCurrentMeld("LDLH");
        Assert.assertEquals(true,sup.isHotGun(input));
        Assert.assertArrayEquals(new String[]{"00"},aggressive.play());

    }


}
