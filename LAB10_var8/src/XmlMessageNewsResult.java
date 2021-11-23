

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;


@XmlRootElement
public class XmlMessageNewsResult extends XmlMessageResult implements Serializable {

	public static final long serialVersionUID = 1L;

	@XmlElement
	String[] news = null;

	@XmlElement
	Date date;

	public XmlMessageNewsResult(byte id) throws ResultException, CommandException {
		setCommand(id);

	}

	public XmlMessageNewsResult() throws ResultException, CommandException {
		super();

	}

	public XmlMessageNewsResult(String[] news) throws ResultException, CommandException {
		setCommand(Protocol.CMD_ALL_NEWS);
		this.news=news;
	}

	public XmlMessageNewsResult(byte result, byte newsCommand, String[] news, Date date) throws ResultException, CommandException {
		super(result,newsCommand);
		this.news=news;
		this.date=date;
	}

	public XmlMessageNewsResult(byte result, byte newsCommand, String[] news) throws ResultException, CommandException {
		super(result,newsCommand);
		this.news = news;
	}


	public String[] getNews() {
		return news;
	}
}
