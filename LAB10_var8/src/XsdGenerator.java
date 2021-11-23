


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public class XsdGenerator {


	public static void create(String fileName, Class<? extends Xml> what)
			throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(what);
		context.generateSchema(new XsdOutputResolver(Directory.xsdDirServer, fileName));
	}

	public static void generateAll() throws JAXBException, IOException {
		XsdGenerator.create(XmlMessageConnect.class.getSimpleName(), XmlMessageConnect.class);
		XsdGenerator.create(XmlMessageConnectResult.class.getSimpleName(), XmlMessageConnectResult.class);
		XsdGenerator.create(XmlMessageDisconnect.class.getSimpleName(), XmlMessageDisconnect.class);
		XsdGenerator.create(XmlMessageDisconnectResult.class.getSimpleName(), XmlMessageDisconnectResult.class);
		XsdGenerator.create(XmlMessageNews.class.getSimpleName(), XmlMessageNews.class);
		XsdGenerator.create(XmlMessageNewsResult.class.getSimpleName(), XmlMessageNewsResult.class);

	}


}
