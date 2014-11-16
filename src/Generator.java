import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map.Entry;


public class Generator {
	PrintWriter pnrwr, reswr;
	BigInteger pnr;
	int j;
	
	public static void main(String[] args) throws IOException {
		BigInteger pnr = new BigInteger("7910200059");
	    Generator g = new Generator(pnr, 0);
	    g.printerino();
	}
	
	public Generator(BigInteger pnr, int j) throws IOException{
		this.pnr = pnr;
		this.j = j;
		pnrwr = new PrintWriter(new FileWriter(pnr+".txt"));
		reswr = new PrintWriter(new FileWriter(pnr+"_res.txt"));
		reswr.println(pnr+" "+j);
		reswr.flush();
	}
	
	public void printerino() throws IOException{
		BigInteger exponent = BigInteger.valueOf(10);
		exponent = exponent.pow(60+j);
		pnr = pnr.multiply(exponent);
		for(int i=0; i<100; i++){
			pnr = pnr.add(BigInteger.ONE);
			pnrwr.println(pnr.toString());
		}
		pnrwr.flush();
	}
	
	public void printResult(HashMap<String, String> res){
		if(res == null){
			reswr.println();
		}else{
			for(Entry<String, String> entry : res.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				reswr.print(key+" "+value+" ");
			}
		}
		reswr.println();
		reswr.flush();
	}
}
