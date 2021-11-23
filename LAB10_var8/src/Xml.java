


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@XmlTransient
public abstract class Xml
{

    public static String toXml(Xml msg) throws JAXBException, IOException
    {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024))
        {
            JAXBContext context = JAXBContext.newInstance(msg.getClass());
            Marshaller m = context.createMarshaller();
            m.marshal(msg, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toString();
        }
    }

    public static Xml fromXml(Class<? extends Xml> what, String xmlData, ValidationRequester vr, ValidationType vt) throws JAXBException, IOException
    {

        try
        {
            XMLValidator.validate(what, xmlData, vr, vt);
        } catch (ValidatorException e)
        {
            if (vr == ValidationRequester.Server)
                System.err.println(e.getMessage());
            else
                System.err.println("Не удалось проверить схему");
            return null;
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlData.getBytes()))
        {
            JAXBContext context = JAXBContext.newInstance(what);
            Unmarshaller u = context.createUnmarshaller();
            return (Xml) u.unmarshal(byteArrayInputStream);
        }
    }

    public static Class<? extends XmlMessageResult> getResultClass(Class<? extends XmlMessage> msg) throws MessageException
    {
        if (XmlMessageConnect.class.equals(msg))
        {
            return XmlMessageConnectResult.class;
        }
        else if (XmlMessageNews.class.equals(msg))
        {
            return XmlMessageNewsResult.class;
        }
        else if (XmlMessageDisconnect.class.equals(msg))
        {
            return XmlMessageDisconnectResult.class;
        }
        throw new MessageException("getResultClass: undefined error");

    }

    public static Class<? extends XmlMessage> getMessageClass(byte id)
    {

        switch (id)
        {
            case Protocol.CMD_CONNECT:
                return XmlMessageConnect.class;
            case Protocol.CMD_DISCONNECT:
                return XmlMessageDisconnect.class;
            default:
                return XmlMessageNews.class;
        }

    }

}
