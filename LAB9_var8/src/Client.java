import java.io.Serializable;
import java.util.Date;

public class Client implements Serializable
{
    String ip;
    Date lastVisit;

    public Client(String ip)
    {
        this.ip=ip;
    }

    public Client(String ip,Date date)
    {
        this.ip=ip;
        lastVisit=date;
    }

    public Client() {}

    public void setLastVisit(Date temp)
    {
        lastVisit=temp;
    }

    @Override
    public String toString()
    {
       return (ip+"   ~~~   "+lastVisit);
    }
}
