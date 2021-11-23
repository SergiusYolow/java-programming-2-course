


import javax.xml.bind.JAXBException;
import java.io.*;
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

    public static void main(String[] args) throws IOException, CommandException, JAXBException, ResultException
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

    static void stopAllUsers() throws IOException, ResultException, CommandException, JAXBException
    {
        String[] nic = getUsers();
        for (String user : nic)
        {
            ServerThread ut = getUserThread(user);
            if (ut != null)
            {
                ut.os.writeByte(Protocol.CMD_DISCONNECT);
                ut.os.writeUTF(Xml.toXml(new XmlMessageDisconnectResult()));
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
                    News[] news = MainClass.getLastNews(MainClass.getLastVisit(thread.userIP));
                    String[] str = new String[news.length];
                    int i = 0;
                    for (News t : news)
                        str[i] = news[i++].toString();
                    thread.os.writeByte(Protocol.CMD_ALL_NEWS);
                    thread.os.writeUTF(Xml.toXml(new XmlMessageNewsResult(Res.OK, Protocol.CMD_LAST_NEWS, str, new Date())));
                    MainClass.deleteClient(thread.userIP);
                    MainClass.appendClientFile(new Client(thread.userIP, date));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (KeyNotUniqueException | ResultException | CommandException | JAXBException e)
        {
            e.printStackTrace();
        }

        return true;
    }
}

class ServerThread extends Thread
{

    private Socket sock;
    DataOutputStream os;
    DataInputStream is;
    private InetAddress addr;
    public String userIP = null;

    public ServerThread(Socket s) throws IOException
    {
        sock = s;
        s.setSoTimeout(1000);
        os = new DataOutputStream(s.getOutputStream());
        is = new DataInputStream(s.getInputStream());
        addr = s.getInetAddress();

        this.setDaemon(true);
    }


    public void run()
    {
        boolean make = false;
        String request = null;
        byte cmd = 0;
        boolean flag = false;
        try
        {
            while (true)
            {
                request = null;
                request = is.readUTF();
                if (request != null)
                {
                    XmlMessageConnect xmc = null;
                    try
                    {
                        xmc = (XmlMessageConnect) Xml.fromXml(XmlMessageConnect.class, request, ValidationRequester.Server, ValidationType.FULL);
                    } catch (JAXBException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if (connect(xmc))
                            make = true;
                    } catch (JAXBException e)
                    {
                        e.printStackTrace();
                    } catch (ResultException e)
                    {
                        e.printStackTrace();
                    } catch (CommandException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            //XmlMessageConnect xmc = (XmlMessageConnect) Xml.fromXml(Xml.getMessageClass(Protocol.CMD_CONNECT), request, ValidationRequester.Server, ValidationType.NONE);
//            if (xmc.getId() == Protocol.CMD_CONNECT)
//            {
//                ServerThread old = register(xmc.userIP);
//                if (old == null)
//                {
//                    XmlMessageConnectResult xmcr = new XmlMessageConnectResult(Res.OK);
//                    os.writeUTF(Xml.toXml(xmcr));
//                    System.out.println(xmcr);
//                    make=true;
//                    //sendSchemas(xmcr, os);
//
//                }
//                else
//                {
//                    os.writeUTF(Xml.toXml(new XmlMessageConnectResult(Res.MSG_ERROR, "User " + old.userIP + " already connected")));
//                    make = false;
//                }
//            }
            while (make)
            {
                cmd = 0;
                request = null;
                XmlMessage msg = null;
                try
                {
                    cmd = is.readByte();
                    request = is.readUTF();
                } catch (IOException e)
                {
                }
                if (request != null)
                {
                    System.out.println(request);
                    msg = null;
                    try
                    {
                        msg = (XmlMessage) Xml.fromXml(Xml.getMessageClass(cmd), request, ValidationRequester.Server, ValidationType.FULL);
                    } catch (JAXBException e)
                    {
                        e.printStackTrace();
                    }
                    if (msg != null)
                    {

                        switch (msg.getId())
                        {


                            case Protocol.CMD_DISCONNECT:
                                flag = true;
                                break;

                            default:
                                try
                                {
                                    news((XmlMessageNews) msg);
                                } catch (ResultException e)
                                {
                                    e.printStackTrace();
                                } catch (CommandException e)
                                {
                                    e.printStackTrace();
                                } catch (JAXBException e)
                                {
                                    e.printStackTrace();
                                }
                                break;

                        }

                        if (flag)
                        {
                            flag = false;
                            break;
                        }
                    }
                }
            }
            disconnect();
        } catch (IOException e)
        {
            System.err.print("Disconnect...");
        }
    }

    void news(XmlMessageNews msg) throws IOException, ResultException, CommandException, JAXBException
    {
        Date date;
        News[] temp = null;
        switch (msg.getId())
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
                String[] str = new String[temp.length];
                int i = 0;
                for (News t : temp)
                    str[i] = temp[i++].toString();
                XmlMessageNewsResult t = new XmlMessageNewsResult(Res.OK, Protocol.CMD_LAST_NEWS, str, new Date());
                os.writeByte(Protocol.CMD_ALL_NEWS);
                os.writeUTF(Xml.toXml(t));
                System.out.println(t);
                System.out.println();
                break;
            }
            case Protocol.CMD_ALL_NEWS:
            {
                temp = MainClass.getAllNews();
                String[] str = new String[temp.length];
                int i = 0;
                for (News t : temp)
                    str[i] = temp[i++].toString();
                XmlMessageNewsResult t = new XmlMessageNewsResult(Res.OK, Protocol.CMD_ALL_NEWS, str, new Date());
                os.writeByte(Protocol.CMD_ALL_NEWS);
                os.writeUTF(Xml.toXml(t));
                System.out.println(t);
                System.out.println();
                break;
            }
            case Protocol.CMD_SELECTED_NEWS:
            {
                date = msg.date;
                temp = MainClass.getNewsByDate(date);
                String[] str = new String[temp.length];
                int i = 0;
                for (News t : temp)
                    str[i] = temp[i++].toString();
                XmlMessageNewsResult t = new XmlMessageNewsResult(Res.OK, Protocol.CMD_SELECTED_NEWS, str, date);
                os.writeByte(Protocol.CMD_ALL_NEWS);
                os.writeUTF(Xml.toXml(t));
                System.out.println(t);
                System.out.println();
                break;
            }
        }

    }

    static void sendSchemas(XmlMessageConnectResult mcr, DataOutputStream os) throws IOException
    {
        for (int i = 0; i < mcr.getSchemaCount(); i++)
        {
            os.writeUTF(FileNames.xsdFileNames[i]);
            os.writeUTF(readFile(Directory.xsdDirServer + File.separator + FileNames.xsdFileNames[i]));
            os.writeUTF(FileNames.dtdFileNames[i]);
            os.writeUTF(readFile(Directory.dtdDirServer + File.separator + FileNames.dtdFileNames[i]));
        }
    }

    static String readFile(String fName) throws FileNotFoundException
    {
        StringBuilder s = new StringBuilder();
        try (Scanner in = new Scanner(new File(fName)))
        {
            while (in.hasNext())
                s.append(in.nextLine()).append("\r\n");
        }
        return s.toString();
    }

    boolean connect(XmlMessageConnect msg) throws IOException, JAXBException, ResultException, CommandException
    {

        System.out.println(Xml.toXml(msg));
        ServerThread old = register(msg.userIP);
        if (old == null)
        {
            System.err.println(msg.userIP + " is connected");
            System.err.println(msg.userIP + " is registered");
            XmlMessageConnectResult xmcr = new XmlMessageConnectResult(Res.OK);
            os.writeUTF(Xml.toXml(xmcr));
            sendSchemas(xmcr, os);
            System.out.println(Xml.toXml(xmcr));
            System.out.println();
            return true;
        }
        else
        {
            os.writeUTF(Xml.toXml(new XmlMessageConnectResult(Res.MSG_ERROR, "User " + old.userIP + " already connected")));
            System.out.println(Xml.toXml(new XmlMessageConnectResult(Res.MSG_ERROR, "User " + old.userIP + " already connected")));
            System.out.println();
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
            }
        }
        return old;
    }
}

