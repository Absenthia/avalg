import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;


public class Brent implements Runnable{
    int iterations = 100;
    Generator g;
    HashMap<String, Integer> temp;
    final boolean DEBUG = false;
    String pNum;
    int number, runTime;
    int J;
    long endTime;
    long foundFactor;
    
	public Brent(String pNum, int number, int J, int runTime){
		this.pNum = pNum;
		this.number=number;
		this.runTime = runTime;
		this.J = J;
	}
	
    public void run(){
    	try {

    		String resPath = pNum+"_"+number+"_"+J+ "_res.txt";
    		
    		BigInteger bigNumber = calcNumber();
		
	        int keepLoop = 0;
	        String currString = null;
        	temp = new HashMap<String, Integer>();
        	while(keepLoop == 0){
        		if(DEBUG)System.out.println("curr = " + bigNumber);
        		keepLoop = calcFactorsPollardRho(bigNumber, runTime);
        	}
        	if(keepLoop == -1){
    			System.out.println("Failed to factorize " + bigNumber.toString());
        		currString = null;
    		}else{
    			System.out.println("Factorized " + bigNumber.toString());
    			currString = printResult();
    		}

        	PrintWriter reswr = new PrintWriter(new FileWriter(resPath));
        	if(currString != null){
	        	reswr.println(currString);
        	}
        	reswr.flush();
	        reswr.close();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public int calcFactorsPollardRho(BigInteger n, int runTime) throws IOException{
    	boolean isPrime = millerRabin(n);
    	BigInteger firstFactor = n;
    	foundFactor = runTime/5;
    	long startTime = System.currentTimeMillis();
        endTime = startTime+(runTime*60*1000);

    	while(!isPrime){
    		if(System.currentTimeMillis() > endTime){
				return -1;
			}
    		if(DEBUG)System.out.println("n, beginning of loop: " + n.toString());
    		firstFactor = trialDivision(n, 700000);
    		if(DEBUG)System.out.println("firstFactor (after testDivide) = " + firstFactor);
    		BigInteger numDivisible = null;
    		if(firstFactor.equals(BigInteger.valueOf(-1))){
    			if(DEBUG)System.out.println("STARTING POLLARD RHO");
    			firstFactor = brent(n);
    			if(DEBUG)System.out.println("Inside pollardRho-if. firstFactor = " + firstFactor);
    			if(firstFactor.equals(BigInteger.valueOf(-1))){
    				return -1; //Pollard Rho fails to find factor, and times out
    			}
    		}
    		
    		if(!millerRabin(firstFactor)){
    			continue;
    		}
    		addPrime(firstFactor);
    		endTime += foundFactor;
			numDivisible = n.divide(firstFactor);
			n = numDivisible;
			if(DEBUG)System.out.println("new n, for next loop: " + n.toString());
			isPrime = millerRabin(n);
			
    	}
    	addPrime(n);
    	return 1;
    }
    
    public void addPrime(BigInteger factor){
    	String stringKey = factor.toString();
    	if(DEBUG)System.out.println("stringKey: " + stringKey);
		if(!temp.containsKey(stringKey)){
			if(DEBUG)System.out.println("inte temp.containsKey(stringKey)");
			temp.put(stringKey, 1);
		}else{
			if(DEBUG)System.out.println("temp.containsKey(stringKey)");
			temp.put(stringKey, temp.get(stringKey)+1);
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
    	if(DEBUG)System.out.println("Starting miller rabin");
        //Random rng = new Random();
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

    public BigInteger gOfX(BigInteger x, BigInteger n){
        return (x.pow(2)).mod(n);
    }

    public BigInteger trialDivision(BigInteger n, Integer upperBound) throws IOException{
    	BufferedReader br = new BufferedReader(new FileReader("primes.txt"));
    	BigInteger p = new BigInteger(br.readLine());
    	for(int i = 0; i < upperBound; i++){
    		if (n.mod(p).equals(BigInteger.ZERO)){
            	return p;
        	}
    		p = new BigInteger(br.readLine());
    	}
    	br.close();
    	return BigInteger.valueOf(-1);
    }

    public BigInteger brent(BigInteger n){
    	if(n.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
    		return BigInteger.valueOf(2);
    	}
    	BigInteger y,c,m;
    	y = randomizeBigInt(n.subtract(BigInteger.ONE));
    	c = randomizeBigInt(n.subtract(BigInteger.ONE));
    	m = randomizeBigInt(n.subtract(BigInteger.ONE));
        BigInteger g,r,q, x, ys;
        g = BigInteger.ONE;
        r = BigInteger.ONE;
        q = BigInteger.ONE;
        x = BigInteger.ONE;
        ys = BigInteger.ONE;
        
        while(g.equals(BigInteger.ONE)){
        	x = y;
        	for(int i = 0; i < r.intValue(); i++){
        		y = modPow3(y, BigInteger.valueOf(2), n);
        		y = (y.add(c)).mod(n);
        	}
        	BigInteger k=BigInteger.ZERO;
        	while(k.compareTo(r) == -1 && g.equals(BigInteger.ONE) && System.currentTimeMillis() < endTime){
        		ys = y;
        		BigInteger tmp = m.min(r.subtract(k));
        		for (int i = 0; i < tmp.intValue(); i++){
        			y = modPow3(y, BigInteger.valueOf(2), n);
            		y = (y.add(c)).mod(n);
            		q = (q.multiply((x.subtract(y)).abs())).mod(n);
        		}
        		g = bigIntGcd(q, n);
        		k = k.add(m);
        	}
        	if(System.currentTimeMillis() > endTime){
        		return BigInteger.valueOf(-1);
        	}
        	r = r.multiply(BigInteger.valueOf(2));
        }
        if(g.equals(n)){
        	while(true){
        		ys = modPow3(ys, BigInteger.valueOf(2), n);
        		ys = (ys.add(c)).mod(n);
        		g = bigIntGcd((x.subtract(ys)).abs(),n);
        		if(g.compareTo(BigInteger.ONE) == 1){
        			break;
        		}
        	}
        }
        return g;
    }
    
    public BigInteger calcNumber(){
    	BigInteger pnumber = new BigInteger(pNum);
    	BigInteger exponent = BigInteger.valueOf(10);
		exponent = exponent.pow(60+J);
		pnumber = pnumber.multiply(exponent);
		pnumber = pnumber.add(BigInteger.valueOf(number));
		return pnumber;
    }
	
	public String printResult(){
		ArrayList<BigInteger> keys = new ArrayList<BigInteger>();
		StringBuilder sb = new StringBuilder();
		
		if(temp != null){
			for(Entry<String, Integer> entry : temp.entrySet()){
				String key = entry.getKey();
				keys.add(new BigInteger(key));
			}
			Collections.sort(keys);
		}else return null;
		
		for(BigInteger tmp : keys){
			String key = tmp.toString();
			int value = temp.get(key);
			sb.append(key+" "+value+" ");
		}
		
		return sb.toString();
	}
}