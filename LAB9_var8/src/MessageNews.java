import java.io.Serializable;
import java.util.Date;

public class MessageNews extends Message implements Serializable
{
    Date date = null;

    public MessageNews() {super();}

    MessageNews(byte CMD, Date d)
    {
        super(CMD);
        date = d;
    }


    MessageNews(byte CMD)
    {
        super(CMD);
    }

}
