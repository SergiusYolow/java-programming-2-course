import java.io.Serializable;


public class Route extends TypeClass implements Serializable
{
	public static class ArgException extends Exception
	{
		private static final long serialVersionUID = 1L;

		ArgException(String arg)
		{
			super("Error: " + arg);
		}
	}
	


	/**
		 * 
		 */
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
		System.out.print("Route:   ");
		System.out.printf("%-40s", routeName);
		System.out.print("Transport type: ");
		System.out.printf("%-15s", transport[0].transportType.toString());
		System.out.print("Amount of cars: ");
		System.out.printf("%-8d", getAmount());
		System.out.print("Interval: ");
		System.out.printf("%-6.2f", getInter());
		System.out.println();
	}

	public String toString()
	{
		return new String("Route: " + routeName + "	type: " + transport[0].transportType.toString() + "	Cars amount: "
				+ getAmount() + "	interval: " + getInter());
	}

	public Route()
	{
	}
	
	public double getInter()
	{
		double k=0;
		for (int i=0;i<amount;i++)
			if (transport[i].status==true)
				k++;
		double res = inter*amount;
		res/=k;
		return res;
	}
	
	public int getAmount()
	{
		int k=0;
		for (int i=0;i<amount;i++)
			if (transport[i].status==true)
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
		
		int k=0;
		for (int i=0;i<amount;i++)
			if (transport[i].status==true)
				k++;
		
		if (k==0 && st == false)
			throw new ArgException("Error: all cars are broken");
		
		this.transport[n].status=st;
	}
}
