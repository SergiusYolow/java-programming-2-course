import java.io.Serializable;

public class BUS extends TransportUnit  implements Serializable{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BUS( int num ) {
		super(num,TypeClass.Type.BUS,true);
	}
	
	public BUS() {}
}
