

import java.io.Serializable;

/**
 * <p>MessageConnect class
 *
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageConnect extends Message implements Serializable
{

    private static final long serialVersionUID = 1L;

    public String userIP;


    public MessageConnect(String userIP)
    {
        super(Protocol.CMD_CONNECT);
        this.userIP = userIP;

    }



}