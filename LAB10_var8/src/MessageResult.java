

import java.io.Serializable;

/**
 * <p>MessageResult class: Server returns data base class
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageResult extends Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int errorCode;
	public String errorMessage;

	public int getErrorCode() {
		return errorCode;
	}

	public boolean Error() {
		return errorCode != Protocol.RESULT_CODE_OK;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	
	protected MessageResult() {
		super();
	}
	
	protected MessageResult( byte id, String errorMessage ) {
		
		super( id );
		this.errorCode = Protocol.RESULT_CODE_ERROR;
		this.errorMessage = errorMessage;
	}

	protected MessageResult( byte id ) {
		
		super( id );
		this.errorCode = Protocol.RESULT_CODE_OK;
		this.errorMessage = "";
	}
}