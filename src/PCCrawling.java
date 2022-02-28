import java.util.*;
import java.io.*;
import java.text.Normalizer;

public class PCCrawling {
	
	static Map <String, Ocurrencias> map;
	static Map <String, Integer> thesauro;
	
	//Lista los directorios y ficheros que se encuentran en el directorio pasado como parámetro de entrada (rutaEntrada). 
	public static void listaIt (String rutaEntrada) throws Exception {
		File fichero = new File (rutaEntrada);
		
		if (!fichero.exists () || !fichero.canRead ()) {
			System.out.println ("[ERROR] El sistema no pudo encontrar el fichero " + fichero);
			return;
		}
		if (fichero.isDirectory ()) {
			String [] listaFicheros = fichero.list ();
			String tmpRuta = rutaEntrada;
			for (int i = 0; i < listaFicheros.length; i++) {
				System.out.println (" - " + listaFicheros [i]);
				listaIt (fichero.getPath () + "\\" + listaFicheros [i]);
				rutaEntrada = tmpRuta;
			}
		} else
			try {
				//Es interesante filtrar previamente aquí los ficheros de extensión textual, como: txt, java, p, cpp...
				if (rutaEntrada.endsWith (".txt")) {
					fichContPalabras (rutaEntrada);
				}
			} catch (FileNotFoundException fnfe) {
				System.out.println ("[ERROR] El sistema no pudo leer el fichero " + fichero);
			}
	}

	//Cuenta el número de veces que aparece cada palabra en el fichero pasado como parámetro de entrada (rutaEntrada).
	public static void fichContPalabras (String rutaEntrada) throws IOException {

		BufferedReader br = new BufferedReader (new FileReader (rutaEntrada));
		File fPath = new File (rutaEntrada);
		String linea;

		while ((linea = br.readLine ()) != null) {
			StringTokenizer st = new StringTokenizer (linea, ";:¡!¿?{}[]().,/\\_- \n\r\"'");
			while (st.hasMoreTokens ()) {
				String s = st.nextToken ().toLowerCase ().replace("á", "a").replace("é", "e").replace("í", "i").replace("ú", "u").replace("ó", "o").replace("ä", "a").replace("ë", "e").replace("ï", "i").replace("ö", "o").replace("ü", "u");
				if (!thesauro.containsKey(s)) {
					Object o = map.get (s);
					if (o == null) {
						map.put (s, new Ocurrencias (fPath.getPath ()));
					}
					else {
						((Ocurrencias) o).putOcurr (fPath.getPath ());
						map.put (s, (Ocurrencias) o);
					}
				}	
			}
		}
		br.close ();
	}
	
	//Vuelca el resultado en el fichero "RIBW_salida" en la ruta pasada como parámetro de entrada ("rutaSalida").
	public static void salvarSalida (String rutaSalida) throws IOException {
		List <String> claves = new ArrayList <String> (map.keySet ());
		Collections.sort (claves);
		
		PrintWriter pr = new PrintWriter (new FileWriter (rutaSalida + "\\RIBW_salida.txt"));
		Iterator <String> i = claves.iterator ();
		while (i.hasNext ()) {
			Object k = i.next ();
			Ocurrencias oc = map.get (k);
			pr.println (k + " : " + oc.getFt ());
			
			Map <String, Integer> aux = oc.getOcurr ();
			List <String> l = new ArrayList <String> (aux.keySet ());
			Iterator <String> it = l.iterator ();
			while (it.hasNext ()) {
				Object s = it.next ();
				pr.println ("\t- " + s + " : " + aux.get (s));
			}
		}
		
		System.out.println ("[SALVADO] El sistema salvó el archivo especificado (RIBW_salida.txt)");
		pr.close ();
	}

	//Salva el objeto de tipo "TreeMap" (Map) en el fichero "map.ser" de la ubicación pasada como parámetro ("rutaSalida").
	public static void salvarObjeto (String rutaSalida) {
		try {
			FileOutputStream fos = new FileOutputStream (rutaSalida + "\\map.ser");
			ObjectOutputStream oos = new ObjectOutputStream (fos);
			oos.writeObject (map);
			System.out.println ("[SALVADO] El sistema salvó el archivo especificado (map.ser)");
			oos.close ();
		} catch (Exception e) {
			System.out.println ("[ERROR] El sistema no pudo salvar el archivo especificado (map.ser)");
			System.out.println (e);
		}
	}

	//Carga el fichero "map.ser" que se encuentra en la ruta pasada como parámetro de entrada ("rutaSalida") y lo retorna
	//como un objeto de tipo "TreeMap" (Map). En caso de que no encuentre "map.ser" arroja una excepción y devuelve "null".
	public static Map <String, Ocurrencias> cargarObjeto (String rutaSalida) {
		try {
			FileInputStream fis = new FileInputStream (rutaSalida + "\\map.ser");
			ObjectInputStream ois = new ObjectInputStream (fis);
			map = (TreeMap <String, Ocurrencias>) ois.readObject ();
			System.out.println ("[CARGA] El sistema cargó el archivo especificado (map.ser)");
			ois.close ();
			return map;
		} catch (Exception e) {
			System.out.println ("[ERROR] El sistema no pudo cargar el archivo especificado (map.ser)");
			return null;
		}
	}
	
	public static void cargarThesauroInvertido (String rutaThesauro) throws IOException {
		
		File ruta = new File (rutaThesauro + "\\stopwords_es.txt");
		if (ruta.exists()) {
			thesauro = new TreeMap <String, Integer> ();
			
			BufferedReader br = new BufferedReader (new FileReader (ruta));
			String linea;

			while ((linea = br.readLine ()) != null) {
				StringTokenizer st = new StringTokenizer (linea);
				while (st.hasMoreTokens ()) {
					String s = st.nextToken ().toLowerCase ().replace("á", "a").replace("é", "e").replace("í", "i").replace("ú", "u").replace("ó", "o").replace("ä", "a").replace("ë", "e").replace("ï", "i").replace("ö", "o").replace("ü", "u");
					Object o = thesauro.get (s);
					if (o == null) {
						thesauro.put (s, null);
					}
				}
			}
			System.out.println ("[CARGA] El sistema cargó el archivo especificado (stopwords_es.txt)");
			br.close ();
		}
		else {
			System.out.println ("[ERROR] El sistema no pudo cargar el archivo especificado (stopwords_es.txt)");
		}
		
	}

	//Inicia la ejecución del programa Java y debe contener dos parámetros de entrada: args [0] hace referencia al directorio que
	//se quiere recorrer y por otro lado, args [1] hace referencia al directorio de salida donde se volcará las palabras contadas.
	public static void main (String [] args) throws Exception {
		File f1 = new File (args [0]);
		File f2 = new File (args [1]);
		
		if (args.length < 2 && !f1.canRead () && !f2.canRead ()) {
			System.out.println ("[ERROR] Formato: > java PCCrawling directorio_entrada directorio_entrada_salida");
		}
		else {
			System.out.println ("[INICIO] El programa ha iniciado correctamente");
			File file = new File (args [1] + "\\map.ser");

			if (file.exists ()) {
				map = cargarObjeto (args [1]);
			} else {
				System.out.println ("[CARGA] El sistema ha creado el archivo especificado (map.ser)");
				map = new TreeMap <String, Ocurrencias> ();
			}
			
			cargarThesauroInvertido (args [1]);
			listaIt (args [0]);
			salvarSalida (args [1]); //Salva el fichero de salida (RIBW_salida.txt).
			salvarObjeto (args [1]); //Salva el objeto/diccionario de salida (map.ser).
			System.out.println ("[FINAL] El programa ha finalizado correctamente");
		}
	}

}
