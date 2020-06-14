package interpreter.arithmetic.ast;

import interpreter.exceptions.ParsingException;

public interface Visitor {
	Double visit(Value node) throws ParsingException;
	Double visit(UnaryOperator node) throws ParsingException;
	Double visit(BinaryOperator node) throws ParsingException;
	Double visit(Composition node) throws ParsingException;
	default Double visit(AST ast) throws ParsingException {
		if (Value.class.isInstance(ast))
			return visit((Value) ast);
		else if (UnaryOperator.class.isInstance(ast))
			return visit((UnaryOperator) ast);
		else if (BinaryOperator.class.isInstance(ast))
			return visit((BinaryOperator) ast);
		else if (Composition.class.isInstance(ast))
			return visit((Composition) ast);
		throw new IllegalArgumentException();
	}
}
