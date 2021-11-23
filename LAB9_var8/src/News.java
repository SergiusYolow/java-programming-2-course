import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;


public class News implements Serializable
{
    // prompts
    public static final String authorDel = ",";
    public static final String areaDel = "\n";
    static final String P_title = "Title";
    static final String P_information = "Information";


    // class release version:
    private static final long serialVersionUID = 1L;

    // areas
    Date date = null;
    String title;
    String information;


    public News() { }

    public News(String t,String i,Date d)
    {
        title=t;
        information=i;
        date=d;
    }


    public static Boolean nextRead(Scanner fin, PrintStream out)
    {
        return nextRead(P_title, fin, out);
    }

    static Boolean nextRead(final String prompt, Scanner fin, PrintStream out)
    {
        out.print(prompt);
        out.print(": ");
        return fin.hasNextLine();
    }


    public static News read(Scanner fin, PrintStream out)
            throws IOException
    {
        String temp;
        News news = new News();

        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        else
        {
            news.title = temp;
        }

        if (!nextRead(P_information, fin, out)) return null;
        temp = fin.nextLine();
        if (temp.length() == 0)
            return null;
        else
        {
            news.information = temp;
        }
        news.date = new Date();

        return news;
    }


    public String toString()
    {
        Formatter f = new Formatter();
        SimpleDateFormat d = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
        f.format("\t%s    %s\n%s", title,d.format(date), information);
        return f.toString();

    }


}
