import java.io.Serializable;

public class TRAM extends TransportUnit implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TRAM( int num ) {
		super(num,TypeClass.Type.TRAM,true);
	}
	
	public TRAM() {}
}
