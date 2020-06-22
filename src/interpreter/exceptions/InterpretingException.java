package interpreter.exceptions;

import java.io.IOException;

public class InterpretingException extends IOException {
	private static final long serialVersionUID = 6880635872675668536L;

	public InterpretingException(String msg) {
		super(msg);
	}

	public InterpretingException() {
		super("Error occurred during interpretation");
	}
}
