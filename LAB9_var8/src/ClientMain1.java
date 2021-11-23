import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeMap;



public class ClientMain1
{

    static boolean stopFlag = false;

    public static void main(String[] args) throws UnknownHostException
    {
        InetAddress address = InetAddress.getByName("192.168.1.102");
        try (Socket sock = new Socket(address, Protocol.PORT))

        {
            System.err.println("Client initialized");

            session(sock,"UserFirstName");
        } catch (Exception e)
        {
            System.err.println(e);
        } finally
        {
            System.err.println("Client stopped");
        }
    }

    static class ClientThread extends Thread
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
                        stopFlag = true;
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


    static void waitKeyToStop()
    {
        System.err.println("Press a key to stop...");
        try
        {
            System.in.read();
        } catch (IOException e)
        {
        }
    }

    static class Session
    {
        boolean connected = false;
        String userIP;

        Session(String user)
        {
            userIP = user;
        }
    }

    static Object syncOS = new Object();

    static void session(Socket s, String user)
    {
        try (Scanner in = new Scanner(System.in);
             ObjectInputStream is = new ObjectInputStream(s.getInputStream());
             ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream()))
        {
            Session ses = new Session(user);
            if (openSession(ses, is, os, in))
            {
                try
                {
                    ClientThread clientThread = new ClientThread(s,is,os);
                    clientThread.start();
                    printPrompt();
                    while (true)
                    {
                        Message msg = getCommand(ses, in);
                        if (stopFlag || !processCommand(ses, msg, os))
                        {
                            break;
                        }
                    }
                } finally
                {
                    closeSession(ses, os);
                }
            }
        } catch (Exception e)
        {
            System.err.println(e);
        }
    }

    static boolean openSession(Session ses, ObjectInputStream is, ObjectOutputStream os, Scanner in)
            throws IOException, ClassNotFoundException
    {
        os.writeObject(new MessageConnect(ses.userIP));
        MessageConnectResult msg = (MessageConnectResult) is.readObject();
        if (msg.Error() == false)
        {
            System.err.println("Server connected");
            ses.connected = true;
            os.writeObject(new MessageNews(Protocol.CMD_LAST_NEWS));
            return true;
        }
        System.err.println("Unable to connect: " + msg.getErrorMessage());
        System.err.println("Press <Enter> to continue...");
        if (in.hasNextLine())
            in.nextLine();
        return false;
    }

    static void closeSession(Session ses, ObjectOutputStream os) throws IOException
    {
        if (ses.connected)
        {
            ses.connected = false;
            ses.userIP = null;
        }
        if(!stopFlag)
        {
            os.writeObject(new MessageDisconnect());
        }
        stopFlag = true;
    }

    static Message getCommand(Session ses, Scanner in)
    {
        while (true)
        {
            //printPrompt();
            if (in.hasNextLine() == false)
                break;
            String str = in.nextLine();
            byte cmd = translateCmd(str);
            switch (cmd)
            {
                case -1:
                    return null;
                case Protocol.CMD_ALL_NEWS:
                    return new MessageNews(Protocol.CMD_ALL_NEWS);

                case Protocol.CMD_SELECTED_NEWS:
                    Date date = new Date();
                    System.out.print("¬ведите день: ");


                    date.setDate(in.nextInt());
                    System.out.print("¬ведите мес€ц: ");
                    date.setMonth(in.nextInt() - 1);
                    System.out.print("¬ведите год: ");
                    date.setYear(in.nextInt() - 1900);

                    return new MessageNews(Protocol.CMD_SELECTED_NEWS, date);

                case 0:
                    continue;
                default:
                    System.err.println("Unknow command!");
                    continue;
            }
        }
        return null;
    }


    static TreeMap<String, Byte> commands = new TreeMap<String, Byte>();

    static
    {

        commands.put("q", new Byte((byte) -1));
        commands.put("quit", new Byte((byte) -1));
        commands.put("sn", new Byte(Protocol.CMD_SELECTED_NEWS));
        commands.put("selected news", new Byte(Protocol.CMD_SELECTED_NEWS));
        commands.put("an", new Byte(Protocol.CMD_ALL_NEWS));
        commands.put("all news", new Byte(Protocol.CMD_ALL_NEWS));

    }

    static byte translateCmd(String str)
    {
        // returns -1-quit, 0-invalid cmd, Protocol.CMD_XXX
        str = str.trim();
        Byte r = commands.get(str);
        if (stopFlag)
            return -1;
        return (r == null ? 0 : r.byteValue());
    }

    static void printPrompt()
    {
        System.err.println();
        System.err.println("Enter \"q\" or \"quit\" to stop");
        System.err.println("Enter \"sn\" or \"selected news\" to get news for selected date");
        System.err.println("Enter \"an\" or \"all news\" to get all news");
        System.err.flush();
    }

    static boolean processCommand(Session ses, Message msg,
                                  ObjectOutputStream os)
            throws IOException
    {
        if (msg != null)
        {
            synchronized (syncOS)
            {
                os.writeObject(msg);
            }
            return true;
        }
        return false;
    }


}
