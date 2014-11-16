import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.HashMap;
import java.util.Random;


public class Main {
	int[] primes;
    int iterations = 100;
    Generator g;

    public static void main(String[] args) throws IOException {
    	Main main = new Main();       
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Gief personnummer");
    	String pNum = b.readLine();
    	
        Generator g = new Generator(new BigInteger(pNum), 0);
        main.run(pNum);
    }

    public void run(String pNum) throws IOException{
    	BufferedReader br = new BufferedReader(new FileReader(pNum + ".txt"));
        BigInteger curr = new BigInteger(br.readLine());
        long startTime = System.currentTimeMillis();
        long endTime = startTime+600000;
        while(curr != null){
        	HashMap<String, String> temp = new HashMap<String, String>();
        	while(System.currentTimeMillis() < endTime){
        		temp = calcFactorsPollardRho(curr, temp);
        		if(System.currentTimeMillis() >= endTime){
        			temp = null;
        			g.printResult(temp);
        		}
        	}
        	g.printResult(temp);
        	curr = new BigInteger(br.readLine());
        	
        }
        //BigInteger b = calcB(bigge);
        
    }
    
    public void testPollardRho(BigInteger t){
    	System.out.println("Init: "+t.toString());
    	System.out.println("After: "+pollardRhoNew(t).toString());
    }
    
    public HashMap<String, String> calcFactorsPollardRho(BigInteger n, HashMap<String, String> temp){
    	if(!millerRabin(n)){
    		BigInteger firstFactor = testDivide(n, 100000);
    		BigInteger numberOfTimes = null;
    		if(firstFactor != BigInteger.valueOf(-1)){
    			numberOfTimes = n.divide(firstFactor);
        		temp.put(firstFactor.toString(), numberOfTimes.toString());
        		calcFactorsPollardRho(numberOfTimes, temp);
    		}
    		BigInteger aFactor = pollardRhoNew(n);
    		numberOfTimes = n.divide(aFactor);
    		temp.put(aFactor.toString(), numberOfTimes.toString());
    		calcFactorsPollardRho(numberOfTimes, temp);
    	}
    	return temp;
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
            }
            //moved this from inner for-loop back to this
            return isProbablyPrime;
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
                    System.out.println("A-S-S?");
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

    public BigInteger quadraticSieve(BigInteger n) throws IOException{
    	//Step 1
        BigInteger factor = testDivide(n, 100000); //TODO - what upper bound do we want?
        if(!factor.equals(BigInteger.valueOf(-1))){
            return factor;
        }
        
        //Step 2
        BigDecimal newN = BigDecimal.valueOf(n.floatValue());
        if(!millerRabin(n)){
        	//Step 3
            for(int k = 2; k < 13; k++){ //TODO - oklart var begränsa k!
                BigDecimal temp = (newN.pow(1 / k));//TODO - change pow to takeRoot!
                temp = temp.round(new MathContext(0, RoundingMode.FLOOR));
                if(temp.pow(k).equals(newN)){
                    return BigInteger.valueOf(temp.intValue()); //TODO - vad göra här egentligen? se pdf...
                }
            }
        }
        
        //Step 4
        BigInteger b = calcB(n);
        //Step 5
        BufferedReader br = new BufferedReader(new FileReader("primes.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("baseprimes.txt"));
        String line = br.readLine();
        while(line != null){
        	BigInteger temp = BigInteger.valueOf(Integer.parseInt(line));
        	if(temp.compareTo(b) != -1) break;
        	if(legendre(temp, n) == 1){
        		System.out.println("This is line: "+ line);
        		bw.write(line);
        	}
        	line=br.readLine();
        }
        
        return b; //TODO - change to different return value
    }

    public BigInteger testDivide(BigInteger n, Integer upperBound){
        for(int p:primes){
        	while(p < upperBound){
        		if (n.mod(BigInteger.valueOf(p)).equals(BigInteger.ZERO)){
                	return BigInteger.valueOf(p);
            	}
        	}
        }
        return BigInteger.valueOf(-1);
    }

    public BigInteger calcB(BigInteger n){
        int c = 2;
        double exponent = 0.5*(Math.sqrt(Math.log(n.doubleValue())*Math.log(Math.log(n.doubleValue()))));
        long tmp = Math.round(c*Math.pow(Math.E, exponent));
        
        BigInteger B = new BigInteger(""+tmp);
        return B;
    }
    
    public int legendre(BigInteger a, BigInteger p){
    	BigInteger symbol = modPow3(a,(p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2)),p);
    	if(symbol.equals(BigInteger.ONE)){
    		return 1;
    	}else if(symbol.equals(BigInteger.ZERO)){
    		return 0;
    	} else {
    		return -1;
    	}
    }
    
    public BigInteger qOfX(BigInteger n, BigInteger x){
    	return (((sqrt(n)).add(x)).pow(2)).subtract(n);
    }
    
    public BigInteger[] shanksTonelli(BigInteger x, BigInteger n, BigInteger p) throws NumberFormatException, IOException{
    	BigInteger[] result = new BigInteger[2];
    	BigInteger pMinusOne = p.subtract(BigInteger.ONE);
    	BigInteger z = null;
    	int s = pMinusOne.getLowestSetBit();
    	if (s == 1){
    		BigInteger r = modPow3(n, (p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4)), p);
    		result[0] = r;
    		result[1] = r.negate();
    		return result;
    	}
    	BigInteger q = pMinusOne.divide(BigInteger.valueOf(2).pow(s));
    	//if p=2^t
    	if(q.equals(BigInteger.ONE)){
    		//do bruteforce?
    	}
    	BufferedReader br = new BufferedReader(new FileReader("primes.txt"));
    	while(br.readLine() != null){
    		z = BigInteger.valueOf((Integer.parseInt(br.readLine())));
    		if(legendre(z, p) == -1){
    			break;
    		}
    	}
    	BigInteger c = modPow3(z, q, p);
    	BigInteger r = modPow3(n, (q.add(BigInteger.ONE)).divide(BigInteger.valueOf(2)), p);
    	BigInteger t = modPow3(n, q, p);
    	BigInteger m = BigInteger.valueOf(s); //TODO - m int?
    	for(int i = 1; i < Integer.parseInt(m.toString()); i++){
    		if(((t.pow(i)).mod(p)).equals(BigInteger.ONE)){ //TODO - modPow3?
    			break;
    		}
    		BigInteger b = modPow3(c, (BigInteger.valueOf(2)).pow(m.intValue()-i-1), p);
    		r = modPow3(r.multiply(b), BigInteger.ONE, p);
    		t = modPow3(t.multiply(b.pow(2)), BigInteger.ONE, p);
    		c = modPow3(b.pow(2), BigInteger.ONE, p);
    		m = BigInteger.valueOf(i);
    	}
    	result[0] = r;
    	result[1] = p.subtract(r);
    	return result;
    }
    
    public static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for(;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
    }
    
    /*
     * found at: http://uclue.com/?xq=1608
     */
    public static BigDecimal takeRoot(int root, BigDecimal n, BigDecimal maxError) {
        int MAXITER = 5000;

        // Specify a math context with 40 digits of precision.
        MathContext mc = new MathContext(40);

        // Specify the starting value in the search for the cube root.
        BigDecimal x;
        x=new BigDecimal("1",mc);

        
        BigDecimal prevX = null;
       
        BigDecimal rootBD = new BigDecimal(root,mc);
        // Search for the cube root via the Newton-Raphson loop. Output each successive iteration's value.
        for(int i=0; i < MAXITER; ++i) {
            x = x.subtract(x.pow(root,mc)
                   .subtract(n,mc)
                   .divide(rootBD.multiply(x.pow(root-1,mc),mc),mc),mc);
            if(prevX!=null && prevX.subtract(x).abs().compareTo(maxError) < 0)
                break;
            prevX = x;
        }
       
        return x;
    }
    
    
}