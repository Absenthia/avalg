import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Random;


public class Main {

    int[] primes;
    int iterations = 100;

    public static void main(String[] args) {
        Main main = new Main();
        main.run(110103, 120823);
    }

    public void run(int a, int b){
        BigInteger bigge = new BigInteger("7910200059000000000000000000000001");
        int i = 1;
        while(!millerRabin(bigge)){
            System.out.println("bigge INNAN körning = " + bigge);
            BigInteger factor = pollardRhoNew(bigge);
            System.out.println("Factor no. "+ i + " = " + factor);
            bigge = bigge.divide(factor);
            System.out.println("bigge efter körning = " + bigge);
            i++;
        }
    }

    public int gcd(int a, int b){
        if(b == 0){
            return a;
        }else{
            return gcd(b, a%b);
        }
    }

    public BigInteger bigIntGcd(BigInteger a, BigInteger b){
        if(b.equals(BigInteger.valueOf(0))){
            return a;
        }else{
            return bigIntGcd(b, a.mod(b));
        }
    }

    public boolean millerRabin(BigInteger toTest){
        System.out.println("Starting miller rabin");
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
                return isProbablyPrime;
            }
        }
        System.out.println(toTest + " is probably prime");
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

    public BigInteger pollardRho(BigInteger n){
        BigInteger x = BigInteger.valueOf(2);
        BigInteger y = BigInteger.valueOf(2);
        BigInteger d = BigInteger.valueOf(1);
        while(d.equals(BigInteger.ONE)){
            System.out.println("Inside while loop pollardRho");
            x = ((x.pow(2)).add(BigInteger.ONE)).mod(n);
            y = (((((y.pow(2)).add(BigInteger.ONE)).mod(n)).pow(2)).add(BigInteger.ONE)).mod(n);
            BigInteger temp = (x.subtract(y)).abs();
            d = bigIntGcd(temp, n);
            System.out.println("x = " + x + ", y = " + y + ", d = " + d);
        }
        if(d.equals(n)){
            return BigInteger.valueOf(-1);
        } else {
            return d;
        }
    }

    /*int g (x) {
        return (x * x + 1) % n;
    }
    int main () {
        int n = 10403;
        int x_fixed = 2;
        int cycle_size = 2;
        int x = 2;
        int h = 1;
        while (h == 1) {
            int count = 1;
            while (count <= cycle_size && h == 1) {
                x = g(x);
                count = count + 1;
                h = gcd(x - x_fixed, n);
            }
            if (h != 1)
                break;
            cycle_size = 2 * cycle_size;
            x_fixed = x;
        }
        cout << "\nThe factor is  " << h;
    }*/


    public BigInteger pollardRhoNew(BigInteger n){
        BigInteger x = BigInteger.valueOf(2);
        BigInteger x_fixed = BigInteger.valueOf(2);
        int cycle_size = 2;
        BigInteger h = BigInteger.ONE;
        while (h.equals(BigInteger.ONE)){
            int count = 1;
            while (count <= cycle_size && h.equals(BigInteger.ONE)){
                x = gOfX(x, n);
                count += 1;
                h = bigIntGcd((x.subtract(x_fixed).abs()), n);
                if(count%10000 == 0){
                    System.out.println("RÖÖÖÖV");
                }
            }
            if (!h.equals(BigInteger.ONE)){
                break;
            }
            cycle_size = cycle_size * 2;
            x_fixed = x;
        }
        return h;
    }

    public BigInteger gOfX(BigInteger x, BigInteger n){
        return (x.pow(2)).mod(n);
    }

    public BigInteger quadraticSieve(BigInteger n){
    	//Step 1
        BigInteger factor = testDivide(n);
        if(!factor.equals(BigInteger.valueOf(-1))){
            return factor;
        }
        
        //Step 2
        BigDecimal newN = BigDecimal.valueOf(n.floatValue());
        if(!millerRabin(n)){
        	//Step 3
            for(int k = 2; k < 13; k++){ //TODO - oklart var begränsa k!
                BigDecimal temp = (newN.pow(1 / k));
                temp = temp.round(new MathContext(0, RoundingMode.FLOOR));
                if(temp.pow(k).equals(newN)){
                    return BigInteger.valueOf(temp.intValue());
                }
            }
        }
        
        //Step 4
        BigInteger b = calcB(n);
        return b;
    }

    public BigInteger testDivide(BigInteger n){
        for(int p:primes){
            if (n.mod(BigInteger.valueOf(p)).equals(BigInteger.ZERO)){
                return BigInteger.valueOf(p);
            }
        }
        return BigInteger.valueOf(-1);
    }

    public BigInteger calcB(BigInteger n){
        int c = 2;
        BigDecimal b = BigDecimal.valueOf(c);
        double sqrtTemp = Math.sqrt((Math.log(n.doubleValue())*Math.log(Math.log(n.doubleValue()))));
        BigDecimal eToPower = b.multiply(BigDecimal.valueOf(Math.pow(Math.E, sqrtTemp * (1 / 2))));
        return BigInteger.valueOf(eToPower.round(new MathContext(0, RoundingMode.FLOOR)).intValue());
    }
    
    

}