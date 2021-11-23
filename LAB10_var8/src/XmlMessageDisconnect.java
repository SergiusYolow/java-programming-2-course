import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class XmlMessageDisconnect extends XmlMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public XmlMessageDisconnect() throws CommandException {
		super(Protocol.CMD_DISCONNECT);
	}
}
