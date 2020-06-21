package interpreter.bool.ast;

import interpreter.bool.Type;
import interpreter.generic.Token;

public class BinaryOperator extends AST {
	public final AST left, right;
	
	public BinaryOperator(AST left, Token<Type> token, AST right) {
		super(token);
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return String.format("(%s %s %s)", left.toString(), token.value, right.toString());
	}
}
