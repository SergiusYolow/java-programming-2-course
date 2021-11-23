import java.io.Serializable;
import java.util.Date;

public class MessageNewsResult extends MessageResult implements Serializable
{

    News[] news;
    int k;
    Date date=null;

    MessageNewsResult() {}

    MessageNewsResult(byte CMD)
    {
        super(CMD);
    }

    MessageNewsResult(News[] temp, byte index)
    {
        id=index;
        news = temp;
        k = temp.length;
        errorCode = (byte) Protocol.RESULT_CODE_OK;
    }

    MessageNewsResult(News[] temp, byte index, Date d)
    {
        date=d;
        id=index;
        news = temp;
        k = temp.length;
        errorCode = (byte) Protocol.RESULT_CODE_OK;
    }
}
