package interpreter.bool.ast;

import interpreter.exceptions.InterpretingException;

public interface Visitor {
	boolean visit(UnaryOperator op) throws InterpretingException;
	boolean visit(BinaryOperator op) throws InterpretingException;
	default Boolean visit(AST ast) throws InterpretingException {	// dispatcher
		if (UnaryOperator.class.isInstance(ast))
			return visit(UnaryOperator.class.cast(ast));
		else if (BinaryOperator.class.isInstance(ast))
			return visit(BinaryOperator.class.cast(ast)); 
		throw new InterpretingException();
	}
}
