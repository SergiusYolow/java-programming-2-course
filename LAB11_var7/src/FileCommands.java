


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileCommands
{
    static private String filename;
    private String idxname;
    private String filenameBak;
    private String idxnameBak;
    private MyFrame frame;

    public FileCommands()
    {
        filename = "Bills.dat";
        idxname = "Bills.idx";
        filenameBak = "Bills.~dat";
        idxnameBak = "Bills.~idx";
        frame = new MyFrame(this);
    }

    void open(String file)
    {
        if (file.length() == 0)
        {
            throw new IllegalArgumentException("Invalid filename");
        }
        filename = file + ".dat";
        idxname = file + ".idx";
        filenameBak = file + ".~dat";
        idxnameBak = file + "~.idx";
        frame.update(filename);
    }

    static String getFilename()
    {
        String text = filename.substring(0, filename.indexOf("."));
        return text;
    }

    static String getFile()
    {
        return filename;
    }

    void appendFile(Bill bill) throws FileNotFoundException, IOException, ClassNotFoundException,
            KeyNotUniqueException
    {
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {

            idx.test(bill);
            long pos = Buffer.writeObject(raf, bill, false);
            idx.put(bill, pos);
        }
    }


    private Bill printRecord(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException
    {
        boolean[] wasZipped = new boolean[]{false};
        return (Bill) Buffer.readObject(raf, pos, wasZipped);

    }


    private List<String> printRecord(RandomAccessFile raf, String key,
                                     IndexBase pidx) throws ClassNotFoundException, IOException
    {
        List<Long> poss = pidx.get(key);
        List<String> records = new ArrayList<>();
        for (Long pos : poss)
        {
            records.add(printRecord(raf, pos).toString());
        }
        return records;
    }


    void printFile() throws IOException, ClassNotFoundException
    {
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            long pos = raf.getFilePointer();
            List<String> records = new ArrayList<>();
            while (pos < raf.length())
            {
                Bill bill = printRecord(raf, pos);
                records.add(bill.toString());
                pos = raf.getFilePointer();
            }
            frame.showData(records);
            if (records.size()==0)
            {
                throw new IOException("File is empty!   ");
            }
        }
    }

    void printSorted(boolean reverse, int index)
            throws ClassNotFoundException, IOException
    {
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            IndexBase pidx = indexByName(idx, index);
            String[] keys;

            if (index <= 1)
                keys = pidx.getKeys(reverse ? new KeyCompIntegerReverse() : new KeyCompInteger());
            else
                keys = pidx.getKeys(reverse ? new KeyCompStringReverse() : new KeyCompString());

            List<String> records = new ArrayList<>();
            for (String key : keys)
            {
                records.addAll(printRecord(raf, key, pidx));
            }
            frame.showData(records);
            if (records.size()==0)
            {
                throw new IOException("File is empty!   ");
            }
        }
    }

    private IndexBase indexByName(Index index, int number)
    {
        switch (number)
        {
            case 0: //department numbers
                return index.houseNumbers;
            case 1: //full names
                return index.apartments;
            case 2: //employment dates
                return index.fullNames;
            case 3: //
                return index.dates;
            default:
                throw new IllegalArgumentException("Invalid index: " + number);
        }
    }

    void printByKey(int number, String key)
            throws ClassNotFoundException, IOException
    {
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            IndexBase pidx = indexByName(idx, number);

            frame.showData(printRecord(raf, key, pidx));
            if (!pidx.contains(key))
            {
                throw new IllegalArgumentException("Key not found: " + key);
            }
        }
    }

    void printByKey(boolean flag, int index, String key)
            throws ClassNotFoundException, IOException
    {
        try (Index idx = Index.load(idxname);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            IndexBase pidx = indexByName(idx, index);
            String[] keys;
            Comparator<String> comp;
            if (index <= 1)
            {
                keys = pidx.getKeys(flag ? new KeyCompIntegerReverse() : new KeyCompInteger());
                comp = (flag ? new KeyCompIntegerReverse() : new KeyCompInteger());
            }
            else
            {
                keys = pidx.getKeys(flag ? new KeyCompStringReverse() : new KeyCompString());
                comp = (flag ? new KeyCompStringReverse() : new KeyCompString());
            }

            List<String> records = new ArrayList<>();

            for (String cur : keys)
            {
                if (comp.compare(cur, key) >= 0)
                {
                    break;
                }
                records.addAll(printRecord(raf, cur, pidx));
            }
            frame.showData(records);
            if (records.size() == 0)
                throw new IllegalArgumentException("There is not any information from this key: " + key + "   ");


        }
    }


    void delete(int number, String key) throws FileNotFoundException, IOException, ClassNotFoundException,
            KeyNotUniqueException
    {
        List<Long> poss;
        try (Index idx = Index.load(idxname))
        {
            IndexBase pidx = indexByName(idx, number);
            if (!pidx.contains(key))
            {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            poss = pidx.get(key);
        }
        Collections.sort(poss);
        File data = new File(filename), index = new File(idxname), dataBak = new File(filenameBak), indexBak = new File(idxnameBak);
        if ((!dataBak.exists() || dataBak.delete()) && (!indexBak.exists() || indexBak.delete()) && data.renameTo(dataBak) && index.renameTo(indexBak))
        {
            try (Index idx = Index.load(idxname);
                 RandomAccessFile fileBak = new RandomAccessFile(filenameBak, "rw");
                 RandomAccessFile file = new RandomAccessFile(filename, "rw"))
            {
                boolean[] wasZipped = new boolean[]{false};
                long pos;
                while ((pos = fileBak.getFilePointer()) < fileBak.length())
                {
                    Bill bill = (Bill)
                            Buffer.readObject(fileBak, pos, wasZipped);
                    if (Collections.binarySearch(poss, pos) < 0)
                    { // if not found in deleted
                        long ptr = Buffer.writeObject(file, bill, wasZipped[0]);
                        idx.put(bill, ptr);
                    }
                }
            }
        }
    }
}

