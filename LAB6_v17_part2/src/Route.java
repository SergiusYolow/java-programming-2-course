import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class Route extends TypeClass implements Serializable
{
	public String getType(TransportUnit t)
	{
		switch (t.transportType)
		{
		case BUS:
			return AppLocale.getString(AppLocale.bus);

		case TROLLEYBUS:
			return AppLocale.getString(AppLocale.trolleybus);

		case TRAM:
			return AppLocale.getString(AppLocale.tram);
		}
		return null;
	}

	public static class ArgException extends Exception
	{
		private static final long serialVersionUID = 1L;

		ArgException(String arg)
		{
			super("Error: " + arg);
		}
	}

	public final Date creationDate = new Date();

	public String getCreationDate()
	{
		DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT,
				AppLocale.get());
		String dateOut = dateFormatter.format(creationDate);
		return dateOut;
	}

	
	private static final long serialVersionUID = 1L;

	private int amount;

	private String routeName;

	private double inter;

	public TransportUnit[] transport = null;

	public String getRouteName()
	{
		return routeName;
	}

	public int getTransportAmount()
	{
		return transport.length;
	}

	public void TraficPrint()
	{
		System.out.print(AppLocale.getString(AppLocale.route) + ": ");
		System.out.printf("%-45s", routeName);
		System.out.print(AppLocale.getString(AppLocale.type) + ": ");
		System.out.printf("%-15s", getType(transport[0]));
		System.out.print(AppLocale.getString(AppLocale.amount) + ": ");
		System.out.printf("%-8d", getAmount());
		System.out.print(AppLocale.getString(AppLocale.inter) + ": ");
		System.out.printf("%-9.2f", getInter());
		System.out.print(AppLocale.getString(AppLocale.creation) + ": ");
		System.out.printf("%-15s", getCreationDate());
		System.out.println();
		
	}

	public String toString()
	{
		return new String(
				AppLocale.getString(AppLocale.route) + ":   " + routeName + AppLocale.getString(AppLocale.type) + ": "
						+ getType(transport[0]) + AppLocale.getString(AppLocale.amount) + ": "
						+ getAmount() + AppLocale.getString(AppLocale.inter) + ": " + getInter()+
						AppLocale.getString(AppLocale.creation) + ": "+getCreationDate());
	}

	public Route()
	{
	}

	public double getInter()
	{
		double k = 0;
		for (int i = 0; i < amount; i++)
			if (transport[i].status == true)
				k++;
		double res = inter * amount;
		res /= k;
		return res;
	}

	public int getAmount()
	{
		int k = 0;
		for (int i = 0; i < amount; i++)
			if (transport[i].status == true)
				k++;

		return k;
	}

	public Route(String name, Type type, int amount, double inter)
	{
		routeName = name;
		transport = new TransportUnit[amount];
		switch (type)
		{
		case BUS:
			for (int i = 0; i < amount; i++)
				transport[i] = new BUS(i + 1);
			break;
		case TRAM:
			for (int i = 0; i < amount; i++)
				transport[i] = new TRAM(i + 1);
			break;
		case TROLLEYBUS:
			for (int i = 0; i < amount; i++)
				transport[i] = new TROLLEYBUS(i + 1);
			break;
		}
		this.inter = inter;
		this.amount = amount;
	}

	public void setTransportStatus(int n, boolean st) throws ArgException
	{
		if (n >= transport.length)
		{
			throw new ArgException("Invalid value of transport number: " + n);
		}

		int k = 0;
		for (int i = 0; i < amount; i++)
			if (transport[i].status == true)
				k++;

		if (k == 0 && st == false)
			throw new ArgException("Error: all cars are broken");

		this.transport[n].status = st;
	}
}
