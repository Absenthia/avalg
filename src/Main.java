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
	static final int NUM_THREADS = 2;

    public static void main(String[] args) throws IOException, InterruptedException {

    	Main m = new Main();
    	
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    	
    	boolean running = true;
	    while(running){
	    	System.out.println("Choose method:");
	    	System.out.println("1: Run all");
	    	System.out.println("2: Run all for specific pnr");
	    	System.out.println("3: Run specific interval for specific pnr");
	    	System.out.println("4: Exit program");
	    	
	    	int choice = Integer.parseInt(b.readLine());
	    	if(choice == 1){
	    		System.out.println("Enter J value");
	    		int j = Integer.parseInt(b.readLine());
	    		System.out.println("Enter runtime in minutes");
	    		int runTime = Integer.parseInt(b.readLine());
	    		
	    		m.runAll(j, runTime);
	    	}else if(choice == 2){
	    		System.out.println("Enter pnr");
	    		String pNum = b.readLine();
	    		System.out.println("Enter J value");
	    		int J = Integer.parseInt(b.readLine());
	    		System.out.println("Enter runtime in minutes");
	    		int runTime = Integer.parseInt(b.readLine());
	    		
	    		m.runAllOne(pNum, J, runTime);
	    	}else if(choice == 3){
	    		System.out.println("Enter pnr");
	    		String pNum = b.readLine();
	    		System.out.println("Enter start point");
	    		int startval = Integer.parseInt(b.readLine());
	    		System.out.println("Enter end point");
	    		int stopval = Integer.parseInt(b.readLine());
	    		System.out.println("Enter J value");
	    		int j = Integer.parseInt(b.readLine());
	    		System.out.println("Enter runtime in minutes");
	    		int runTime = Integer.parseInt(b.readLine());
	    		
	    		m.runChosen(pNum, startval, stopval, j, runTime);
	    	}else if(choice == 4){
	    		running = false;
	    	}
	    }
    	

    	
    }
    
    public Main(){
    	
    }
    
    public void runChosen(String pNum, int startval, int stopval, int J, int runTime) throws InterruptedException, IOException{
    	int length = stopval-startval+1;
    	int intervals = length/5;
    	int runSec = runTime*60;
    	

    	ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    	int start = startval;
    	int stop = stopval;
    	for(int i=0; i<intervals; i++){
    		Brent worker = new Brent(pNum, start, stop, J, runSec);
    		executor.execute(worker);
    		start += length;
    		stop += length;
    	}
    	//Finish all threads in queue
    	executor.shutdown();
    	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    	
    	rebuildFile(pNum,startval,stopval, J);
    }
    
    public void runAllOne(String pNum, int J, int runTime) throws InterruptedException, IOException{
    	int startval = 1;
    	int stopval = 100;
    	
    	runChosen(pNum, startval, stopval, J, runTime);
    }
    
    public void runAll(int J, int runTime) throws IOException, InterruptedException{
    	//RUN CHD PNR
    	String pNum = "7910200059";
    	runAllOne(pNum, J, runTime);    	
    	
    	//RUN SEB PNR
    	pNum = "9106175632";
    	runAllOne(pNum, J, runTime);    
    }
    
    public void rebuildFile(String pNum, int x, int y, int J) throws IOException{
    	PrintWriter pnrwr = new PrintWriter(new FileWriter(pNum+"_res.txt"));
		pnrwr.println(pNum+" "+J);//TODO VALUE OF j NOT ZERO
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