import javax.xml.bind.JAXBException;
import java.io.IOException;

public class TestClass
{
    public static void main(String[] args) throws IOException, ClassNotFoundException, KeyNotUniqueException, JAXBException, CommandException, ResultException
    {
//        News[] n = new News[1];
//        n[0] = new News("title", "info", new Date());
//        System.out.println(n[0].toString());
//        String[] temp = new String[2];
//        temp[0]=n[0].toString();
//        temp[1]="news title info date";
//
//        System.out.println(Xml.toXml(new XmlMessageNewsResult()));
        XsdGenerator.generateAll();

    }
}
