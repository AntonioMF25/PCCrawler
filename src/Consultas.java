import java.text.Normalizer;
import java.util.*;
import java.util.Map.Entry;

public class Consultas {
	
	static Map <String, Ocurrencias> map; //Estrutura de datos que almacena las palabras que aparecen y cuántas veces.
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Inicializa las instancias de la clase Consultas (constructor).
	public Consultas (Map <String, Ocurrencias> map) {
		Consultas.map = map;
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Devuelve el mismo término pasado como parámetro de entrada ("termino") pero sin mayúsculas, tildes ni diéresis.
	public static String reemplazarCaracteresEspeciales (String termino) {
		String source = Normalizer.normalize (termino.toLowerCase (), Normalizer.Form.NFD);
	    return source.replaceAll ("[^\\p{ASCII}]", "");
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/

	//El sistema solicita al usuario que introduzca por teclado el término que desea consultar. Si no encuentra el término
	//lo indicará mediante un mensaje. Por el contrario, si lo encuentra indicará en qué ficheros aparece y cuántas veces.
	public void consultar () {
		System.out.print ("\n[PC-CRAWLER] Escriba el término que desea consultar: ");
		@SuppressWarnings ("resource") Scanner scanner = new Scanner (System.in);
		String consulta = reemplazarCaracteresEspeciales (scanner.next ());
		
		Ocurrencias oc = map.get (consulta);
		Map <String, Integer> mapOc = null;
		
		if (oc != null) {
			mapOc = oc.getOcurr ();
			List <Entry <String, Integer>> list = new ArrayList <> (mapOc.entrySet ());
			list.sort (Entry.comparingByValue ());
			Collections.reverse (list);
			
			System.out.println ("");
			for (Entry <String, Integer> fichero : list) {
			    System.out.println (" - " + fichero.getKey () + ": " + fichero.getValue ());
			}
		}
		else { System.out.println ("[PC-Crawler] El término '"+ consulta +"' no aparece ninguna vez"); }
	}
}
