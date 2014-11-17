import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map.Entry;


public class Generator {
	PrintWriter pnrwr, reswr;
	BigInteger pnr;
	int j, x, y;
	
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
	
	public Generator(BigInteger pnr, int j, int x, int y) throws IOException{
		this.pnr = pnr;
		this.j = j;
		pnrwr = new PrintWriter(new FileWriter(pnr+"_"+x+"-"+y+".txt"));
		reswr = new PrintWriter(new FileWriter(pnr+"_"+x+"-"+y+"_res.txt"));
		reswr.println(pnr+" "+j);
		reswr.flush();
		printerino(x, y);
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
	
	public void printerino(int x, int y) throws IOException{
		BigInteger exponent = BigInteger.valueOf(10);
		exponent = exponent.pow(60+j);
		pnr = pnr.multiply(exponent);
		pnr = pnr.add(BigInteger.valueOf(x));
		for(int i=x; i<=y; i++){
			pnrwr.println(pnr.toString());
			pnr = pnr.add(BigInteger.ONE);
		}
		pnrwr.flush();
	}
	
	public void printResult(HashMap<String, Integer> res){
		if(res != null){
			for(Entry<String, Integer> entry : res.entrySet()){
				String key = entry.getKey();
				Integer value = entry.getValue();
				reswr.print(key+" "+value+" ");
			}
		}
		reswr.println();
		reswr.flush();
	}
}
