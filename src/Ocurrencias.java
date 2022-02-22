import java.io.Serializable;
import java.util.*;

public class Ocurrencias implements Serializable {
	
	Map <String, Integer> ocurr = new TreeMap <String, Integer> ();
	Integer Ft;
	
	//Inicializa las instancias de la clase Ocurrencias (constructor).
	public Ocurrencias (String nf) {
		ocurr.put (nf, 1);
		Ft = 1;
	}
	
	//Devuelve/retorna el atributo "Ft".
	public Integer getFt () {
		return Ft;
	}
	
	//Devuelve/retorna el atributo "ocurr".
	public Map <String, Integer> getOcurr () {
		return ocurr;
	}

	//Si existe en el mapa "ocurr" la ruta del fichero pasado por parámetro, aumenta en uno el número de palabras que ya tenía. En
	//caso contrario inicializa ese número de palabras (ftd) a 1, al ser la primera palabra de ese tipo que aparece en ese fichero.
	public void putOcurr (String nf) {
		if (ocurr.get (nf) != null) {
			ocurr.put (nf, ocurr.get (nf) + 1);
		}
		else {
			ocurr.put (nf, 1);
		}
		Ft++;
	}
	
}
