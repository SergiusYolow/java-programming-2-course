

public class ResultException extends MessageException {

	public ResultException(byte result) {
		super("Invalid " + Res.class.getName() + ": " + result);
	}

	public ResultException(String str) {
		super(str);
	}
}
