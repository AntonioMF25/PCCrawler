import java.util.*;
import java.io.*;

public class main {
	
	static Map map;
	
	//Lista los directorios y ficheros que se encuentran en el archivo pasado como parámetro de entrada (args [0]). 
	public static void listaIt (String [] args) throws Exception {
		if (args.length < 1) {
			System.out.println ("[ERROR] Formato: > java RIBW_actividad01 nombre_archivo");
			return;
		}
		File fichero = new File (args [0]);
		if (!fichero.exists () || !fichero.canRead ()) {
			System.out.println ("[ERROR] El sistema no pudo leer el fichero " + fichero);
			return;
		}
		if (fichero.isDirectory ()) {
			String [] listaFicheros = fichero.list ();
			String tmpRuta = args [0];
			for (int i = 0; i < listaFicheros.length; i++) {
				System.out.println (" - " + listaFicheros [i]);
				args [0] = args [0] + "\\" + listaFicheros [i];
				listaIt (args);
				args [0] = tmpRuta;
			}
		} else
			try {
				//Es interesante filtrar previamente aquí los ficheros de extensión textual, como: txt, java, p, cpp...
				FileReader fr = new FileReader (fichero);
				BufferedReader br = new BufferedReader (fr);
				String linea;
				while ((linea = br.readLine ()) != null) {
					fichContPalabras (args);
				}
				br.close ();
			} catch (FileNotFoundException fnfe) {
				System.out.println ("[ERROR] Fichero desaparecido en combate");
			}
	}

	//Cuenta el número de veces que aparece cada palabra del fichero pasado por primer parámetro (args [0]) y
	//vuelca el resultado en el fichero pasado como segundo parámetro (args [1]).
	public static void fichContPalabras (String args []) throws IOException {
		if (args.length < 2) {
			System.out.println("[ERROR] Formato: > java RIBW_actividad01 fichero_entrada fichero_salida");
			return;
		}

		String fichEntrada = args [0];
		String fichSalida = args [1];

		BufferedReader br = new BufferedReader (new FileReader (fichEntrada));
		String linea;

		while ((linea = br.readLine ()) != null) {
			StringTokenizer st = new StringTokenizer (linea, ";:.,/\\_- \n\r\"'");
			while (st.hasMoreTokens ()) {
				String s = st.nextToken ();
				Object o = map.get (s);
				if (o == null) {
					map.put (s, Integer.valueOf (1));
				}
				else {
					Integer cont = (Integer) o;
					map.put (s, Integer.valueOf (cont.intValue () + 1));
				}
			}
		}
		br.close ();

		List claves = new ArrayList (map.keySet ());
		Collections.sort (claves);
		
		PrintWriter pr = new PrintWriter (new FileWriter (fichSalida + "\\RIBW_actividad01_salida.txt"));
		Iterator i = claves.iterator ();
		while (i.hasNext ()) {
			Object k = i.next ();
			pr.println (k + " : " + map.get (k));
		}
		pr.close ();
	}

	//Salva (escribe) en el fichero "map.ser" que estará en la ruta pasada por el primer parámetro de entrada (args [0]),
	//el objeto de tipo "TreeMap" (Map) pasado como segundo parámetro de entrada (mapa).
	public static void salvarObjeto (String args []) {
		try {
			FileOutputStream fos = new FileOutputStream (args[1] + "\\map.ser");
			ObjectOutputStream oos = new ObjectOutputStream (fos);
			oos.writeObject (map);
		} catch (Exception e) {
			System.out.println ("[CARGA] El sistema no pudo encontrar el archivo especificado (map.ser)");
			System.out.println (e);
		}
	}

	//Carga el fichero "h.ser" que se encuentra en la ruta pasada como parámetro de entrada (args [0]) y lo retorna (salida)
	//como un objeto de tipo "TreeMap" (Map). En caso de que no encuentre "map.ser" se arroja una excepción y devuelve "null".
	public static Map cargarObjeto (String args []) {
		try {
			FileInputStream fis = new FileInputStream (args[1] + "\\map.ser");
			ObjectInputStream ois = new ObjectInputStream (fis);
			Map map = (TreeMap) ois.readObject ();
			System.out.println ("[CARGA] El sistema encontró el archivo especificado (map.ser)");
			return map;
		} catch (Exception e) {
			System.out.println ("[CARGA] El sistema no pudo encontrar el archivo especificado (map.ser)");
			return null;
		}
	}

	//Inicia la ejecución del programa Java y debe contener dos parámetros de entrada: args [0] hace referencia al archivo que
	//se quiere recorrer y por otro lado, args [1] hace refenrecia al fichero de salida donde se volcará las palabras contadas.
	public static void main (String [] args) throws Exception {
		File file = new File (args[1] + "\\map.ser"); 

		if (file.exists ()) {
			map = cargarObjeto (args);
		} else {
			map = new TreeMap ();
		}
		listaIt (args);
		salvarObjeto (args);
	}
}
