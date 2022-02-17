import java.util.*;
import java.io.*;

public class main {
	
	static Map map;
	
	//Lista los directorios y ficheros que se encuentran en el directorio pasado como parámetro de entrada (args [0]). 
	public static void listaIt (String [] args) throws Exception {
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
				while ((br.readLine ()) != null) {
					fichContPalabras (args);
				}
				br.close ();
			} catch (FileNotFoundException fnfe) {
				System.out.println ("[ERROR] Fichero desaparecido en combate");
			}
	}

	//Cuenta el número de veces que aparece cada palabra del fichero pasado por primer parámetro (args [0]) y vuelca
	//el resultado en el fichero "RIBW_actividad01_salida" en la ruta pasada como segundo parámetro (args [1]).
	public static void fichContPalabras (String args []) throws IOException {
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

	//Salva el objeto de tipo "TreeMap" (Map) llamado "map.ser" en la ubicación pasada como segundo parámetro (args [1]).
	public static void salvarObjeto (String args []) {
		try {
			FileOutputStream fos = new FileOutputStream (args[1] + "\\map.ser");
			ObjectOutputStream oos = new ObjectOutputStream (fos);
			oos.writeObject (map);
			System.out.println ("[SALVADO] El sistema salvó el archivo especificado (map.ser)");
		} catch (Exception e) {
			System.out.println ("[ERROR] El sistema no pudo salvar el archivo especificado (map.ser)");
			System.out.println (e);
		}
	}

	//Carga el fichero "h.ser" que se encuentra en la ruta pasada como segundo parámetro de entrada (args [1]) y lo retorna
	//como un objeto de tipo "TreeMap" (Map). En caso de que no encuentre "map.ser" arroja una excepción y devuelve "null".
	public static Map cargarObjeto (String args []) {
		try {
			FileInputStream fis = new FileInputStream (args[1] + "\\map.ser");
			ObjectInputStream ois = new ObjectInputStream (fis);
			map = (TreeMap) ois.readObject ();
			System.out.println ("[CARGA] El sistema cargó el archivo especificado (map.ser)");
			return map;
		} catch (Exception e) {
			System.out.println ("[ERROR] El sistema no pudo cargar el archivo especificado (map.ser)");
			return null;
		}
	}

	//Inicia la ejecución del programa Java y debe contener dos parámetros de entrada: args [0] hace referencia al directorio que
	//se quiere recorrer y por otro lado, args [1] hace referencia al directorio de salida donde se volcará las palabras contadas.
	public static void main (String [] args) throws Exception {
		if (args.length < 2) {
			System.out.println ("[ERROR] Formato: > java main directorio_entrada directorio_entrada_salida");
		}
		else {
			System.out.println ("[INICIO] El programa ha iniciado correctamente");
			File file = new File (args[1] + "\\map.ser"); 

			if (file.exists ()) {
				map = cargarObjeto (args);
			} else {
				System.out.println ("[CARGA] El sistema ha creado el archivo especificado (map.ser)");
				map = new TreeMap ();
			}
			listaIt (args);
			salvarObjeto (args);
			System.out.println ("[FINAL] El programa ha finalizado correctamente");
		}
	}
}
