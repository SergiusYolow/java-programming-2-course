import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class TestClass
{
    public static void main(String[] args) throws IOException, ClassNotFoundException, KeyNotUniqueException
    {


        String temp = InetAddress.getLocalHost().getHostName();
        System.out.println(temp);


    }
}
