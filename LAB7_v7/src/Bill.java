import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Scanner;

public class Bill implements Serializable
{

    private static final long serialVersionUID = 1L;

    String houseNumber;
    String apartment;
    String address;
    String fullName;
    String date;
    String price;
    String fine;
    String delay;

    public static Bill read(Scanner fin)
    {
        Bill bills = new Bill();
        String temp;

        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        if (ValidHouseNumber(temp))
            bills.houseNumber = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        if (ValidApartment(temp))
            bills.apartment = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        bills.address = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        bills.fullName = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        bills.date = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        bills.price = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        bills.fine = temp;


        if (!fin.hasNextLine())
            return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        bills.delay = temp;

        return bills;
    }

    Bill() { }

    static boolean ValidHouseNumber(String temp)
    {
        for (int i = 0; i < temp.length(); i++)
            if (Character.isDigit(temp.charAt(i)) == false)
                return false;
        return true;
    }

    static boolean ValidApartment(String temp)
    {
        for (int i = 0; i < temp.length(); i++)
            if (Character.isDigit(temp.charAt(i)) == false)
                return false;
        return true;
    }


    public String toString()
    {
        Formatter f = new Formatter();
        SimpleDateFormat d = new SimpleDateFormat("dd.MM.yyyy");
        f.format("House number: %-4s Apartment: %-5s Address: %-25s Fullname: %-30s Date: %-13s Price: %-6s Fine: %-5s Delay: %-5s", houseNumber, apartment, address, fullName, date, price, fine, delay);
        return f.toString();
    }

}
