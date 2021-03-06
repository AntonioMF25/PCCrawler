import java.util.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.text.Normalizer;

public class PCCrawling {
	
	static Map <String, Ocurrencias> map; //Estrutura de datos que almacena las palabras que aparecen y cuántas veces.
	static Map <String, Integer> thesauro; //Estructura de datos que almacena las palabras del thesauro invertido.
	static String rutaProyecto = FileSystems.getDefault ().getPath ("").toAbsolutePath ().toString ();
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Devuelve el mismo token pasado como parámetro de entrada ("token") pero sin mayúsculas, tildes ni diéresis.
	public static String reemplazarCaracteresEspeciales (StringTokenizer token) {
		String source = Normalizer.normalize (token.nextToken ().toLowerCase (), Normalizer.Form.NFD);
	    return source.replaceAll ("[^\\p{ASCII}]", "");
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Lista los directorios y ficheros que se encuentran en el directorio pasado como parámetro de entrada (rutaEntrada). 
	public static void recorridoRecursivo (String rutaEntrada) throws Exception {
		File fichero = new File (rutaEntrada);
		
		//Se comprueba si la ruta pasada como parámetro de entrada existe y si esta se puede leer (no está corrupta).
		//En caso afirmativo continúa la ejecución del método, pero en caso contrario se muestra un error y finaliza.
		if (!fichero.exists () || !fichero.canRead ()) {
			System.out.println ("[ERROR] El sistema no pudo encontrar el fichero " + fichero);
			return;
		}
		//Se comprueba si la ruta pasada como parámetro de entrada es un directorio. En caso afirmativo recorre todos
		//los archivos de este directorio de forma recursiva. Si no es un directorio contínua la ejecución del método.
		if (fichero.isDirectory ()) {
			File [] listaFicheros = fichero.listFiles ();
			for (File nombreFichero : listaFicheros) {
				recorridoRecursivo (nombreFichero.getAbsolutePath ());
			}
		}
		//En caso de que la ruta pasada como parámetro de entrada no sea un directorio se comprueba que sea un fichero
		//de extensión ".txt", ".html", ".pdf" o ".class". Si esto sucede tendrá en cuenta únicamente aquellas palabras
		//que son clave y las contará. En caso contrario no hará nada, es decir, ignorará esos determinados ficheros.
		else {
			try {
				Parser parser = new Parser ();
				fichContPalabras (rutaEntrada, parser.leerFichero (rutaEntrada));
			} catch (FileNotFoundException fnfe) {
				System.out.println ("[ERROR] El sistema no pudo leer el fichero " + fichero);
			}
		}
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/

	//Cuenta el número de veces que aparece cada palabra en el fichero pasado como parámetro de entrada (rutaEntrada).
	//Si la palabra leída aparece en el thesauro, entonces no se introduce en la estructura "map" de tipo "TreeMap".
	public static void fichContPalabras (String rutaEntrada, String contenido) throws IOException {
		
		//El sistema entenderá como "token" aquel conjunto de caracteres que aparacezcan seguidos hasta un delimitador. Los
		//delimitadores son puntos, comas, espacios, paréntesis, comillas, etc. Tras ello, a cada uno de estos "tokens" les
		//quitará las mayúsculas, las tildes y los diéresis y por último, los guardará en el objeto "map" de tipo "TreeMap".
		StringTokenizer st = new StringTokenizer (contenido, ";:¡!¿?{}[]|&%$#€@<>~¬=+*`´¨()·.,/\\_- \n\r\"'");
		while (st.hasMoreTokens ()) {
			String s = reemplazarCaracteresEspeciales (st);
			
			//Se trata de un thesauro invertido: si este contiene la palabra leída, entonces no se introduce en "map".
			//En caso de que no aparezca en el thesauro, se comprueba si el fichero donde se encuentra el token leído
			//ya existe en la estructura de datos "map". Si existe aumenta en uno el contador total (nf), sino lo crea.
			if (!thesauro.containsKey (s)) {
				Object o = map.get (s);
				if (o == null) { map.put (s, new Ocurrencias (rutaEntrada)); }
				else {
					((Ocurrencias) o).putOcurr (rutaEntrada);
					map.put (s, (Ocurrencias) o);
				}
			}	
		}
	}

	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Vuelca el resultado en la consola, ordenando las palabras alfabéticamente de manera ascendente (de la "A" a la "Z").
	public static void mostrarEnPantalla () {
		List <String> claves = new ArrayList <String> (map.keySet ());
		Collections.sort (claves);
		System.out.println ();
		
		//Muestra cada una de las palabras y el número de veces que aparecen cada una de ellas en total (Ej. mama: 2).
		Iterator <String> i = claves.iterator ();
		while (i.hasNext ()) {
			Object k = i.next ();
			Ocurrencias oc = map.get (k);
			System.out.println (" - " + k + ": " + oc.getFt ());
			
			//Para cada palabra, muestra en qué ficheros de texto aparece y cuántas veces (Ej. C:\FileRute\file.txt: 4).
			Map <String, Integer> aux = oc.getOcurr ();
			List <String> l = new ArrayList <String> (aux.keySet ());
			Iterator <String> it = l.iterator ();
			while (it.hasNext ()) {
				Object s = it.next ();
				System.out.println ("\t» " + s + ": " + aux.get (s));
			}
		}
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/

	//Salva el objeto de tipo "TreeMap" (Map) en el fichero "map.ser" que se encuentra en la carpeta "files" del proyecto.
	public static void salvarObjeto () {
		try {
			FileOutputStream fos = new FileOutputStream (rutaProyecto + "\\files\\map.ser");
			ObjectOutputStream oos = new ObjectOutputStream (fos);
			oos.writeObject (map);
			System.out.println ("[SALVADO] El sistema salvó el archivo especificado (map.ser)");
			oos.close ();
		} catch (Exception e) {
			System.out.println ("[ERROR] El sistema no pudo salvar el archivo especificado (map.ser)");
		}
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/

	//Carga el fichero "map.ser" que se encuentra en carpeta "files" del proyecto y lo retorna como un objeto de tipo "TreeMap"
	//(Map). En caso de que no encuentre el fichero "map.ser" arroja una excepción y devuelve el valor "null".
	@SuppressWarnings ("unchecked")
	public static Map <String, Ocurrencias> cargarObjeto () {
		try {
			FileInputStream fis = new FileInputStream (rutaProyecto + "\\files\\map.ser");
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
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Carga las palabras del thesauro "stopwords_es.txt", que se encuentra en la carpeta "files" del proyecto, y lo guarda en
	//un objeto de tipo "TreeMap" (Map) llamado "thesauro". Si no encuentra el fichero "stopwords_es.txt" muestra un error.
	public static boolean cargarThesauroInvertido () throws IOException {
		File ficheroThesauro = new File (rutaProyecto + "\\files\\stopwords_es.txt");
		
		if (!ficheroThesauro.exists ()) {
			System.out.println ("\n[ERROR] El sistema no pudo cargar el archivo especificado (stopwords_es.txt)");
			return false;
		}
		else {
			thesauro = new TreeMap <String, Integer> ();
			BufferedReader br = new BufferedReader (new FileReader (ficheroThesauro));
			String linea;
			
			//A cada palabra que lea de "stopwords_es.txt" le quitará las mayúsculas, las tildes y los diéresis. Tras ello, las
			//guardará en la estructura "TreeMap" llamada "thesauro". Se evitará introducir dos palabras iguales en el "TreeMap".
			while ((linea = br.readLine ()) != null) {
				StringTokenizer st = new StringTokenizer (linea);
				while (st.hasMoreTokens ()) {
					String s = reemplazarCaracteresEspeciales (st);
					Object o = thesauro.get (s);
					if (o == null) {
						thesauro.put (s, null);
					}
				}
			}
			System.out.println ("\n[CARGA] El sistema cargó el archivo especificado (stopwords_es.txt)");
			br.close ();
			return true;
		}
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//El sistema solicita al usuario que introduzca por teclado la acción que desea realizar. Si pulsa "0" finalizará el programa,
	//si pulsa "1" consultará un término, si pulsa "2" recorrerá el directorio pasado por parámetro de entrada y si finalmente, si
	//otra tecla el sistema le avisará que la opción introducida no existe y le preguntará de nuevo qué acción desea realizar.
	public static void seleccionarOpcion (String directorio) throws Exception {
		Scanner scanner = new Scanner (System.in); String opcion = "0";
		
		do {
			if (opcion.equals ("0") || opcion.equals ("1") || opcion.equals ("2")) {
				System.out.println ("\n[PC-CRAWLER] Seleccione la opción que desea realizar (0-2):");
				System.out.print (" 0. Salir \n 1. Consultar término \n 2. Recorrer directorio");
			}
			System.out.print ("\n[PC-CRAWLER] Opción elegida: ");
			
			switch (opcion = scanner.next ()) {
			case "0":
				break;
			case "1":
				Consultas consulta = new Consultas (map);
				consulta.consultar (); //Ejecuta la acción de consultar un término, indicando dónde aparece y cuántas veces.
				break;
			case "2":
				if (cargarThesauroInvertido ()) { //Carga las palabras del thesauro (stopwords_es.txt).
					recorridoRecursivo (directorio); //Ejecuta el algoritmo principal (recorre los ficheros y cuenta las palabras).
					salvarObjeto (); //Salva el objeto/diccionario de salida (map.ser).
					mostrarEnPantalla (); //Vuelca en pantalla el resultado obtenido (palabras leídas y el número de veces).
				}
				break;
			default:
				System.out.print ("\n[PC-CRAWLER] La opción que ha introducido no existe. Vuelva a intentarlo");
			}
		} while (!opcion.equals ("0"));
		scanner.close ();
	}

	/** ------------------------------------------------------------------------------------------------------------------------ **/
	
	//Inicia la ejecución del programa, que debe tener un parámetro que haga referencia al directorio que se quiere recorrer.
	public static void main (String [] args) throws Exception {
		//Si no hay como mínimo un parámetro de entrada, el sistema muestra un error y finaliza el programa.
		if (args.length < 1) {
			System.out.println ("[ERROR] Formato: > java PCCrawling directorio_analizar");
		}
		else {
			File f1 = new File (args [0]);
			
			//Se comprueba si el parámetro de entrada es correcto y si se puede leer sin errores (no está corrupto). En caso
			//de que no haya problemas comienza la ejecución del programa y en caso contrario se muestra un error y finaliza.
			if (!f1.canRead ()) {
				System.out.println ("[ERROR] La ruta a las que hace referencia el parámetro de entrada no es correcta");
			}
			else {
				System.out.println ("[INICIO] El programa ha iniciado correctamente");
				File file = new File (rutaProyecto + "\\files\\map.ser");

				//Si el sistema encuentra el fichero "map.ser" en la ruta pasada como segundo parámetro (args [1]) lo carga
				//en el objeto "map" de tipo "TreeMap". Si el sistema no encuentra el fichero "map.ser", entonces lo crea.
				if (file.exists ()) { map = cargarObjeto (); }
				else {
					System.out.println ("[CARGA] El sistema ha creado el archivo especificado (map.ser)");
					map = new TreeMap <String, Ocurrencias> ();
				}

				seleccionarOpcion (args [0]); // El usuario debe elegir qué acción desea realizar.
				System.out.println ("\n[FINAL] El programa ha finalizado correctamente");
			}
		}
	}
}
