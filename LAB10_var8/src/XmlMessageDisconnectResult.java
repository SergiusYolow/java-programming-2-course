

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class XmlMessageDisconnectResult extends XmlMessageResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public XmlMessageDisconnectResult() throws ResultException, CommandException {
		setID(Protocol.CMD_DISCONNECT);
	}

	public XmlMessageDisconnectResult(byte result) throws ResultException, CommandException {
		super(Protocol.CMD_DISCONNECT, result);
	}

	public XmlMessageDisconnectResult(byte result, String message) throws ResultException, CommandException {
		super(Protocol.CMD_DISCONNECT, result, message);
	}
}
