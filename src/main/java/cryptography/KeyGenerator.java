package cryptography;
import javafx.util.Pair;

import java.math.*;
import java.util.Random;
// Using asymmetric key cryptographic RSA 512 bits

public class KeyGenerator {
    // choosing randomly 512 bits number as a public key.
    private Random ran = new Random(System.currentTimeMillis());
    private BigInteger e = BigInteger.probablePrime(32,ran),d;
    private BigInteger lambda;
    private BigInteger p,q,N;
    private int bit = 1024;
    private BigInteger one = new BigInteger("1");
    // Constructor

    public KeyGenerator(){}

    public Pair<BigInteger,Pair<BigInteger,BigInteger>> generate(){
        // random variable
        // seed is current time: 10^6 possibilities
        ran.setSeed(System.currentTimeMillis());
        // get 2 512 bits random numbers
        p = BigInteger.probablePrime(bit,ran);
        q = BigInteger.probablePrime(bit,ran);
        // set up N
        N = p.multiply(q);
        // the smallest number such that is divisible by (p-1) and (q-1)
        lambda = lcm(p.subtract(one),q.subtract(one));
        // calculating the private key: d
        d = e.modInverse(lambda);
        // return public key and private key
        Pair<BigInteger,BigInteger> out = new Pair(e,d);
        // return N Pair of <public key, private key>
        return new Pair(N,out);
    }
    // source: https://www.baeldung.com/java-least-common-multiple
    // find the smallest number such that is divisible by (p-1) and (q-1)
    private  BigInteger lcm(BigInteger number1, BigInteger number2) {
        BigInteger gcd = number1.gcd(number2);
        BigInteger absProduct = number1.multiply(number2).abs();
        return absProduct.divide(gcd);
    }



}
