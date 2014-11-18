import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;


public class Generator {
	PrintWriter pnrwr;
	BigInteger pnr;
	int J, x, y;
	
	/*public static void main(String[] args) throws IOException {
		BigInteger pnr = new BigInteger("7910200059");
	    Generator g = new Generator(pnr, 0);
	    g.printerino();
	}*/
	
	public Generator(BigInteger pnr, int J) throws IOException{
		this.pnr = pnr;
		this.J = J;
		pnrwr = new PrintWriter(new FileWriter(pnr+"_"+J+".txt"));
	}
	
	public Generator(BigInteger pnr, int J, int x, int y) throws IOException{
		this.pnr = pnr;
		this.J = J;
		pnrwr = new PrintWriter(new FileWriter(pnr+"_"+x+"-"+y+"_"+J+".txt"));
		printerino(x, y);
	}
	
	public void printerino() throws IOException{
		BigInteger exponent = BigInteger.valueOf(10);
		exponent = exponent.pow(60+J);
		pnr = pnr.multiply(exponent);
		for(int i=0; i<100; i++){
			pnr = pnr.add(BigInteger.ONE);
			pnrwr.println(pnr.toString());
		}
		pnrwr.flush();
	}
	
	public void printerino(int x, int y) throws IOException{
		BigInteger exponent = BigInteger.valueOf(10);
		exponent = exponent.pow(60+J);
		pnr = pnr.multiply(exponent);
		pnr = pnr.add(BigInteger.valueOf(x));
		for(int i=x; i<=y; i++){
			pnrwr.println(pnr.toString());
			pnr = pnr.add(BigInteger.ONE);
		}
		pnrwr.flush();
	}
	
	public String printResult(HashMap<String, Integer> res){
		ArrayList<BigInteger> keys = new ArrayList<BigInteger>();
		StringBuilder sb = new StringBuilder();
		
		if(res != null){
			for(Entry<String, Integer> entry : res.entrySet()){
				String key = entry.getKey();
				keys.add(new BigInteger(key));
			}
			Collections.sort(keys);
		}else return null;
		
		for(BigInteger tmp : keys){
			String key = tmp.toString();
			int value = res.get(key);
			sb.append(key+" "+value+" ");
		}
		
		return sb.toString();
	}
}