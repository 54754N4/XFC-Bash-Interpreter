package interpreter.arithmetic.ast;

import interpreter.exceptions.ParsingException;

public interface Visitable {
	double accept(Visitor visitor) throws ParsingException;
}
