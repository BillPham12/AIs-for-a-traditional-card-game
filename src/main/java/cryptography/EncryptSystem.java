package cryptography;

import javafx.util.Pair;

import java.math.BigInteger;
import java.util.StringTokenizer;

public class EncryptSystem {
    public EncryptSystem(){}

    // The message should have the form: name:x;y;z;d;e;f
    // Encrypt the message: Take a string message, public key and number N then return a big integer
    public BigInteger encrypt(BigInteger e, BigInteger N, String message){
            String out = "";
            int x = message.indexOf(":::");
            for(int i =0; i < x;i++)
                out += (int)message.charAt(i);
            out += "585858";
            message = message.substring(x+3);
            StringTokenizer str = new StringTokenizer(message,";");
            while(str.hasMoreTokens()){
                String current = str.nextToken();
                for(int i =0; i < current.length();i++){
                    out += (int) current.charAt(i); out += "59";
                }
            }
            BigInteger output = new BigInteger(out);
            return output.modPow(e,N);
        }
        // Decrypt the message: Take a big integer as input and private key, and number N
    // then produce a string message
    public String decrypt(BigInteger d, BigInteger N,BigInteger num){
        BigInteger input_number = num.modPow(d,N);
        String message = input_number.toString();
        String output = "";
        int x = message.indexOf("585858");
        for (int i =0; i < x; i+=2){
           output +=  (char) Integer.parseInt(message.substring(i,i+2));
        }
        output += ":::";
        message = message.substring(x+6);
        x = 0;
        while(message.contains("59")){
            x++;
            int index = message.indexOf("59");
            output += (char) (Integer.parseInt(message.substring(0,index)));
            message = message.substring(index+2);
            if(x == 2) {x = 0; output += ";";}
        }
        return output;
    }
/*    public static void main(String[] args){
        EncryptSystem x = new EncryptSystem();
        KeyGenerator xx = new KeyGenerator();
        Pair<BigInteger, Pair<BigInteger,BigInteger>> k = xx.generate();
        BigInteger e,d,N;
        e = k.getValue().getKey();
        d = k.getValue().getValue();
        N = k.getKey();

        String sms = "BILL:::2H;3S;4H;";
        System.out.println(x.decrypt(d,N,x.encrypt(e,N,sms)));
        String sms2 = "KEVIN:::2H;3S;4H";
        System.out.println(x.decrypt(d,N,x.encrypt(e,N,sms2)));
    }*/
}

