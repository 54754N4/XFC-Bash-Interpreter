package interpreter.generic;

import interpreter.exceptions.ParsingException;

public interface Parser<T> {
	public T parse() throws ParsingException;
}
