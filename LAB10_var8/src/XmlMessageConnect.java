import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class XmlMessageConnect extends XmlMessage implements Serializable
{

    private static final long serialVersionUID = 1L;

    @XmlElement
    String userIP = null;

    public XmlMessageConnect(String userIP) throws CommandException
    {
        super(Protocol.CMD_CONNECT);
        this.userIP=userIP;
    }

    public XmlMessageConnect() throws CommandException
    {
        super(Protocol.CMD_CONNECT);

    }


}
