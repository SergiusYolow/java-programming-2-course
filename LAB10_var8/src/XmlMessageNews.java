import javax.xml.bind.annotation.XmlElement;
import
		 javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
public class XmlMessageNews extends XmlMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	Date date;

	public XmlMessageNews() throws CommandException {
		super();
	}

	public XmlMessageNews(byte id) throws CommandException {
		super(id);
	}

	public XmlMessageNews(byte id, Date date) throws CommandException {
		super(id);
		this.date=date;
	}


}
