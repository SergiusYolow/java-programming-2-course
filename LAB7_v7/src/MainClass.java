import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class MainClass
{

    public static void main(String[] args)
    {
        try
        {
            if (args.length >= 1)
            {
                if (args[0].compareTo("-a") == 0)
                {
                    // Append file with new object from System.in
                    append_file();
                }
                else if (args[0].compareTo("-p") == 0)
                {
                    // Prints data file
                    print_file();
                }
                else if (args[0].compareTo("-d") == 0)
                {
                    // Delete data file
                    delete_file();
                }
                else
                {
                    System.err.println("Option is not realised: " + args[0]);
                    System.exit(1);
                }
            }
            else
            {
                if (args.length == 0)
                {
                    System.out.print("¬ведите консольную команду: ");
                    String temp = fin.nextLine();
                    if (temp.length() >= 1)
                    {
                        if (temp.compareTo("-a") == 0)
                        {
                            // Append file with new object from System.in
                            append_file();
                        }
                        else if (temp.compareTo("-p") == 0)
                        {
                            // Prints data file
                            print_file();
                        }
                        else if (temp.compareTo("-d") == 0)
                        {

                            // Delete data file
                            delete_file();
                        }
                        else
                        {
                            System.err.println("Option is not realised: " + temp);
                            System.exit(1);
                        }
                    }
                    ;
                }
            }
        } catch (Exception e)
        {
            System.err.println("Runtime error: " + e);
            System.exit(1);
        }

        System.exit(0);
    }

    static final String filename = "Bills.dat";

    public static Scanner fin = new Scanner(System.in);

    static Bill read_bills()
    {
        if (fin.hasNextLine())
        {
            return Bill.read(fin);
        }
        return null;
    }

    static void delete_file()
    {
        File f = new File(filename);
        f.delete();
    }

    static void append_file() throws FileNotFoundException, IOException
    {
        Bill book;
        System.out.println("Enter bill data: ");
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            while ((book = read_bills()) != null)
            {
                Bufferer.writeObject(raf, book);
            }
        }
    }

    static void print_file() throws FileNotFoundException, IOException, ClassNotFoundException
    {
        boolean option = true;
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw"))
        {
            long pos;
            while ((pos = raf.getFilePointer()) < raf.length())
            {
                Bill book = (Bill) Bufferer.readObject(raf, pos);
                System.out.println(book);
                option = false;
            }
        }
        if (option)
            System.out.println("file is empty!");
    }
}
