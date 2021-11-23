import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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

            session(sock, "UserFirstName");
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
            while (!stopFlag)
            {
                String req = null;
                byte cmd = 0;
                XmlMessageResult request = null;
                try
                {
                    cmd = is.readByte();
                    req = is.readUTF();
                } catch (IOException e)
                {
                    //e.printStackTrace();
                }

                if (req != null)
                {
                    ValidationType vt = ValidationType.FULL;
                    if (cmd==Protocol.CMD_DISCONNECT)
                        vt=ValidationType.NONE;
                    try
                    {
                        request = (XmlMessageResult) Xml.fromXml(Xml.getResultClass(Xml.getMessageClass(cmd)), req, ValidationRequester.Client, vt);
                    } catch (JAXBException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } catch (MessageException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (request != null)
                {
                    System.out.println(req);
                    if (request.getID() == Protocol.CMD_DISCONNECT)
                    {
                        stopFlag = true;
                        System.err.println("\nYou are disconnected from server");
                        System.err.println("Press <Enter> to stop...");
                    }
                    else
                    {
                        XmlMessageNewsResult news = (XmlMessageNewsResult) request;
                        switch (news.getID())
                        {
                            case Protocol.CMD_ALL_NEWS:
                                System.out.println("\nAll News:");
                                break;
                            case Protocol.CMD_SELECTED_NEWS:
                                SimpleDateFormat d = new SimpleDateFormat("dd.MM.yyyy");
                                if (news.news == null)
                                {

                                    System.out.println("\nThere was no news on " + d.format(news.date));
                                }
                                else
                                {
                                    System.out.println("\nNews for " + d.format(news.date) + ":");
                                }
                                break;
                            case Protocol.CMD_LAST_NEWS:
                                if (news.news != null)
                                    System.out.println("\nLatest news:");
                        }
                        if (news.news != null)
                            if (news.news.length != 0)
                                for (String temp : news.news)
                                    System.out.println(temp);
                    }
                }
                if (ClientMain1.stopFlag)
                {
                    socket = null;
                    os = null;
                    is = null;

                    break;
                }
            }
            this.interrupt();
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
             DataInputStream is = new DataInputStream(s.getInputStream());
             DataOutputStream os = new DataOutputStream(s.getOutputStream()))
        {
            Session ses = new Session(user);
            if (openSession(ses, is, os, in))
            {
                try
                {
                    ClientThread clientThread = new ClientThread(s, is, os);
                    clientThread.start();
                    printPrompt();
                    while (true)
                    {
                        XmlMessage msg = getCommand(ses, in);
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
            //System.err.println(e);
        }
    }


    static XmlMessageResult sendMessage(XmlMessage msg, DataInputStream is, DataOutputStream os) throws IOException, JAXBException, MessageException, InvalidSchemaException
    {
        if (msg == null)
            return null;

        os.writeUTF(Xml.toXml(msg));
        return (XmlMessageResult) Xml.fromXml(Xml.getResultClass(msg.getClass()), is.readUTF(), ValidationRequester.Client, ValidationType.NONE);
    }

    static void saveData(String fname, String data) throws IOException
    {
        File file = new File(fname);
        if (!file.exists())
            file.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(file))
        {
            fos.write(data.getBytes());
            fos.flush();
        }
    }

    static void saveXmlSchema(XmlMessageConnectResult mcr, DataInputStream is)
    {

        try
        {
            //System.out.println(mcr.getSchemaCount());
            for (int i = 0; i < mcr.getSchemaCount(); i++)
            {

                String fName = is.readUTF();
                //System.out.print(fName + " ");
                String fData = is.readUTF();
                //System.out.println(fData);
                saveData(Directory.xsdDirClient + File.separator + fName, fData);
                fName = is.readUTF();
                //System.out.print(fName + " ");
                fData = is.readUTF();
                //System.out.println(fData);
                saveData(Directory.dtdDirClient + File.separator + fName, fData);
            }
            System.err.println("XML updated successfully...");

        } catch (IOException e)
        {
            System.err.println("XML not updated...");
            System.exit(1);
        }
    }


    static boolean openSession(Session ses, DataInputStream is, DataOutputStream os, Scanner in)
            throws IOException, ClassNotFoundException, MessageException, InvalidSchemaException, JAXBException
    {
        XmlMessageConnectResult mcr = null;
        if ((mcr = (XmlMessageConnectResult) sendMessage(new XmlMessageConnect(ses.userIP), is, os)).getID() == Res.OK)
        {
            System.err.println("Server connected");
            saveXmlSchema(mcr, is);
            ses.connected = true;
            os.writeByte(Protocol.CMD_LAST_NEWS);
            os.writeUTF(Xml.toXml(new XmlMessageNews(Protocol.CMD_LAST_NEWS, new Date())));
            System.out.println(Xml.toXml(mcr));
            return true;
        }
        System.err.println("Unable to connect: " + mcr.getErrorMessage());
        System.err.println("Press <Enter> to continue...");
        if (in.hasNextLine())
            in.nextLine();
        return false;
    }

    static void closeSession(Session ses, DataOutputStream os) throws IOException, CommandException, JAXBException
    {
        if (ses.connected)
        {
            ses.connected = false;
            ses.userIP = null;
        }
        if (!stopFlag)
        {
            os.writeByte(Protocol.CMD_DISCONNECT);
            os.writeUTF(Xml.toXml(new XmlMessageDisconnect()));

        }
        stopFlag = true;
    }

    static XmlMessage getCommand(Session ses, Scanner in) throws CommandException
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
                    return new XmlMessageDisconnect();
                case Protocol.CMD_ALL_NEWS:
                    return new XmlMessageNews(Protocol.CMD_ALL_NEWS, new Date());

                case Protocol.CMD_SELECTED_NEWS:
                    Date date = new Date();
                    System.out.print("¬ведите день: ");


                    date.setDate(in.nextInt());
                    System.out.print("¬ведите мес€ц: ");
                    date.setMonth(in.nextInt() - 1);
                    System.out.print("¬ведите год: ");
                    date.setYear(in.nextInt() - 1900);

                    return new XmlMessageNews(Protocol.CMD_SELECTED_NEWS, date);

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

    static boolean processCommand(Session ses, XmlMessage msg,
                                  DataOutputStream os)
            throws IOException, JAXBException
    {
        if (msg != null)
        {
            synchronized (syncOS)
            {
                os.writeByte(msg.getId());
                os.writeUTF(Xml.toXml(msg));
            }

        }
        if (msg.getId() == Protocol.CMD_DISCONNECT)
            return false;
        else
            return true;
    }


}
