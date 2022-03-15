import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.asm.ClassParser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

public class Parser {
	
	//Comprueba que el fichero pasado como parámetro de entrada es de extensión ".txt", ".pdf", ".class" o ".html".
	//Si esto sucede, tendrá en cuenta únicamente aquellas palabras claves que lo componen y las devolverá en una
	//cadena. Si el fichero analizado no es de una de las extensiones mencionadas, entonces el sistema lo ignorará.
	public String leerFichero (String rutaFichero) throws IOException, SAXException, TikaException {
		
		//Detectamos el tipo de fichero:
		BodyContentHandler handler = new BodyContentHandler ();
		Metadata metadata = new Metadata ();
		FileInputStream inputstream = new FileInputStream (new File (rutaFichero));
		ParseContext pcontext = new ParseContext ();

		//Comenzamos a recorrer el documento (parser): 
		switch (FilenameUtils.getExtension (rutaFichero)) {
		case "txt":
			TXTParser txtParser = new TXTParser ();
			txtParser.parse (inputstream, handler, metadata, pcontext);
			break;
		case "pdf":
			PDFParser pdfParser = new PDFParser ();
			pdfParser.parse (inputstream, handler, metadata, pcontext);
			break;
		case "class":
			ClassParser javaParser = new ClassParser ();
			javaParser.parse (inputstream, handler, metadata, pcontext);
			break;
		case "html":
			HtmlParser htmlParser = new HtmlParser ();
			htmlParser.parse (inputstream, handler, metadata, pcontext);
			break;
		}

		return handler.toString ();
	}
}