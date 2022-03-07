import java.util.*;
import java.io.*;
import java.text.Normalizer;

public class PCCrawling {
	
	static Map <String, Ocurrencias> map; //Estrutura de datos que almacena las palabras que aparecen y cuántas veces.
	static Map <String, Integer> thesauro; //Estructura de datos que almacena las palabras del thesauro invertido.
	
	//Devuelve el mismo token pasado como parámetro de entrada ("token") pero sin mayúsculas, tildes ni diéresis.
	public static String reemplazarCaracteresEspeciales (StringTokenizer token) {
		String source = Normalizer.normalize (token.nextToken ().toLowerCase (), Normalizer.Form.NFD);
	    return source.replaceAll ("[^\\p{ASCII}]", "");
	}
	
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
			String [] listaFicheros = fichero.list ();
			String tmpRuta = rutaEntrada;
			for (int i = 0; i < listaFicheros.length; i++) {
				recorridoRecursivo (fichero.getPath () + "\\" + listaFicheros [i]);
				rutaEntrada = tmpRuta;
			}
		}
		//En caso de que la ruta pasada como parámetro de entrada no sea un directorio se comprueba que sea un fichero
		//de extensión ".txt". En caso afirmativo leerá y contará las palabras de este, en caso contrario no hará nada.
		else {
			try {
				if (rutaEntrada.endsWith (".txt")) {
					fichContPalabras (rutaEntrada);
				}
			} catch (FileNotFoundException fnfe) {
				System.out.println ("[ERROR] El sistema no pudo leer el fichero " + fichero);
			}
		}
	}

	//Cuenta el número de veces que aparece cada palabra en el fichero pasado como parámetro de entrada (rutaEntrada).
	//Si la palabra leída aparece en el thesauro, entonces no se introduce en la estructura "map" de tipo "TreeMap".
	public static void fichContPalabras (String rutaEntrada) throws IOException {
		BufferedReader br = new BufferedReader (new FileReader (rutaEntrada));
		File fPath = new File (rutaEntrada);
		String linea;

		//El sistema entenderá como "token" aquel conjunto de caracteres que aparacezcan seguidos hasta un delimitador. Los
		//delimitadores son puntos, comas, espacios, paréntesis, comillas, etc. Tras ello, a cada uno de estos "tokens" les
		//quitará las mayúsculas, las tildes y los diéresis y por último, los guardará en el objeto "map" de tipo "TreeMap".
		while ((linea = br.readLine ()) != null) {
			StringTokenizer st = new StringTokenizer (linea, ";:¡!¿?{}[]|&%$#€@<>~¬=+*`´¨()·.,/\\_- \n\r\"'");
			while (st.hasMoreTokens ()) {
				String s = reemplazarCaracteresEspeciales (st);
				
				//Se trata de un thesauro invertido: si este contiene la palabra leída, entonces no se introduce en "map".
				//En caso de que no aparezca en el thesauro, se comprueba si el fichero donde se encuentra el token leído
				//ya existe en la estructura de datos "map". Si existe aumenta en uno el contador total (nf), sino lo crea.
				if (!thesauro.containsKey (s)) {
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
	//Las palabras aparecerán ordenadas alfabéticamente de manera ascendente, es decir, de la letra "A" a la "Z".
	public static void mostrarPantalla () {
		List <String> claves = new ArrayList <String> (map.keySet ());
		Collections.sort (claves);
		System.out.println ();
		
		//Muestra cada una de las palabras y el número de veces que aparecen cada una de ellas en total (Ej. mama: 2).
		Iterator <String> i = claves.iterator ();
		while (i.hasNext ()) {
			Object k = i.next ();
			Ocurrencias oc = map.get (k);
			System.out.println ("• " + k + ": " + oc.getFt ());
			
			//Para cada palabra, muestra en qué ficheros de texto aparece y cuántas veces (Ej. C:\FileRute\file.txt: 4).
			Map <String, Integer> aux = oc.getOcurr ();
			List <String> l = new ArrayList <String> (aux.keySet ());
			Iterator <String> it = l.iterator ();
			while (it.hasNext ()) {
				Object s = it.next ();
				System.out.println ("\t- " + s + ": " + aux.get (s));
			}
		}
		System.out.println ();
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
	
	//Carga las palabras del thesauro "stopwords_es.txt", que se encuentra en la ruta pasada como parámetro ("rutaThesauro"),
	//y lo guarda en un objeto de tipo "TreeMap" (Map) llamado "thesauro". Si no encuentra "stopwords_es.txt" muestra un error.
	public static void cargarThesauroInvertido (String rutaThesauro) throws IOException {
		File ficheroThesauro = new File (rutaThesauro + "\\stopwords_es.txt");
		
		if (!ficheroThesauro.exists ()) {
			System.out.println ("[ERROR] El sistema no pudo cargar el archivo especificado (stopwords_es.txt)");
			return;
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
			System.out.println ("[CARGA] El sistema cargó el archivo especificado (stopwords_es.txt)");
			br.close ();
		}
	}

	//Inicia la ejecución del programa Java y debe contener dos parámetros de entrada: args [0] hace referencia al directorio que
	//se quiere recorrer y por otro lado, args [1] hace referencia al directorio de salida donde se volcará las palabras contadas.
	//En la ruta del segundo parámetro pasado por entrada (args [1]) se debe encuentrar el fichero thesauro ("stopwords_es.txt").
	public static void main (String [] args) throws Exception {
		File f1 = new File (args [0]);
		File f2 = new File (args [1]);
		
		//Se comprueba si hay como mínimo dos parámetros de entrada y si estos se pueden leer (no están corruptos).
		//En caso afirmativo comienza la ejecución del programa y en caso contrario se muestra un error y finaliza.
		if (args.length < 2 && !f1.canRead () && !f2.canRead ()) {
			System.out.println ("[ERROR] Formato: > java PCCrawling directorio_entrada directorio_salida");
		}
		else {
			System.out.println ("[INICIO] El programa ha iniciado correctamente");
			File file = new File (args [1] + "\\map.ser");

			//Si el sistema encuentra el fichero "map.ser" en la ruta pasada como segundo parámetro (args [1]) lo carga
			//en el objeto "map" de tipo "TreeMap". Si el sistema no encuentra el fichero "map.ser", entonces lo crea.
			if (file.exists ()) {
				map = cargarObjeto (args [1]);
			} else {
				System.out.println ("[CARGA] El sistema ha creado el archivo especificado (map.ser)");
				map = new TreeMap <String, Ocurrencias> ();
			}
			
			Scanner scanner = new Scanner (System.in);
			System.out.println("0 si salir 1 si consulta 2 si recorrer directorio: ");
			switch (scanner.nextInt()) {
			case 0:
				return;
			case 1:
				Consultas consulta = new Consultas (map);
				consulta.consultar();
				break;
			case 2:
				cargarThesauroInvertido (args [1]); //Carga las palabras del thesauro (stopwords_es.txt).
				recorridoRecursivo (args [0]); //Ejecuta el algoritmo principal (recorre los ficheros y cuenta las palabras).
				salvarObjeto (args [1]); //Salva el objeto/diccionario de salida (map.ser).
				mostrarPantalla (); //Salva el fichero de salida (RIBW_salida.txt).
				break;
			default:
				
			}
			System.out.println ("[FINAL] El programa ha finalizado correctamente");
		}
	}
}
