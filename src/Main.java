import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class Main {
	static final int NUM_THREADS = 6;

    public static void main(String[] args) throws IOException, InterruptedException {

    	Main m = new Main();
    	
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Gief personnummer");
    	String pNum = b.readLine();
    	
    	
    	int startval = 1;
    	int stopval = 5;
    	int length = 5;
    	int intervals = 100/length;
    	int runSec = 30;
    	int runMin = 1;
    	
    	/*
    	ArrayList<Brent> tasks = new ArrayList<Brent>();
    	
    	int start = startval;
    	int stop = stopval;
    	for(int i=0; i<length; i++){
	    	Brent brentThread = new Brent(pNum, start, stop, 1);       
	        tasks.add(brentThread);
	        start += length;
	        stop += length;
    	}
    	for(Brent tmp : tasks){
    		tmp.start();
    	}
    	for(Brent tmp : tasks){
    		tmp.join();
    	}*/
    	
    	ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    	int start = startval;
    	int stop = stopval;
    	for(int i=0; i<intervals; i++){
    		Runnable worker = new Brent(pNum, start, stop, runSec);
    		executor.execute(worker);
    		start += length;
    		stop += length;
    	}
    	//Finish all threads in queue
    	executor.shutdown();
    	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    	
    	m.rebuildFile(pNum,startval,stopval);
    }
    
    public Main(){
    	
    }
    
    public void rebuildFile(String pNum, int x, int y) throws IOException{
    	PrintWriter pnrwr = new PrintWriter(new FileWriter(pNum+"_res.txt"));
		pnrwr.println(pNum+" 20");//TODO VALUE OF j NOT ZERO
		pnrwr.flush();
		
    	BufferedReader br;
    	
    	int start = x;
		int end = y;
		int length = y;
		int intervals = 100/length;
    	for(int i=0; i<intervals; i++){
    		br = new BufferedReader(new FileReader(pNum+"_"+start+"-"+end+ "_res.txt"));
    		for(int j=0; j<length; j++){
    			String tmp = br.readLine();
    			if(tmp != null){
    				pnrwr.print(tmp);
    			}
    			pnrwr.println();
    		}
    		br.close();
    		start +=length;
    		end +=length;
    	}
    	pnrwr.flush();
    }
    
}