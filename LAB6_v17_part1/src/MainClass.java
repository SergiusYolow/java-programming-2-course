import java.io.IOException;

public class MainClass extends TypeClass
{

	static String[] Names()
	{
		String[] name = new String[4];
		name[0] = "Dinamo Stadium — Minsk Arena";
		name[1] = "National Library — Grand Theatre";
		name[2] = "Central Park — Art Museum";
		name[3] = "Cinema Belarus — Galleria Minsk";
		return name;
	}

	public static Route[] createTraffic()
	{
		String[] name = Names();
		Route[] temp = new Route[4];
		temp[0] = new Route(name[0], TypeClass.Type.BUS, 10, 5);
		temp[1] = new Route(name[1], TypeClass.Type.TRAM, 15, 8);
		temp[2] = new Route(name[2], TypeClass.Type.TROLLEYBUS, 6, 10);
		temp[3] = new Route(name[3], TypeClass.Type.TRAM, 8, 12);
		return temp;
	}

	public static void main(String[] args) throws IOException
	{
		try
		{
			Connector con = new Connector("Traffic.dat");
			con.write(createTraffic());
			Route[] LandTraffic = con.read();
			System.out.println("The LandTraffic: ");
			for (int i = 0; i < 4; i++)
			{
				LandTraffic[i].TraficPrint();
			}

			for (int i = 0; i < 4; i++)
				LandTraffic[3].setTransportStatus(i, false);

			System.out.println();
			LandTraffic[3].TraficPrint();
		} catch (Exception e)
		{
			System.err.println(e);
		}
	}

}
