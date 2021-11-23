



public class InvalidSchemaException extends ValidatorException {

	public InvalidSchemaException(String msg) {
		super(msg);
	}

	public InvalidSchemaException(Class<? extends Xml> msg, String request) {
		super("Schema validation failed" + "\nClass: " + msg.getSimpleName() + "\nRequest: " + request);
	}


}
