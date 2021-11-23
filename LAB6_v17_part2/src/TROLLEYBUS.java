import java.io.Serializable;

public class TROLLEYBUS extends TransportUnit implements Serializable  {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TROLLEYBUS( int num ) {
		super(num,TypeClass.Type.TROLLEYBUS,true);
	}
	public TROLLEYBUS() {}
}
