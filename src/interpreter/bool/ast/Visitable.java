package interpreter.bool.ast;

import interpreter.exceptions.InterpretingException;

public interface Visitable {
	boolean accept(Visitor visitor) throws InterpretingException;
}
