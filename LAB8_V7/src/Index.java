import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class KeyCompString implements Comparator<String>
{
    public int compare(String o1, String o2)
    {
        return o1.compareTo(o2);
    }
}

class KeyCompStringReverse implements Comparator<String>
{
    public int compare(String o1, String o2)
    {
        return o2.compareTo(o1);
    }
}

class KeyCompInteger implements Comparator<String>
{
    public int compare(String o1, String o2)
    {
        int a = Integer.valueOf(o1);
        int b = Integer.valueOf(o2);
        if (a > b)
            return 1;
        else
        {
            if (a == b)
                return 0;
            else
                return -1;
        }
    }
}

class KeyCompIntegerReverse implements Comparator<String>
{
    public int compare(String o1, String o2)
    {
        int a = Integer.valueOf(o1);
        int b = Integer.valueOf(o2);
        if (a > b)
            return -1;
        else
        {
            if (a == b)
                return 0;
            else
                return 1;
        }
    }
}

interface IndexBase
{
    String[] getKeys(Comparator<String> comp);

    void put(String key, long value);

    boolean contains(String key);

    Vector<Long> get(String key);
}

class IndexOneToOne implements Serializable, IndexBase
{
    // Unique keys
    // class release version:
    private static final long serialVersionUID = 1L;

    private TreeMap<String, Long> map;

    public IndexOneToOne()
    {
        map = new TreeMap<String, Long>();
    }

    public String[] getKeys(Comparator<String> comp)
    {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value)
    {
        map.put(key, new Long(value));
    }

    public boolean contains(String key)
    {
        return map.containsKey(key);
    }

    public Vector<Long> get(String key)
    {
        Vector<Long> temp = new Vector<Long>();
        long pos = map.get(key).longValue();
        temp.add(pos);
        return temp;
    }
}

class IndexOneToNull implements Serializable, IndexBase
{
    // Not unique keys
    // class release version:
    private static final long serialVersionUID = 1L;

    private TreeMap<String, long[]> map;

    public IndexOneToNull()
    {
        map = new TreeMap<String, long[]>();
    }

    public String[] getKeys(Comparator<String> comp)
    {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value)
    {
        long[] arr = map.get(key);
        arr = (arr != null) ?
                Index.InsertValue(arr, value) :
                new long[]{value};
        map.put(key, arr);
    }

    public void put(String keys,   // few keys in one string
                    String keyDel, // key delimiter
                    long value)
    {
        StringTokenizer st = new StringTokenizer(keys, keyDel);
        int num = st.countTokens();
        for (int i = 0; i < num; i++)
        {
            String key = st.nextToken();
            key = key.trim();
            put(key, value);
        }
    }

    public boolean contains(String key)
    {
        return map.containsKey(key);
    }

    public Vector<Long> get(String key)
    {
        long[] pos = map.get(key);
        Vector<Long> temp = new Vector<Long>();
        for (long t : pos)
            temp.add(t);
        return temp;
    }
}

class KeyNotUniqueException extends Exception
{
    // class release version:
    private static final long serialVersionUID = 1L;

    public KeyNotUniqueException(String key)
    {
        super(new String("Key is not unique: " + key));
    }
}

public class Index implements Serializable, Closeable
{
    // class release version:
    private static final long serialVersionUID = 1L;

    public static long[] InsertValue(long[] arr, long value)
    {
        int length = (arr == null) ? 0 : arr.length;
        long[] result = new long[length + 1];
        for (int i = 0; i < length; i++)
            result[i] = arr[i];
        result[length] = value;
        return result;
    }

    IndexOneToOne fullNames;
    IndexOneToNull dates;
    IndexOneToNull apartments;
    IndexOneToNull houseNumbers;


    public void test(Bill bill) throws KeyNotUniqueException
    {
        assert (bill != null);
        if (fullNames.contains(bill.fullName))
        {
            throw new KeyNotUniqueException(bill.fullName);
        }

    }

    public void put(Bill bill, long value) throws KeyNotUniqueException
    {
        test(bill);
        SimpleDateFormat d = new SimpleDateFormat("yyyy.MM.dd");
        fullNames.put(bill.fullName, value);
        dates.put(d.format(bill.date), Bill.authorDel, value);
        apartments.put(Integer.toString(bill.apartment), Bill.authorDel, value);
        houseNumbers.put(Integer.toString(bill.houseNumber), Bill.authorDel, value);


    }

    public Index()
    {
        fullNames = new IndexOneToOne();
        dates = new IndexOneToNull();
        apartments = new IndexOneToNull();
        houseNumbers = new IndexOneToNull();
    }

    public static Index load(String name)
            throws IOException, ClassNotFoundException
    {
        Index obj = null;
        try
        {
            FileInputStream file = new FileInputStream(name);
            try (ZipInputStream zis = new ZipInputStream(file))
            {
                ZipEntry zen = zis.getNextEntry();
                if (zen.getName().equals(FileComands.zipEntryName) == false)
                {
                    throw new IOException("Invalid block format");
                }
                try (ObjectInputStream ois = new ObjectInputStream(zis))
                {
                    obj = (Index) ois.readObject();
                }
            }
        } catch (FileNotFoundException e)
        {
            obj = new Index();
        }
        if (obj != null)
        {
            obj.save(name);
        }
        return obj;
    }

    private transient String filename = null;

    public void save(String name)
    {
        filename = name;
    }

    public void saveAs(String name) throws IOException
    {
        FileOutputStream file = new FileOutputStream(name);
        try (ZipOutputStream zos = new ZipOutputStream(file))
        {
            zos.putNextEntry(new ZipEntry(FileComands.zipEntryName));
            zos.setLevel(ZipOutputStream.DEFLATED);
            try (ObjectOutputStream oos = new ObjectOutputStream(zos))
            {
                oos.writeObject(this);
                oos.flush();
                zos.closeEntry();
                zos.flush();
            }
        }
    }

    public void close() throws IOException
    {
        saveAs(filename);
    }
}
