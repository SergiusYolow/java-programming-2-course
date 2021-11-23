

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class ValidatorException extends Exception {

	public ValidatorException(String str){
		super(str);
	}

	public ValidatorException(ParserConfigurationException e){
		super("Can't create document builder: " + e.getMessage());
	}

	public ValidatorException(TransformerConfigurationException e){
		super("Can't create transformer: " + e.getMessage());
	}

	public ValidatorException(TransformerException e){
		super("Transforming error: " + e.getMessage());
	}

	public ValidatorException(IOException e){
		super("Can't open schema source : " + e.getMessage());
	}

}
