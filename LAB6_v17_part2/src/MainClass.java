import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MainClass extends TypeClass
{
    static Locale createLocale(String[] args)
    {
        if (args.length == 2)
        {
            return new Locale(args[0], args[1]);
        }
        else
            if (args.length == 4)
            {
                return new Locale(args[2], args[3]);
            } return null;
    }

    static void setupConsole(String[] args)
    {
        if (args.length >= 2)
        {
            if (args[0].compareTo("-encoding") == 0)
            {
                try
                {
                    System.setOut(new PrintStream(System.out, true, args[1]));
                }
                catch (UnsupportedEncodingException ex)
                {
                    System.err.println("Unsupported encoding: " + args[1]); System.exit(1);
                }
            }
        }
    }


    static String[] Names()
    {
        String[] name = new String[4]; name[0] = "Dinamo Stadium — Minsk Arena";
        name[1] = "National Library — Grand Theatre"; name[2] = "Central Park — Art Museum";
        name[3] = "Ñinema Belarus — Galleria Minsk"; return name;
    }

    public static Route[] createTraffic()
    {

        Route[] temp = new Route[4];
        temp[0] = new Route(AppLocale.getString(AppLocale.first_route), TypeClass.Type.BUS, 10, 5);
        temp[1] = new Route(AppLocale.getString(AppLocale.second_route), TypeClass.Type.TRAM, 15, 8);
        temp[2] = new Route(AppLocale.getString(AppLocale.third_route), TypeClass.Type.TROLLEYBUS, 6, 10);
        temp[3] = new Route(AppLocale.getString(AppLocale.fourth_route), TypeClass.Type.TRAM, 8, 12); return temp;
    }

    public static void main(String[] args) throws IOException
    {
        try
        {
            setupConsole(args); Locale loc = createLocale(args); if (loc == null)
        {
            System.err.println("Invalid argument(s)\n" + "Syntax: [-encoding ENCODING_ID] language country\n" +
                               "Example: -encoding Cp855 be BY");
            System.exit(1);
        } AppLocale.set(loc); Connector con = new Connector("Traffic.dat"); con.write(createTraffic());
            Route[] LandTraffic = con.read(); System.out.println(AppLocale.getString(AppLocale.land_trafic) + ": ");
            for (int i = 0; i < 4; i++)
            {
                LandTraffic[i].TraficPrint();
            }

            for (int i = 0; i < 4; i++)
                LandTraffic[3].setTransportStatus(i, false);

            System.out.println(); LandTraffic[3].TraficPrint();
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

}
