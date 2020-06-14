package interpreter.arithmetic.ast;

import interpreter.exceptions.ParsingException;

public class AST implements Visitable {

	@Override
	public double accept(Visitor visitor) throws ParsingException {
		return visitor.visit(this);
	}
	
}
