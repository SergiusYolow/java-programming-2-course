

/**
 * <p>CMD interface: Client message IDs 
 * @author Sergey Gutnikov
 * @version 1.0
 */
interface CMD {
	static final byte CMD_CONNECT 			= 1;
	static final byte CMD_ALL_NEWS			= 2;
	static final byte CMD_LAST_NEWS			= 3;
	static final byte CMD_SELECTED_NEWS		= 4;
	static final byte CMD_DISCONNECT		= 5;
}

/**
 * <p>RESULT interface: Result codes 
 * @author Sergey Gutnikov
 * @version 1.0
 */
interface RESULT {
	static final int RESULT_CODE_OK 		= 0;
	static final int RESULT_CODE_ERROR 		= -1;
}	

/**
 * <p>PORT interface: Port # 
 * @author Sergey Gutnikov
 * @version 1.0
 */
interface PORT {
	static final int PORT 					= 8071;
}

/**
 * <p>Protocol class: Protocol constants 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class Protocol implements CMD, RESULT, PORT {
	private static final byte CMD_MIN = CMD_CONNECT;
	private static final byte CMD_MAX = CMD_DISCONNECT;
	public static boolean validID( byte id ) {
		return id >= CMD_MIN && id <= CMD_MAX; 
	}
}

