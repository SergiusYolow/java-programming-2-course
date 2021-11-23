


import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

public abstract class XMLValidator
{

    private static Object sync1 = new Object();
    private static Object sync2 = new Object();

    public static void validate(Class<? extends Xml> Class, String xmlRequest, ValidationRequester vr, ValidationType vt)
            throws IOException, ValidatorException
    {

        if (vt == ValidationType.NONE)
            return;

        if (vr == ValidationRequester.Server)
        {
            if (vt == ValidationType.XSD)
            {
                validateXsd(Class, xmlRequest, Directory.xsdDirServer);
            }
            else if (vt == ValidationType.DTD)
            {
                validateDtd(Class, xmlRequest, Directory.dtdDirServer);
            }
            else
            {
                validateXsd(Class, xmlRequest, Directory.xsdDirServer);
                validateDtd(Class, xmlRequest, Directory.dtdDirServer);
            }
        }
        else
        {
            if (vt == ValidationType.XSD)
            {
                validateXsd(Class, xmlRequest, Directory.xsdDirClient);
            }
            else if (vt == ValidationType.DTD)
            {
                validateDtd(Class, xmlRequest, Directory.dtdDirClient);
            }
            else
            {
                validateXsd(Class, xmlRequest, Directory.xsdDirClient);
                validateDtd(Class, xmlRequest, Directory.dtdDirClient);
            }
        }
    }

    private static void validateXsd(Class<? extends Xml> Class, String xmlRequest, String directory) throws IOException, ValidatorException
    {
        synchronized (sync1)
        {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(xmlRequest.getBytes()))
            {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new File(directory + File.separator + Class.getSimpleName() + ".xsd"));
                Validator validator = schema.newValidator();
                Source source = new StreamSource(bis);
                validator.validate(source);
            } catch (SAXException | IOException e)
            {
                throw new InvalidSchemaException(Class, xmlRequest);
            }
        }
    }

    private static void validateDtd(Class<? extends Xml> Class, String xmlRequest, String directory) throws ValidatorException, IOException
    {
        synchronized (sync2)
        {
            DocumentBuilder db;
            try
            {
                db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e)
            {
                throw new ValidatorException(e);
            }

            Document doc;
            try
            {
                doc = db.parse(new InputSource(new StringReader(xmlRequest)));
            } catch (SAXException e2)
            {
                throw new InvalidSchemaException(Class, xmlRequest);
            }

            Transformer transformer;
            try
            {
                transformer = TransformerFactory.newInstance().newTransformer();
            } catch (TransformerConfigurationException e1)
            {
                throw new ValidatorException(e1);
            }
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, directory + File.separator + Class.getSimpleName() + ".dtd");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            try
            {
                transformer.transform(new DOMSource(doc), result);
            } catch (TransformerException e)
            {
                throw new ValidatorException(e);
            }

            try
            {
                db.parse(new InputSource(new StringReader(writer.toString())));
            } catch (SAXException | IOException e)
            {
                throw new InvalidSchemaException(Class, xmlRequest);
            }
        }
    }
}
