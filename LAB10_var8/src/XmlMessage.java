


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.PrintStream;
import java.io.Serializable;

@XmlTransient
public class XmlMessage extends Xml implements Serializable {

	public static class Data implements Serializable {

		private static final long serialVersionUID = 1L;


		protected byte id;

		public Data() {
			id = Protocol.CMD_INVALID;
		}

		@XmlAttribute(required = true)
		public byte getID() {
			return id;
		}

		public void setID(byte id) throws CommandException {
			if (!isValid(id))
				throw new CommandException(id);
			this.id = id;
		}

		public String toString() {
			return "" + id;
		}
	}

	@XmlElement
	protected Data data;

	protected XmlMessage.Data getData() {
		return data;
	}

	private static final long serialVersionUID = 1L;

	protected XmlMessage() {
		data = new Data();
	}

	public XmlMessage(byte id) throws CommandException {
		this();
		setID(id);
	}

	public static boolean isValid(byte command) {
		return command >= Protocol.CMD_CONNECT && command <= Protocol.CMD_DISCONNECT;
	}


	public byte getId() {
		return getData().getID();
	}

	protected void setID(byte id) throws CommandException {
		getData().setID(id);
	}

}
