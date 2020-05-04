package game;

public class Card {
    // H: heart; D: Diamond; C: clubs ;S: Spades
    private String suit;
    // value will be from 2 to 14.
    // 11 for J; 12 for Q; 13 for K and 14 for A
    private int value;
    private boolean visible = false;

    // card constructor
    public Card(String suit, int value){this.suit =suit; this.value= value;}

    // set visible
    public void setVisible(){visible = true;}
    // get visible
    public boolean getVisible(){return visible;}
    public String getSuit(){return suit;}
    public int getValue(){return value;}
    // print out function
    public String toString(){
        if (value == 2)  return "L" + suit + ";";
        else if (value <= 9) return value + suit;
        else{
            if( value == 10) return "X" +suit + ";";
            else if( value == 11) return "J" +suit +";";
            else if( value == 12) return "Q" +suit +";";
            else if ( value == 13) return "K" +suit+";";
            else return "A" +suit + ";";
        }
    }

}
