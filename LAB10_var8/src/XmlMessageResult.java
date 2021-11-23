



import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlTransient
public class XmlMessageResult extends Xml implements Serializable {

	public static class Data implements Serializable {

		private static final long serialVersionUID = 1L;

		@XmlAttribute()
		private byte id;

		@XmlAttribute()
		private byte command;

		@XmlAttribute()
		private String errMessage;

		public Data() {
			command = Protocol.CMD_INVALID;
			id = Res.INVALID;
		}

		@XmlTransient
		public byte getID() {
			return id;
		}

		public void setID(byte id) throws ResultException {
			this.id = id;
		}

		@XmlTransient
		public byte getCommand() {
			return command;
		}

		public void setCommand(byte command) throws CommandException {
			this.command = command;
		}

		@XmlTransient
		public String getErrorMessage() {
			return errMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errMessage = errorMessage;
		}

		public boolean checkError() {
			return id == Protocol.RESULT_CODE_OK;
		}

		public String toString() {
			return "" + id + ", " + ", " + errMessage;
		}

	}

	private static final long serialVersionUID = 1L;

	@XmlElement
	Data data;

	public XmlMessageResult() {
		data = new Data();
	}

	public XmlMessageResult(byte command, byte result) throws ResultException, CommandException {
		this();
		setCommand(command);
		setID(result);
	}

	public XmlMessageResult(byte command, byte result, String errorMessage) throws ResultException, CommandException {
		this();
		setCommand(command);
		setID(result);
		setErrorMessage(errorMessage);
	}


	protected XmlMessageResult.Data getData() {
		return data;
	}

	@XmlTransient()
	public byte getID() {
		return getData().getID();
	}

	public void setID(byte id) throws ResultException {
		getData().setID(id);
	}

	@XmlTransient
	public byte getCommand() {
		return getData().getCommand();
	}

	public void setCommand(byte command) throws CommandException {
		getData().setCommand(command);
	}

	@XmlTransient
	public String getErrorMessage() {
		return getData().getErrorMessage();
	}

	public void setErrorMessage(String msg) {
		getData().setErrorMessage("");
	}

	public static boolean isValid(byte result) {
		return result >= Res.OK && result <= Res.UNKNOWN_ERROR;
	}

	public boolean checkError() {
		return getData().checkError();
	}

	@Override
	public String toString() {
		try {
			return toXml(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
