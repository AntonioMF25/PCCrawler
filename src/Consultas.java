import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Consultas {
	
	static Map <String, Ocurrencias> map;
	String consulta;
	
	public Consultas (Map <String, Ocurrencias> map) {
		
		this.map = map;
		consulta = "";
		
	}

	public void consultar () {
		//TODO Comprobar si el mapa vacio mensaje de los cojones
		
		System.out.println("Consulta: ");
		Scanner scanner = new Scanner (System.in);
		consulta = scanner.next();
		
		Ocurrencias oc = map.get(consulta);
		Map <String, Integer> mapOc = oc.getOcurr();
		List<Entry<String, Integer>> list = new ArrayList<>(mapOc.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);
		list.forEach(System.out::println);
		
	}
	
}
