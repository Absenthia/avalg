import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;



public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
    	Main m = new Main();
    	
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
    	m.rebuildFile(pNum);
    }
    
    public Main(){
    	
    }
    
    public void rebuildFile(String pNum) throws IOException{
    	PrintWriter pnrwr = new PrintWriter(new FileWriter(pNum+"_res.txt"));
		pnrwr.println(pNum+" 0");//TODO VALUE OF j NOT ZERO
		pnrwr.flush();
		
    	BufferedReader br;
    	
    	int start = 1;
		int end = 10;
    	for(int i=0; i<10; i++){
    		br = new BufferedReader(new FileReader(pNum+"_"+start+"-"+end+ "_res.txt"));
    		for(int j=0; j<10; j++){
    			String tmp = br.readLine();
    			if(tmp != null){
    				pnrwr.print(tmp);
    			}
    			pnrwr.println();
    		}
    		br.close();
    		start +=10;
    		end +=10;
    	}
    	pnrwr.flush();
    }
}