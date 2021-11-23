import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

public class MainClass
{
    static final String newsFilename = "News.dat";
    static final String newsFilenameBak = "News.~dat";
    static final String newsIdxname = "News.idx";
    static final String newsIdxnameBak = "News.~idx";
    static final String clientsFilename = "Clients.dat";
    static final String clientsFilenameBak = "ClientsBack.dat";
    static final String clientsIdxname = "Clients.idx";
    static final String clientsIdxnameBak = "ClientsBack.idx";

    public static Object syncClients = new Object();
    public static Object syncNews = new Object();


    // input file encoding:
    private static String encoding = "Cp1251";
    private static PrintStream billOut = System.out;

    static News readBill(Scanner fin) throws IOException
    {
        return News.nextRead(fin, billOut)
                ? News.read(fin, billOut) : null;
    }

    static void deleteFile()
    {
        deleteBackup();
        new File(newsFilename).delete();
        new File(newsIdxname).delete();
    }

    private static void deleteBackup()
    {
        new File(clientsFilenameBak).delete();
        new File(clientsIdxnameBak).delete();
    }

    private static void backup()
    {
        deleteBackup();
        new File(clientsFilename).renameTo(new File(clientsFilenameBak));
        new File(clientsIdxname).renameTo(new File(clientsIdxnameBak));
    }

    static boolean deleteClient(String userIP) throws IOException
    {
        synchronized (syncClients)
        {
            if (userIP == null)
            {
                System.err.println("Invalid argument");
                return false;
            }
            long[] poss = null;
            try (Index idx = Index.load(clientsIdxname))
            {
                IndexBase pidx = indexClients(idx);
                if (pidx == null)
                {
                    return false;
                }
                if (pidx.contains(userIP) == false)
                {

                    return false;
                }
                poss = pidx.get(userIP);
            }
            backup();
            Arrays.sort(poss);
            try (Index idx = Index.load(clientsIdxname);
                 RandomAccessFile fileBak = new RandomAccessFile(clientsFilenameBak, "rw");
                 RandomAccessFile file = new RandomAccessFile(clientsFilename, "rw"))
            {
                boolean[] wasZipped = new boolean[]{false};
                long pos;
                while ((pos = fileBak.getFilePointer()) < fileBak.length())
                {
                    Client bill = (Client)
                            Buffer.readObject(fileBak, pos, wasZipped);
                    if (Arrays.binarySearch(poss, pos) < 0)
                    { // if not found in deleted
                        long ptr = Buffer.writeObject(file, bill, wasZipped[0]);
                        idx.putClient(bill, ptr);
                    }
                }
                deleteBackup();
            } catch (KeyNotUniqueException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            return true;
        }
    }

    static void appendNewsFile(String[] args, Boolean zipped)
            throws IOException, KeyNotUniqueException
    {
        if (args.length >= 2)
        {
            FileInputStream stdin = new FileInputStream(args[1]);
            System.setIn(stdin);
            if (args.length == 3)
            {
                encoding = args[2];
            }
            // hide output:
            billOut = new PrintStream("nul");
        }
        appendNewsFile();
    }

    static Date appendNewsFile()
            throws IOException, KeyNotUniqueException
    {
        synchronized (syncNews)
        {
            Scanner fin = new Scanner(System.in);
            billOut.println("Enter news data: ");
            try (Index idx = Index.load(newsIdxname);
                 RandomAccessFile raf = new RandomAccessFile(newsFilename, "rw"))
            {
                News news = readBill(fin);
                if (news == null)
                    return null;
                long pos = Buffer.writeObject(raf, news, false);
                idx.putDate(news, pos);
                return news.date;
            }
        }
    }

    static void appendClientFile(Client client)
            throws IOException
    {
        synchronized (syncClients)
        {
            try (Index idx = Index.load(clientsIdxname);
                 RandomAccessFile raf = new RandomAccessFile(clientsFilename, "rw"))
            {
                if (idx.test(client))
                {
                    long pos = Buffer.writeObject(raf, client, false);
                    idx.putClient(client, pos);
                }
            } catch (KeyNotUniqueException e)
            {
                e.printStackTrace();
            }
        }

    }

    static void appendNewsFile(News news)
            throws IOException
    {
        synchronized (syncClients)
        {
            try (Index idx = Index.load(newsIdxname);
                 RandomAccessFile raf = new RandomAccessFile(newsFilename, "rw"))
            {

                long pos = Buffer.writeObject(raf, news, false);
                idx.putDate(news, pos);

            } catch (KeyNotUniqueException e)
            {
                e.printStackTrace();
            }
        }

    }

    private static void printNews(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException
    {
        boolean[] wasZipped = new boolean[]{false};
        News news = (News) Buffer.readObject(raf, pos, wasZipped);
        if (wasZipped[0] == true)
        {
            System.out.print(" compressed");
        }
        System.out.println(news);
    }


    private static void printClient(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException
    {
        boolean[] wasZipped = new boolean[]{false};
        Client client = (Client) Buffer.readObject(raf, pos, wasZipped);
        if (wasZipped[0] == true)
        {
            System.out.print(" compressed");
        }
        System.out.println(client);
    }

    private static void printRecord(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException
    {
        boolean[] wasZipped = new boolean[]{false};
        News news = (News) Buffer.readObject(raf, pos, wasZipped);
        if (wasZipped[0] == true)
        {
            System.out.print(" compressed");
        }
        System.out.println(news);
    }

    static News[] getAllNews()

    {
        synchronized (syncNews)
        {
            RandomAccessFile raf = null;
            try
            {
                raf = new RandomAccessFile(newsFilename, "rw");
                long pos;
                Vector<News> temp = new Vector<News>();
                boolean[] wasZipped = new boolean[]{false};
                while ((pos = raf.getFilePointer()) < raf.length())
                {
                    temp.add((News) Buffer.readObject(raf, pos, wasZipped));


                }
                return (temp.toArray(new News[0]));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void printRecord(RandomAccessFile raf, String key,
                                    IndexBase pidx) throws ClassNotFoundException, IOException
    {
        long[] poss = pidx.get(key);

        for (long pos : poss)
        {
            printRecord(raf, pos);
        }
    }


    static void printClientsFile()
            throws FileNotFoundException, IOException, ClassNotFoundException
    {
        long pos = 0;
        int rec = 0;
        try (RandomAccessFile raf = new RandomAccessFile(clientsFilename, "rw"))
        {
            while ((pos = raf.getFilePointer()) < raf.length())
            {
                //System.out.println("#" + (++rec) + " RECORD:  ");
                printClient(raf, pos);

            }
            if (pos == 0)
                System.out.println("ClientsFile is Empty!");
            System.out.flush();
        }
    }

    static void printNewsFile()
            throws FileNotFoundException, IOException, ClassNotFoundException
    {
        long pos = 0;
        int rec = 0;
        try (RandomAccessFile raf = new RandomAccessFile(newsFilename, "rw"))
        {
            while ((pos = raf.getFilePointer()) < raf.length())
            {
                //System.out.println("#" + (++rec) + " RECORD:  ");
                printNews(raf, pos);

            }
            if (pos == 0)
                System.out.println("NewsFile is Empty!");
            System.out.flush();
        }
    }


    static boolean printFile(String[] args, boolean reverse)
            throws ClassNotFoundException, IOException
    {
        if (args.length != 2)
        {
            System.err.println("Invalid number of arguments");
            return false;
        }
        try (Index idx = Index.load(newsIdxname);
             RandomAccessFile raf = new RandomAccessFile(newsFilename, "rw"))
        {
            IndexBase pidx = indexDates(idx);
            if (pidx == null)
            {
                return false;
            }

            String[] keys = null;
            switch (args[1])
            {
                case "hn":
                    keys = pidx.getKeys(reverse ? new KeyCompIntegerReverse() : new KeyCompInteger());
                    System.err.println("Sorted by house number");
                    break;
                case "a":
                    keys = pidx.getKeys(reverse ? new KeyCompIntegerReverse() : new KeyCompInteger());
                    System.err.println("Sorted by apartment");
                    break;
                case "fn":
                    keys = pidx.getKeys(reverse ? new KeyCompStringReverse() : new KeyCompString());
                    System.err.println("Sorted by full name");
                    break;
                case "d":
                    keys = pidx.getKeys(reverse ? new KeyCompStringReverse() : new KeyCompString());
                    System.err.println("Sorted by date");
                    break;
            }
            for (String key : keys)
            {
                printRecord(raf, key, pidx);
            }
        }
        return true;
    }

    static boolean findByKey(String[] args, KeyCompString keyCompString)
            throws ClassNotFoundException, IOException
    {
        if (args.length != 3)
        {
            System.err.println("Invalid number of arguments");
            return false;
        }
        try (Index idx = Index.load(newsIdxname);
             RandomAccessFile raf = new RandomAccessFile(newsFilename, "rw"))
        {
            IndexBase pidx = indexDates(idx);
            if (pidx.contains(args[2]) == false)
            {
                System.err.println("Key not found: " + args[2]);
                return false;
            }
            printRecord(raf, args[2], pidx);
        }
        return true;
    }

    static boolean dateCheck(String a, Date b)
    {
        SimpleDateFormat d = new SimpleDateFormat("yyyy.MM.dd");
        String temp = a.substring(0, 10);
        if (temp.compareTo(d.format(b))==0)
            return true;
        else
            return false;
    }

    static boolean lastCheck(String a, Date b)
    {
        SimpleDateFormat d = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String temp = d.format(b);
        for (int i = 0; i < temp.length(); i++)
            if (a.charAt(i) > temp.charAt(i))
                return true;
            else if (a.charAt(i) < temp.charAt(i))
                return false;
        return false;

    }


    static News[] getNewsByDate(Date date)

    {
        synchronized (syncNews)
        {

            Index idx = Index.load(newsIdxname);
            RandomAccessFile raf = null;
            try
            {
                raf = new RandomAccessFile(newsFilename, "rw");
                IndexBase pidx = indexDates(idx);
                Vector<News> temp = new Vector<News>();
                String[] keys = pidx.getKeys(new KeyCompString());
                for (int i = 0; i < keys.length; i++)
                {
                    String key = keys[i];
                    if (dateCheck(key, date))
                    {
                        News[] news = getNews(raf, key, pidx);
                        for (News n : news)
                            temp.add(n);
                    }

                }
                return temp.toArray(new News[0]);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }

    static News[] getLastNews(Date date)

    {
        synchronized (syncNews)
        {
            //boolean[] wasZipped = new boolean[]{false};
            Index idx = Index.load(newsIdxname);
            RandomAccessFile raf = null;
            try
            {
                raf = new RandomAccessFile(newsFilename, "rw");
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            IndexBase pidx = indexDates(idx);
            Vector<News> temp = new Vector<News>();
            String[] keys = pidx.getKeys(new KeyCompString());
            SimpleDateFormat d = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            for (int i = 0; i < keys.length; i++)
            {
                String key = keys[i];
                if (key.compareTo(d.format(date)) > 0)
                {
                    News[] news = getNews(raf, key, pidx);
                    for (News n : news)
                        temp.add(n);
                }

            }
            return temp.toArray(new News[0]);
        }

    }

    static Date getLastVisit(String userIP)

    {
        synchronized (syncClients)
        {
            Client client = null;
            try (Index idx = Index.load(clientsIdxname);
                 RandomAccessFile raf = new RandomAccessFile(clientsFilename, "rw"))
            {
                IndexBase pidx = indexClients(idx);
                if (pidx.contains(userIP) == false)
                {
                    return null;
                }
                client = getClient(raf, userIP, pidx);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return client.lastVisit;
        }
    }

    private static IndexBase indexDates(Index idx)
    {
        IndexBase pidx = null;
        pidx = idx.dates;
        return pidx;
    }

    private static IndexBase indexClients(Index idx)
    {
        IndexBase pidx = null;
        pidx = idx.clients;
        return pidx;
    }

    static boolean findClientByIP(Client client)
            throws ClassNotFoundException, IOException
    {

        try (Index idx = Index.load(clientsIdxname);
             RandomAccessFile raf = new RandomAccessFile(clientsFilename, "rw"))
        {
            IndexBase pidx = indexClients(idx);
            if (pidx.contains(client.ip))
            {
                System.err.println("Key not found: " + client.ip);
                return false;
            }
            String[] keys = pidx.getKeys(new KeyCompInteger());
            for (int i = 0; i < keys.length; i++)
            {
                String key = keys[i];
                if (key.equals(client.ip))
                {
                    break;
                }

            }
        }
        return true;
    }

    private static Client getClient(RandomAccessFile raf, String key, IndexBase pidx)
    {
        long[] poss = pidx.get(key);
        int k = 0;
        boolean[] wasZipped = new boolean[]{false};
        Client client = null;
        try
        {
            client = (Client) Buffer.readObject(raf, poss[0], wasZipped);

        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        if (client != null)
            return client;
        else
            return null;
    }

    private static News[] getNews(RandomAccessFile raf, String key,
                                  IndexBase pidx)
    {
        long[] poss = pidx.get(key);
        News[] temp = new News[poss.length];
        int k = 0;
        boolean[] wasZipped = new boolean[]{false};
        for (long pos : poss)
        {
            try
            {
                temp[k++] = (News) Buffer.readObject(raf, pos, wasZipped);
            } catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return temp;
    }
}
