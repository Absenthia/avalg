import java.io.BufferedReader;
import java.io.File;
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
	static final int NUM_THREADS = 3;

    public static void main(String[] args) throws IOException, InterruptedException {

    	Main m = new Main();
    	
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    	
    	boolean running = true;
	    while(running){
	    	System.out.println("Choose method:");
	    	System.out.println("1: Run all");
	    	System.out.println("2: Run all for specific pnr");
	    	System.out.println("3: Run specific numbers for specific pnr");
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
	    		System.out.println("Enter numbers separated by space");
	    		String[] nums = b.readLine().split(" ");
	    		int[] numbers = new int[nums.length];
	    		for(int i=0; i<nums.length; i++){
	    			numbers[i] = Integer.parseInt(nums[i]);
	    		}
	    		System.out.println("Enter J value");
	    		int j = Integer.parseInt(b.readLine());
	    		System.out.println("Enter runtime in minutes");
	    		int runTime = Integer.parseInt(b.readLine());
	    		
	    		m.runChosen(pNum, numbers, j, runTime);
	    	}else if(choice == 4){
	    		running = false;
	    	}
	    }
    	
	    b.close();
    	
    }
    
    public Main(){
    	
    }
    
    public void runChosen(String pNum, int[] numbers, int J, int runTime) throws InterruptedException, IOException{
    	int runSec = runTime;
    	

    	ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    	
    	for(int i=0; i<numbers.length; i++){
    		Brent worker = new Brent(pNum, numbers[i], J, runSec);
    		executor.execute(worker);
    	}
    	//Finish all threads in queue
    	executor.shutdown();
    	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    	
    	rebuildFile(pNum,numbers, J);
    }
    
    public void runAllOne(String pNum, int J, int runTime) throws InterruptedException, IOException{
    	int[] numbers = new int[100];
    	for(int i=0; i<100; i++){
    		numbers[i] = i+1;
    	}
    	runChosen(pNum, numbers, J, runTime);
    }
    
    public void runAll(int J, int runTime) throws IOException, InterruptedException{
    	//RUN CHD PNR
    	String pNum = "7910200059";
    	runAllOne(pNum, J, runTime);    	
    	
    	//RUN SEB PNR
    	pNum = "9106175632";
    	runAllOne(pNum, J, runTime);    
    }
    
    public void rebuildFile(String pNum, int[] numbers, int J) throws IOException{
    	String[] resfile = new String[100];
    	String resPath = pNum+"_"+J+"_res.txt";
    	
    	File tmpfile = new File(resPath);
	    if(tmpfile.isFile()){
	    	BufferedReader resbr = new BufferedReader(new FileReader(resPath));
	    	resbr.readLine();
	    	//kommentar
	    	for(int i=0; i<100; i++){
	    		String line = resbr.readLine();
	    		if(line != null){
	    			resfile[i] = line;
	    		}else{
	    			resfile[i] = null;
	    		}
	    	}
	    	resbr.close();
	    }else{
	    	for(int i=0; i<100; i++){
	    		resfile[i] = null;
	    	}
	    }
	    
    	PrintWriter pnrwr = new PrintWriter(new FileWriter(resPath));
		pnrwr.println(pNum+" "+J);//TODO VALUE OF j NOT ZERO
		pnrwr.flush();
		
    	BufferedReader br;
    	
    	for(int i=0; i<numbers.length; i++){
    		br = new BufferedReader(new FileReader(pNum+"_"+numbers[i]+"_"+J+ "_res.txt"));
			String tmp = br.readLine();
			if(tmp != null){
				resfile[numbers[i]-1] = tmp;
			}
    		br.close();
    	}
    	for(int i=0; i<resfile.length; i++){
    		String current = resfile[i];
    		if(current != null){
    			pnrwr.print(current);
    		}
    		pnrwr.println();
    	}
    	pnrwr.flush();
    	pnrwr.close();
    }
    
}