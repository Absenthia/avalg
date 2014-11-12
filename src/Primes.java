import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;


public class Primes extends Main{
	
	
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
}