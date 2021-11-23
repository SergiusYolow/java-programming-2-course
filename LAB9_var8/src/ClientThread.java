import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

class ClientThread extends Thread
{

    Socket socket;

    ObjectOutputStream os;
    private ObjectInputStream is;


    public ClientThread(Socket sock, ObjectInputStream i, ObjectOutputStream o) throws IOException
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

            MessageNewsResult news = readResult();
            if (news != null)
            {
                if (news.id == Protocol.CMD_DISCONNECT)
                {
                    ClientMain1.stopFlag = true;
                    System.err.println("\nYou are disconnected from server");
                    System.err.println("Press <Enter> to stop...");
                }
                else
                {
                    MessageNewsResult newsRes = news;
                    switch (newsRes.getID())
                    {
                        case Protocol.CMD_ALL_NEWS:
                            System.out.println("\nAll News:");
                            break;
                        case Protocol.CMD_SELECTED_NEWS:
                            SimpleDateFormat d = new SimpleDateFormat("dd.MM.yyyy");
                            if (newsRes.k == 0)
                            {

                                System.out.println("\nThere was no news on " + d.format(newsRes.date));
                            }
                            else
                            {
                                System.out.println("\nNews for " + d.format(newsRes.date) + ":");
                            }
                            break;
                        case Protocol.CMD_LAST_NEWS:
                            if (newsRes.k != 0)
                                System.out.println("\nLatest news:");
                    }
                    for (News temp : newsRes.news)
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

    public MessageNewsResult readResult()
    {

        try
        {
            socket.setSoTimeout(1000);
            MessageNewsResult temp = (MessageNewsResult) is.readObject();
            return temp;
        } catch (SocketTimeoutException e)
        {
        } catch (IOException e)
        {
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }


}
