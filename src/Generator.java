import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;


public class Generator {
	BigInteger pnr;
	int j;
	
	public static void main(String[] args) throws IOException {
		BigInteger pnr = new BigInteger("9106175632");
	    Generator g = new Generator(pnr, 0);
	    g.printerino();
	}
	
	public Generator(BigInteger pnr, int j){
		this.pnr = pnr;
		this.j = j;
	}
	
	public void printerino() throws IOException{
		PrintWriter pw = new PrintWriter(new FileWriter(pnr+".txt"));
		BigInteger exponent = BigInteger.valueOf(10);
		exponent = exponent.pow(60+j);
		pnr = pnr.multiply(exponent);
		for(int i=0; i<100; i++){
			pnr = pnr.add(BigInteger.ONE);
			pw.println(pnr.toString());
		}
		pw.flush();
	}
}
