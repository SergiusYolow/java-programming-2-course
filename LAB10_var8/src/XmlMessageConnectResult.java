

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlRootElement
public class XmlMessageConnectResult extends XmlMessageResult implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private int schemaCount;

	@XmlTransient
	private static boolean err;

	{
		if (FileNames.xsdFileNames.length != FileNames.dtdFileNames.length) {
			System.err.println("DTD count not equals XSD count");
			err = true;
			schemaCount = -1;
		}
		schemaCount = FileNames.xsdFileNames.length;
		err = false;
	}


	public XmlMessageConnectResult() throws CommandException, ResultException {
		setCommand(Protocol.CMD_CONNECT);
	}

	public XmlMessageConnectResult(byte result) throws ResultException, CommandException {
		super(Protocol.CMD_CONNECT, result);
		if (err)
			setID(Res.UNKNOWN_ERROR);

	}

	public XmlMessageConnectResult(byte result, String message) throws ResultException, CommandException {
		super(Protocol.CMD_CONNECT, result, message);
		if (err)
			setID(Res.UNKNOWN_ERROR);
	}

	public int getSchemaCount() {
		return schemaCount;
	}

}
