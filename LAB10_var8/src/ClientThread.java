import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

class ClientThread extends Thread
{

    Socket socket;

    DataOutputStream os;
    DataInputStream is;


    public ClientThread(Socket sock, DataInputStream i, DataOutputStream o) throws IOException
    {

        socket = sock;
        os = o;
        is = i;
        this.setDaemon(true);
    }

    public void run()
    {
        while (true)
        {

            XmlMessageNewsResult news = readResult();
            try
            {
                System.out.println(Xml.toXml(news));
            } catch (JAXBException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (news != null)
            {
                if (news.getID() == Protocol.CMD_DISCONNECT)
                {
                    ClientMain1.stopFlag = true;
                    System.err.println("\nYou are disconnected from server");
                    System.err.println("Press <Enter> to stop...");
                }
                else
                {

                    switch (news.getID())
                    {
                        case Protocol.CMD_ALL_NEWS:
                            System.out.println("\nAll News:");
                            break;
                        case Protocol.CMD_SELECTED_NEWS:
                            SimpleDateFormat d = new SimpleDateFormat("dd.MM.yyyy");
                            if (news.news.length == 0)
                            {

                                System.out.println("\nThere was no news on " + d.format(news.date));
                            }
                            else
                            {
                                System.out.println("\nNews for " + d.format(news.date) + ":");
                            }
                            break;
                        case Protocol.CMD_LAST_NEWS:
                            if (news.news.length != 0)
                                System.out.println("\nLatest news:");
                    }
                    for (String temp : news.news)
                        System.out.println(temp);
                }
            }
            if (ClientMain1.stopFlag)
            {
                socket=null;
                os=null;
                is=null;
                break;
            }
        }
        this.interrupt();
    }

    public XmlMessageNewsResult readResult()
    {

        try
        {
            socket.setSoTimeout(2000);
            XmlMessageNewsResult temp = (XmlMessageNewsResult) Xml.fromXml(Xml.getMessageClass(Protocol.CMD_LAST_NEWS),is.readUTF(),ValidationRequester.Client,ValidationType.FULL);
            return temp;
        } catch (SocketException e)
        {

        } catch (IOException e)
        {

        } catch (JAXBException e)
        {

        }
        return null;
    }


}
