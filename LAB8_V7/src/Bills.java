import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

public class Bills
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length >= 1)
            {
                if (args[0].equals("-?") || args[0].equals("-h"))
                {
                    System.out.println(
                            "Comands:\n" +
                                    "\t-a                        - append data\n" +
                                    "\t-d                        - clear all data\n" +
                                    "\t-dk  {hn|a|fn|d} key      - clear data by key\n" +
                                    "\t-p                        - print data unsorted\n" +
                                    "\t-ps  {hn|a|fn|d}          - print data sorted by key\n" +
                                    "\t-psr {hn|a|fn|d}          - print data reverse sorted by key\n" +
                                    "\t-f   {hn|a|fn|d} key      - find record by key\n" +
                                    "\t-fr  {hn|a|fn|d} key      - find records > key\n" +
                                    "\t-fl  {hn|a|fn|d} key      - find records < key\n" +
                                    "\t-?, -h                    - command line syntax\n"
                    );
                }
                else if (args[0].equals("-a"))
                {
                    // Append file with new object from System.in
                    // -a [file [encoding]]
                    appendFile(args, false);
                }
                else if (args[0].equals("-p"))
                {
                    // Prints data file
                    printFile();
                }
                else if (args[0].equals("-ps"))
                {
                    // Prints data file sorted by key
                    if (printFile(args, false) == false)
                    {
                        System.exit(1);
                    }
                }
                else if (args[0].equals("-psr"))
                {
                    // Prints data file reverse-sorted by key
                    if (printFile(args, true) == false)
                    {
                        System.exit(1);
                    }
                }
                else if (args[0].equals("-d"))
                {
                    // delete files
                    if (args.length != 1)
                    {
                        System.err.println("Invalid number of arguments");
                        System.exit(1);
                    }
                    deleteFile();
                }
                else if (args[0].equals("-dk"))
                {
                    // Delete records by key
                    if (deleteFile(args) == false)
                    {
                        System.exit(1);
                    }
                }
                else if (args[0].equals("-f"))
                {
                    // Find record(s) by key
                    if (findByKey(args) == false)
                    {
                        System.exit(1);
                    }
                }
                else if (args[0].equals("-fr"))
                {
                    // Find record(s) by key large then key
                    if (args[1].equals("hn") || args[1].equals("a"))
                    {
                        if (findByKey(args, new KeyCompIntegerReverse()) == false)
                        {
                            System.exit(1);
                        }
                    }
                    else
                    {
                        if (findByKey(args, new KeyCompStringReverse()) == false)
                        {
                            System.exit(1);
                        }
                    }

                }
                else if (args[0].equals("-fl"))
                {
                    // Find record(s) by key less then key
                    if (args[1].equals("hn") || args[1].equals("a"))
                    {
                        if (findByKey(args, new KeyCompInteger()) == false)
                        {
                            System.exit(1);
                        }
                    }
                    else
                    {
                        if (findByKey(args, new KeyCompString()) == false)
                        {
                            System.exit(1);
                        }
                    }
                }
                else
                {
                    System.err.println("Option is not realised: " + args[0]);
                    System.exit(1);
                }
            }
            else
            {
                System.err.println("Bills: Nothing to do! Enter -? for options");
            }
        } catch (Exception e)
        {
            System.err.println("Run/time error: " + e);
            System.exit(1);
        }
        System.err.println("Bills finished...");
        System.exit(0);
    }

    static final String filename = "Bills.dat";
    static final String idxname = "Bills.idx";

    static final String filenameBak = "Bills.~dat";
    static final String idxnameBak = "Bills.~idx";

    // input file encoding:
    private static String encoding = "Cp1251";
    private static PrintStream billOut = System.out;

    static Bill readBill(Scanner fin) throws IOException
    {
        return Bill.nextRead(fin, billOut)
                ? Bill.read(fin, billOut) : null;
    }

    static void deleteFile()
    {
        deleteBackup();
        new File(filename).delete();
        new File(idxname).delete();
    }

    private static void deleteBackup()
    {
        new File(filenameBak).delete();
        new File(idxnameBak).delete();
    }

    private static void backup()
    {
        deleteBackup();
        new File(filename).renameTo(new File(filenameBak));
        new File(idxname).renameTo(new File(idxnameBak));
    }

    static boolean deleteFile(String[] args)
            throws ClassNotFoundException, IOException, KeyNotUniqueException
    {
        //-dk  {i|a|n} key      - clear data by key
        if (args.length < 3)
        {
            System.err.println("Invalid number of arguments");
            return false;
        }

        String str = args[2];
        for (int i = 3; i < args.length; i++)
            str += " " + args[i];


        long[] poss = null;
        try (Index idx = Index.load(idxname))
        {
            IndexBase pidx = indexByArg(args[1], idx);
            if (pidx == null)
            {
                return false;
            }
            if (pidx.contains(str) == false)
            {
                System.err.println("Key not found: " + str);
                return false;
            }
            Vector<Long> temp = pidx.get(str);
            poss = new long[temp.size()];
            int k = 0;
            for (Long t : temp)
                poss[k] = t;

        }
        backup();
        Arrays.sort(poss);
        try (Index idx = Index.load(idxname);
             RandomAccessFile fileBak = new RandomAccessFile(filenameBak, "rw");
             RandomAccessFile file = new RandomAccessFile(filename, "rw"))
        {
            boolean[] wasZipped = new boolean[]{false};
            long pos;
            while ((pos = fileBak.getFilePointer()) < fileBak.length())
            {
                Bill bill = (Bill)
                        FileComands.readObject(fileBak, pos, wasZipped);
                if (Arrays.binarySearch(poss, pos) < 0)
                { // if not found in deleted
                    long ptr = FileComands.writeObject(file, bill, wasZipped[0]);
                    idx.put(bill, ptr);
                }
            }
        }
        return true;
    }

    static void appendFile(String[] args, Boolean zipped)
            throws IOException, ClassNotFoundException,
            KeyNotUniqueException
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
        appendFile(zipped);
    }

    static void appendFile(Boolean zipped)
            throws IOException, ClassNotFoundException,
            KeyNotUniqueException
    {
        Scanner fin = new Scanner(System.in, encoding);
        //billOut.println( "Enter bill data: " );
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            for (; ; )
            {
                Bill bill = readBill(fin);
                if (bill == null)
                    break;
                idx.test(bill);
                long pos = FileComands.writeObject(raf, bill, zipped);
                idx.put(bill, pos);
            }
        }
    }

    private static void printRecord(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException
    {
        boolean[] wasZipped = new boolean[]{false};
        Bill bill = (Bill) FileComands.readObject(raf, pos, wasZipped);
        if (wasZipped[0] == true)
        {
            System.out.print(" compressed");
        }
        System.out.println(bill);
    }

    private static void printRecord(RandomAccessFile raf, String key,
                                    IndexBase pidx) throws ClassNotFoundException, IOException
    {
        Vector<Long> temp = pidx.get(key);
        long[] poss = new long[temp.size()];
        int k = 0;
        for (Long t : temp)
            poss[k] = t;
        for (long pos : poss)
        {
            printRecord(raf, pos);
        }
    }

    static void printFile()
            throws IOException, ClassNotFoundException
    {
        long pos;
        int rec = 0;
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            while ((pos = raf.getFilePointer()) < raf.length())
            {
                rec++;
                //System.out.print("#" + (rec) + " RECORD:  ");
                printRecord(raf, pos);
            }
            System.out.flush();
        }
        if (rec == 0)
            System.err.println("File is empty!");
    }

    private static IndexBase indexByArg(String arg, Index idx)
    {
        IndexBase pidx = null;
        if (arg.equals("hn"))
        {
            pidx = idx.houseNumbers;
        }
        else if (arg.equals("a"))
        {
            pidx = idx.apartments;
        }
        else if (arg.equals("fn"))
        {
            pidx = idx.fullNames;
        }
        else if (arg.equals("d"))
        {
            pidx = idx.dates;
        }
        else
        {
            System.err.println("Invalid index specified: " + arg);
        }
        return pidx;
    }

    static boolean printFile(String[] args, boolean reverse)
            throws ClassNotFoundException, IOException
    {
        if (args.length != 2)
        {
            System.err.println("Invalid number of arguments");
            return false;
        }
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            String title = "";
            IndexBase pidx = indexByArg(args[1], idx);
            if (pidx == null)
            {
                return false;
            }

            String[] keys = null;

            switch (args[1])
            {
                case "hn":
                    keys = pidx.getKeys(reverse ? new KeyCompIntegerReverse() : new KeyCompInteger());
                    title = "Sorted by house number";
                    break;
                case "a":
                    keys = pidx.getKeys(reverse ? new KeyCompIntegerReverse() : new KeyCompInteger());
                    title = "Sorted by apartment";
                    break;
                case "fn":
                    keys = pidx.getKeys(reverse ? new KeyCompStringReverse() : new KeyCompString());
                    title = "Sorted by full name";
                    break;
                case "d":
                    keys = pidx.getKeys(reverse ? new KeyCompStringReverse() : new KeyCompString());
                    title = "Sorted by date";
                    break;
            }
            if (keys.length != 0)
            {
                System.err.println(title);
                for (String key : keys)
                {
                    printRecord(raf, key, pidx);
                }
            }
            else
                System.err.println("File is Empty!");
        }
        return true;
    }

    static boolean findByKey(String[] args)
            throws ClassNotFoundException, IOException
    {
        if (args.length < 3)
        {
            System.err.println("Invalid number of arguments");
            return false;
        }
        String key = args[2];
        for (int i = 3; i < args.length; i++)
            key += " " + args[i];
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            IndexBase pidx = indexByArg(args[1], idx);
            if ((pidx.contains(key)) == false)
            {
                System.err.println("Key not found: " + key);
                return false;
            }
            printRecord(raf, key, pidx);
        }
        return true;
    }

    static boolean findByKey(String[] args, Comparator<String> comp)
            throws ClassNotFoundException, IOException
    {
        if (args.length < 3)
        {
            System.err.println("Invalid number of arguments");
            return false;
        }

        String key = args[2];
        for (int i = 3; i < args.length; i++)
            key += " " + args[i];

        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            IndexBase pidx = indexByArg(args[1], idx);
//            if (pidx.contains(key) == false)
//            {
//                System.err.println("Key not found: " + key);
//                return false;
//            }
            String[] keys = pidx.getKeys(comp);
            if (args[0].equals("-fl"))
                for (int i = 0; i < keys.length; i++)
                {
                    String ke = keys[i];
                    if (ke.compareTo(key) >= 0)
                    {
                        break;
                    }
                    printRecord(raf, ke, pidx);
                }
            else
                for (int i = 0; i < keys.length; i++)
                {
                    String ke = keys[i];
                    if (ke.compareTo(key) <= 0)
                    {
                        break;
                    }
                    printRecord(raf, ke, pidx);
                }
        }
        return true;
    }
}
