import java.io.Serializable;
import java.util.*;

public class Ocurrencias implements Serializable {
	
	Integer Ft;
	Map<String, Integer> ocurr = new TreeMap <String, Integer> ();
	
	public Ocurrencias (String nf) {
		Ft = 1;
		ocurr.put(nf, 1);
	}
	
	public Integer getFt () {
		return Ft;
	}

	public void putOcurr (String pathFichero) {
		if (ocurr.get(pathFichero) != null) {
			ocurr.put(pathFichero, ocurr.get(pathFichero) + 1);
		}
		else {
			ocurr.put(pathFichero, 1);
		}
		Ft++;
	}
	
	public Map<String, Integer> getOcurr () {
		return ocurr;
	}
	
}
