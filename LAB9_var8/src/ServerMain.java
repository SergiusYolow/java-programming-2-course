


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeMap;


public class ServerMain
{

    private static int MAX_USERS = 10;

    public static void main(String[] args) throws IOException
    {

        try (ServerSocket serv = new ServerSocket(Protocol.PORT))
        {
            System.err.println("Server initialized\n");
            ServerStopThread tester = new ServerStopThread();
            tester.start();
            while (true)
            {
                Socket sock = accept(serv);
                if (sock != null)
                {
                    if (getNumUsers() < MAX_USERS)
                    {
                        ServerThread server = new ServerThread(sock);
                        server.start();
                    }
                    else
                    {
                        System.err.println(sock.getInetAddress().getHostAddress() + " connection rejected");
                        sock.close();
                    }
                }
                if (getStopFlag())
                {
                    break;
                }
            }
        } catch (IOException e)
        {
            System.err.println(e);
        } finally
        {
            stopAllUsers();
            System.err.println("Server stopped");
        }
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
        }
    }

    public static Socket accept(ServerSocket serv)
    {
        assert (serv != null);
        try
        {
            serv.setSoTimeout(1000);
            Socket sock = serv.accept();
            return sock;
        } catch (SocketException e)
        {
        } catch (IOException e)
        {
        }
        return null;
    }

    static void stopAllUsers() throws IOException
    {
        String[] nic = getUsers();
        for (String user : nic)
        {
            ServerThread ut = getUserThread(user);
            if (ut != null)
            {
                ut.os.writeObject(new MessageNewsResult(Protocol.CMD_DISCONNECT));
                //System.err.println(ut.userIP + " is disconnected");
            }
        }
    }

    private static Object syncFlags = new Object();
    private static boolean stopFlag = false;

    public static boolean getStopFlag()
    {
        synchronized (ServerMain.syncFlags)
        {
            return stopFlag;
        }
    }

    public static void setStopFlag(boolean value)
    {
        synchronized (ServerMain.syncFlags)
        {
            stopFlag = value;
        }
    }


    private static Object syncUsers = new Object();
    private static TreeMap<String, ServerThread> users =
            new TreeMap<String, ServerThread>();

    public static ServerThread getUserThread(String userIP)
    {
        synchronized (ServerMain.syncUsers)
        {
            return ServerMain.users.get(userIP);
        }
    }

    public static ServerThread registerUser(String userIP, ServerThread user)
    {
        synchronized (ServerMain.syncUsers)
        {
            ServerThread old = ServerMain.users.get(userIP);
            if (old == null)
            {
                ServerMain.users.put(userIP, user);
            }
            return old;
        }
    }

    public static ServerThread setUser(String userIP, ServerThread userThread)
    {
        synchronized (ServerMain.syncUsers)
        {
            ServerThread res = ServerMain.users.put(userIP, userThread);
            if (userThread == null)
            {
                ServerMain.users.remove(userIP);
            }
            return res;
        }
    }

    public static String[] getUsers()
    {
        synchronized (ServerMain.syncUsers)
        {
            return ServerMain.users.keySet().toArray(new String[0]);
        }
    }

    public static int getNumUsers()
    {
        synchronized (ServerMain.syncUsers)
        {
            return ServerMain.users.keySet().size();
        }
    }
}

class ServerStopThread extends CommandThread
{

    static final String CMD_QUIT = "q";
    static final String CMD_QUIT_LONG = "quit";
    static final String CMD_ADD_NEWS = "an";
    static final String CMD_ADD_NEWS_LONG = "add news";


    Scanner fin;

    public ServerStopThread()
    {
        fin = new Scanner(System.in);
        ServerMain.setStopFlag(false);
        putHandler(CMD_QUIT, CMD_QUIT_LONG, new CmdHandler()
        {
            @Override
            public boolean onCommand(int[] errorCode) { return onCmdQuit(); }
        });
        putHandler(CMD_ADD_NEWS, CMD_ADD_NEWS_LONG, new CmdHandler()
        {
            @Override
            public boolean onCommand(int[] errorCode) { return onCmdAddNews(); }
        });

        this.setDaemon(true);
        System.err.println("Enter \'" + CMD_QUIT + "\' or \'" + CMD_QUIT_LONG + "\' to stop server");
        System.err.println("Enter \'" + CMD_ADD_NEWS + "\' or \'" + CMD_ADD_NEWS_LONG + "\' to add one news\n");
    }

    public void run()
    {

        while (true)
        {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                break;
            }
            if (fin.hasNextLine() == false)
                continue;
            String str = fin.nextLine();
            if (!command(str))
            {
                break;
            }
        }
    }

    public boolean onCmdQuit()
    {
        System.err.println("stop server...");
        fin.close();

        ServerMain.setStopFlag(true);
        return false;
    }

    public boolean onCmdAddNews()
    {
        Date date = null;
        try
        {
            if ((date = MainClass.appendNewsFile()) != null)
            {
                System.err.println("News added");

            }
            else
            {
                System.err.println("News not added");
            }
            String[] userIP = ServerMain.getUsers();
            ServerThread thread;
            for (String temp : userIP)
            {
                thread = ServerMain.getUserThread(temp);
                if (MainClass.getLastVisit(thread.userIP).compareTo(date) < 0)
                {
                    thread.os.writeObject(new MessageNewsResult(MainClass.getLastNews(MainClass.getLastVisit(thread.userIP)), Protocol.CMD_LAST_NEWS));
                    MainClass.deleteClient(thread.userIP);
                    MainClass.appendClientFile(new Client(thread.userIP, date));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (KeyNotUniqueException e)
        {
            e.printStackTrace();
        }

        return true;
    }
}

class ServerThread extends Thread
{

    private Socket sock;
    public ObjectOutputStream os;
    private ObjectInputStream is;
    private InetAddress addr;
    public String userIP = null;

    public ServerThread(Socket s) throws IOException
    {
        sock = s;
        s.setSoTimeout(1000);
        os = new ObjectOutputStream(s.getOutputStream());
        is = new ObjectInputStream(s.getInputStream());
        addr = s.getInetAddress();
        this.setDaemon(true);
    }

    public void run()
    {

        boolean flag = false;
        try
        {
            while (true)
            {
                Message msg = null;
                try
                {
                    msg = (Message) is.readObject();
                } catch (IOException e)
                {
                } catch (ClassNotFoundException e)
                {
                }
                if (msg != null)
                    switch (msg.getID())
                    {

                        case Protocol.CMD_CONNECT:
                            if (!connect((MessageConnect) msg))
                                return;
                            break;

                        case Protocol.CMD_DISCONNECT:
                            flag = true;
                            break;

                        default:
                            news((MessageNews) msg);
                            break;

                    }

                if (flag)
                {
                    flag = false;
                    break;
                }
            }
            disconnect();
        } catch (IOException e)
        {
            System.err.print("Disconnect...");
        }
    }

    void news(MessageNews msg) throws IOException
    {
        Date date;
        News[] temp = null;
        switch (msg.getID())
        {
            case Protocol.CMD_LAST_NEWS:
            {
                date = MainClass.getLastVisit(userIP);

                if (date == null)
                {
                    temp = MainClass.getAllNews();

                }
                else
                {
                    temp = MainClass.getLastNews(date);
                }

                if (temp.length != 0)
                {
                    date = temp[temp.length - 1].date;
                    MainClass.deleteClient(userIP);
                    MainClass.appendClientFile(new Client(userIP, date));
                }
                os.writeObject(new MessageNewsResult(temp, Protocol.CMD_LAST_NEWS));
                break;
            }
            case Protocol.CMD_ALL_NEWS:
            {
                temp = MainClass.getAllNews();
                os.writeObject(new MessageNewsResult(temp, Protocol.CMD_ALL_NEWS));
                break;
            }
            case Protocol.CMD_SELECTED_NEWS:
            {
                date = msg.date;
                temp = MainClass.getNewsByDate(date);
                os.writeObject(new MessageNewsResult(temp, Protocol.CMD_SELECTED_NEWS, date));
                break;
            }
        }

    }

    boolean connect(MessageConnect msg) throws IOException
    {

        ServerThread old = register(msg.userIP);
        if (old == null)
        {
            os.writeObject(new MessageConnectResult());
            return true;
        }
        else
        {
            os.writeObject(new MessageConnectResult(
                    "User " + old.userIP + " already connected"));
            return false;
        }
    }


    private boolean disconnected = false;

    public void disconnect()
    {
        if (!disconnected)
            try
            {
                System.err.println(userIP + " is disconnected");
                unregister();
                os.close();
                is.close();
                sock.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                this.interrupt();
                disconnected = true;
            }
    }

    private void unregister()
    {
        if (userIP != null)
        {
            ServerMain.setUser(userIP, null);
            userIP = null;
        }
    }

    private ServerThread register(String IP)
    {
        ServerThread old = ServerMain.registerUser(IP, this);
        if (old == null)
        {
            if (userIP == null)
            {
                userIP = IP;
                System.err.println(IP + " connected");
                System.err.println("User " + IP + " is registered\n");
            }
        }
        return old;
    }
}

