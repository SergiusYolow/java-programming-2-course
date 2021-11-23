

public class CommandException extends MessageException {

	public CommandException(byte command) {
		super("Invalid " + CMD.class.getName() + ": " + command);
	}
}
