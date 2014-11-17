import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;


public class Primes {
    int iterations = 100;
    Generator g;
    HashMap<String, Integer> temp;
    final boolean DEBUG = false;
    int runTime = 10;
    String pNum;

	int stop = 10001;
    BigInteger maxPrime = BigInteger.valueOf(50000000);
    BigInteger two = BigInteger.valueOf(2);
    BigInteger startMR = BigInteger.valueOf(stop);
    
    public void run(){
    	calcPrimes();
    }
	
    public void calcPrimes(){
    	try {
			PrintWriter pw = new PrintWriter(new File("primes.txt"));
			
			pw.println(2);
			int tmp = 3;
			
			while(tmp < stop){
				boolean prime = true;
				for(int i=3; i<tmp; i++){
					if(tmp%i == 0){
						prime = false;
					}
				}
				if(prime) pw.println(tmp);
				tmp += 2;
			}
			pw.flush();
			
			BigInteger current = startMR;
			
			while(current.compareTo(maxPrime) != 1){
				if(millerRabin(current)){
					pw.println(current.intValue());
				}
				current = current.add(two);//current ++
			}
			pw.flush();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public boolean millerRabin(BigInteger toTest){
    	if(DEBUG)System.out.println("Starting miller rabin");
        Random rng = new Random();
        boolean isProbablyPrime = false;

        BigInteger nMinusOne = toTest.subtract(BigInteger.ONE);
        int s = nMinusOne.getLowestSetBit(); //Finds powers of two
        BigInteger d = nMinusOne.divide(BigInteger.valueOf(2).pow(s));
        outerloop: //label
        for(int i = 0; i < iterations; i++){
            BigInteger a = randomizeBigInt(nMinusOne.subtract(BigInteger.valueOf(4)).add(BigInteger.valueOf(2)));
            BigInteger x = modPow3(a, d, toTest);
            if(x.equals(BigInteger.ONE) || x.equals(toTest.subtract(BigInteger.ONE))) {
                continue;
            }
            for (int j = 0; j < s; j++){
                x = modPow3(x, BigInteger.valueOf(2), toTest);
                if(x.equals(1)){
                    return isProbablyPrime;
                }else if (x.equals(nMinusOne)){
                    continue outerloop;
                }
            }
            //moved this from inner for-loop back to this
            return isProbablyPrime;
        }
        if(DEBUG)System.out.println(toTest + " is probably prime");
        return true;
    }
    
    //Right to left binary method
    public BigInteger modPow3(BigInteger base, BigInteger exponent, BigInteger mod){
        BigInteger result = BigInteger.ONE;
        base = base.mod(mod);

        while(exponent.compareTo(BigInteger.valueOf(0)) == 1){
            if(exponent.mod(BigInteger.valueOf(2)).equals(BigInteger.valueOf(1))){
                result = result.multiply(base).mod(mod);
            }
            exponent = exponent.shiftRight(1);
            base = (base.multiply(base).mod(mod));
        }
        return result;
    }

    public BigInteger randomizeBigInt (BigInteger upperBound){
        BigInteger result;
        Random rng = new Random();
        do{
            result = new BigInteger(upperBound.bitLength(), rng);
        } while (result.compareTo(upperBound) >= 0);
        return result;
    }
}