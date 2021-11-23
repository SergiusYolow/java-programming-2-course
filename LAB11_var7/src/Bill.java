import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;


public class Bill implements Serializable
{
    // prompts
    public static final String authorDel = ",";
    public static final String areaDel = "\n";
    static final String P_houseNumber = "House";
    static final String P_apartment = "Apartment";
    static final String P_address = "Address";
    static final String P_fullName = "Full name";
    static final String P_date = "Date";
    static final String P_Price = "Price";
    static final String P_Fine = "Fine";
    static final String P_delay = "Delay";

    // class release version:
    private static final long serialVersionUID = 1L;

    // areas
    int houseNumber;            ///
    int apartment;              ///
    Date date = new Date();     ///
    String address;
    String fullName;            ///
    String price;
    String fine;
    String delay;


    public Bill() { }

    public Bill(String houseNumber, String apartment, String date, String address, String fullName, String price, String fine, String delay) throws IOException
    {
        if (houseNumber.length() == 0)
            throw new IllegalArgumentException("Empty house number value");
        else
        {
            if (ValidHouseNumber(houseNumber))
                this.houseNumber = Integer.valueOf(houseNumber);
            else
                throw new IOException("Invalid house number value");
        }

        if (apartment.length() == 0)
            throw new IllegalArgumentException("Empty apartment value");
        else
        {
            if (ValidHouseNumber(apartment))
                this.apartment = Integer.valueOf(apartment);
            else
                throw new IOException("Invalid houseNumber value");
        }

        if (address.length() != 0)
            this.address = address;
        else
            throw new IllegalArgumentException("Empty address value");

        if (fullName.length() != 0)
            this.fullName = fullName;
        else
            throw new IllegalArgumentException("Empty full name value");

        if (delay.length() != 0)
            this.delay = delay;
        else
            throw new IllegalArgumentException("Empty delay value");

        if (fine.length() != 0)
            this.fine = fine;
        else
            throw new IllegalArgumentException("Empty fine value");

        if (price.length() != 0)
            this.price = price;
        else
            throw new IllegalArgumentException("Empty price value");

        if (date.length() != 0)
        {
            if (ValidDate(date))
            {
                int end = date.indexOf('.');
                String lex = date.substring(0, end++);
                this.date.setDate(Integer.valueOf(lex));
                lex = date.substring(end, date.lastIndexOf('.'));
                this.date.setMonth(Integer.valueOf(lex) - 1);
                lex = date.substring(date.lastIndexOf('.') + 1, date.length());
                this.date.setYear(Integer.valueOf(lex) - 1900);
                this.date.setHours(0);
                this.date.setMinutes(0);
                this.date.setSeconds(0);
            }
            else
                throw new IOException("Invalid date value");
        }
        else
            throw new IllegalArgumentException("Empty date value");


    }


    public static Boolean nextRead(Scanner fin, PrintStream out)
    {
        return nextRead(P_houseNumber, fin, out);
    }

    static Boolean nextRead(final String prompt, Scanner fin, PrintStream out)
    {
        out.print(prompt);
        out.print(": ");
        return fin.hasNextLine();
    }


    public static Bill read(Scanner fin, PrintStream out)
            throws IOException
    {
        String temp;
        Bill bill = new Bill();

        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        else
        {
            if (ValidHouseNumber(temp))
                bill.houseNumber = Integer.valueOf(temp);
            else
                throw new IOException("Invalid Book.houseNumber value");
        }

        if (!nextRead(P_apartment, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        else
        {
            if (ValidApartment(temp))
                bill.apartment = Integer.valueOf(temp);
            else
                throw new IOException("Invalid Book.apartment value");
        }


        if (!nextRead(P_address, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() != 0)
            bill.address = temp;
        else
            return null;


        if (!nextRead(P_fullName, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() != 0)
            bill.fullName = temp;
        else
            return null;


        if (!nextRead(P_date, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() != 0)
        {
            if (ValidDate(temp))
            {
                int end = temp.indexOf('.');
                String lex = temp.substring(0, end++);
                bill.date.setDate(Integer.valueOf(lex));
                lex = temp.substring(end, temp.lastIndexOf('.'));
                bill.date.setMonth(Integer.valueOf(lex) - 1);
                lex = temp.substring(temp.lastIndexOf('.') + 1, temp.length());
                bill.date.setYear(Integer.valueOf(lex) - 1900);
                bill.date.setHours(0);
                bill.date.setMinutes(0);
                bill.date.setSeconds(0);
            }
            else
                throw new IOException("Invalid Book.date value");
        }
        else
            return null;


        if (!nextRead(P_Price, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() != 0)
            bill.price = temp;
        else
            return null;


        if (!nextRead(P_Fine, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() != 0)
            bill.fine = temp;
        else
            return null;


        if (!nextRead(P_delay, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() != 0)
            bill.delay = temp;
        else
            return null;

        return bill;
    }

    static boolean ValidHouseNumber(String temp)
    {
        if (temp.length() == 0)
            return false;
        for (int i = 0; i < temp.length(); i++)
            if (Character.isDigit(temp.charAt(i)) == false)
                return false;
        return true;
    }

    static boolean ValidApartment(String temp)
    {
        if (temp.length() == 0)
            return false;
        for (int i = 0; i < temp.length(); i++)
            if (Character.isDigit(temp.charAt(i)) == false)
                return false;
        return true;
    }

    static boolean ValidDate(String temp)
    {
        if (temp.length() == 0)
            return false;
        int one = temp.indexOf('.');
        int two = temp.lastIndexOf('.');
        if (one == -1 || two == -1)
            return false;
        String lex = temp.substring(0, one);
        if (lex.length() == 0 || lex.length() > 2)
            return false;
        if (!ValidHouseNumber(lex))
            return false;
        int tmp = Integer.valueOf(lex);
        if (tmp <= 0 || tmp > 31)
            return false;

        lex = temp.substring(one + 1, two);
        if (lex.length() == 0 || lex.length() > 2)
            return false;
        if (!ValidHouseNumber(lex))
            return false;
        tmp = Integer.valueOf(lex);
        if (tmp <= 0 || tmp > 12)
            return false;

        lex = temp.substring(two + 1, temp.length());
        if (lex.length() == 0 || lex.length() > 4)
            return false;
        if (!ValidHouseNumber(lex))
            return false;
        tmp = Integer.valueOf(lex);
        Date date = new Date();
        if (tmp <= 0 || tmp > (date.getYear() + 1900))
            return false;

        return true;


    }

    String GetFine()
    {
        return (fine + '%');
    }


    public String toString()
    {
        Formatter f = new Formatter();
        SimpleDateFormat d = new SimpleDateFormat("dd.MM.yyyy");
        f.format("House number: %-5s Apartment: %-7s Address: %-25s Fullname: %-30s Date: %-13s Price: %-6s Fine: %-5s Delay: %-5s", houseNumber, apartment, address, fullName, d.format(date), price, GetFine(), delay);
        return f.toString();

    }


}
