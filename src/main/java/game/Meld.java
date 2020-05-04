package game;

import java.lang.reflect.Array;
import java.util.*;
import robotStrategy.support;

import javax.swing.plaf.IconUIResource;
import java.util.stream.StreamSupport;

public class Meld {
    private String[] value;
    private support support;
    public  Meld(){ value = null;}

    public Meld(String[] strings) {value = strings;}

    public Meld getMeld(){return this;}

    // set value
    public void setValue(String[] str) {value =str;}
    // get value
    public String[] getValue(){return value;}

    //check for valid meld
    public boolean checkMeld(String[] coming_meld){
        support = new support();
        if(coming_meld == null || coming_meld.length == 0) return false;
        else{
            Arrays.sort(coming_meld, new sort_cards());
            char[] l = new char[]{'3','4','5','6','7','8','9','X','J','Q','K','A','L'};
            ArrayList<Character> mylist = new ArrayList<Character>(){};
            for(int i =0; i < l.length;i++)
                mylist.add(l[i]);
            // assuming input is sorted
            // check for a single card
            if(value != null && support.isHotGun(value) && support.isHotGun(coming_meld)){
                if(value.length == 6 && coming_meld.length == 4) return true;
                else if(value.length < coming_meld.length) return true;
                else if (value.length == coming_meld.length){
                    int last = value.length -1;
                    if (compare(value[last],coming_meld[last]) > 0) return false;
                    else return true;

                }
            }


            if(support.isHotGun(coming_meld)){
                if (value == null) return true;
                if (mylist.indexOf(value[0].charAt(0)) == 12){
                    if((coming_meld.length == 4 || coming_meld.length >= 6) && value.length == 1)
                        return true;
                    else if((coming_meld.length >= 8 && value.length == 2))
                        return true;
                    else if((coming_meld.length >= 10 && value.length == 3))
                        return true;
                }
                else return false;

            }

            if(isSingle(coming_meld))
            {
                if(value == null) return true;
                else if (coming_meld.length !=  value.length) return false;
                else return compare(coming_meld[0], value[0]) > 0;
            }
            // check for a pair/ triple
            else if(isPair(coming_meld)){
                int last = coming_meld.length-1;
                char con = coming_meld[0].charAt(0);
                for(int i =0; i < coming_meld.length-1;i++)
                    if(con != coming_meld[i].charAt(0))
                        return false;

                if(value == null) return true;
                else if (!isPair(value)) return false;
                else if (!isPair(coming_meld)) return false;
                Arrays.sort(value, new sort_cards());
                if(coming_meld.length !=  value.length) return false;
                else return compare(coming_meld[last], value[last]) > 0;
            }
            // check for a sequences
            else {
                int last = coming_meld.length-1;
                for(int i =0; i < coming_meld.length-1;i++)
                    if(mylist.indexOf(coming_meld[i+1].charAt(0))-mylist.indexOf(coming_meld[i].charAt(0)) != 1)
                        return false;

                if(coming_meld.length < 3) return false;
                else if(value == null) return true;
                else if (!isSequence(value)) return false;
                else if (!isSequence(coming_meld)) return false;
                else if(coming_meld[last].charAt(0) == 'L') return false;
                else{
                    Arrays.sort(value, new sort_cards());
                    if(coming_meld.length != value.length) return false;
                    else return compare(coming_meld[last], value[last]) > 0;
                }
            }
        }

    }

    // > 0 meaning a > b
    // < 0 meaning a < b
     public int compare(String a, String b) {
            ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
            ArrayList<Character> l1 = new ArrayList<>(); l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
            if(a.charAt(0) == b.charAt(0)) return (l.indexOf(a.charAt(1)) - l.indexOf(b.charAt(1)));
            else if (l1.contains(a.charAt(0)) && !l1.contains(b.charAt(0))) return 1;
            else if (!l1.contains(a.charAt(0)) && l1.contains(b.charAt(0))) return -1;
            else if (!l1.contains(a.charAt(0)) && !l1.contains(b.charAt(0))) return (int) a.charAt(0) - b.charAt(0);
            else return l1.indexOf(a.charAt(0)) - l1.indexOf(b.charAt(0));
    }
    // sorting algorithms
    private class sort_cards implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
            ArrayList<Character> l1 = new ArrayList<>(); l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('L');
            if(a.charAt(0) == b.charAt(0)) return (l.indexOf(a.charAt(1)) - l.indexOf(b.charAt(1)));
            else if (l1.contains(a.charAt(0)) && !l1.contains(b.charAt(0))) return 1;
            else if (!l1.contains(a.charAt(0)) && l1.contains(b.charAt(0))) return -1;
            else return l1.indexOf(a.charAt(0)) - l1.indexOf(b.charAt(0));
        }
    }
    public String toString(){
        if(value == null) return "Nothing";
        else{
            String str = "";
            for(int i =0; i < value.length;i++){
                str += value[i] + "-";
            }
            return str;
        }
    }
    // Assuming the value of the current meld is checked
    private boolean isPair(String[] value){
        if (value == null || value.length == 1) return false;
        char x;
        for (int i =0; i < value.length-1;i++)
            if(value[i].charAt(0) != value[i+1].charAt(0))
                return false;
        return true;
    }

    private boolean isSequence(String[] value){
        if (value == null || value.length == 1) return false;
        char[] l = new char[]{'3','4','5','6','7','8','9','X','J','Q','K','A'};
        ArrayList<Character> list = new ArrayList<Character>();
        for (char x : l)
            list.add(x);
        for (int i =0; i < value.length-1;i++)
            if(list.indexOf(value[i].charAt(0))+1 != list.indexOf(value[i+1].charAt(0)))
                return false;
        return true;
    }

    private boolean isSingle(String[] value){
        if (value.length == 1) return true;
    return false;}
}
