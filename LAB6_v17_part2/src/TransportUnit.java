import java.io.Serializable;



public class TransportUnit extends TypeClass implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Type transportType;
	int transportNumber;
	boolean status;
	
	
	TransportUnit(int num,Type type,boolean status)
	{
		transportNumber=num;
		transportType=type;
		this.status=status;
	}
	 
	public TransportUnit() {}
	
	

}
