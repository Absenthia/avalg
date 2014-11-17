import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Gief personnummer");
    	String pNum = b.readLine();
    	
    	int start = 1;
    	int stop = 10;
    	
    	ArrayList<Brent> threads = new ArrayList<Brent>();
    	
    	for(int i=0; i<10; i++){
	    	Brent brentThread = new Brent(pNum, start, stop);       
	        threads.add(brentThread);
	        start += 10;
	        stop += 10;
    	}
    	for(Brent tmp : threads){
    		tmp.start();
    	}
    	for(Brent tmp : threads){
    		tmp.join();
    	}
    }
}