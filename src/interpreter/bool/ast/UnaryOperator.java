package interpreter.bool.ast;

import interpreter.bool.Type;
import interpreter.generic.Token;

public class UnaryOperator extends AST {
	public final AST node;
	
	public UnaryOperator(Token<Type> token, AST node) {
		super(token);
		this.node = node;
	}
	
	@Override
	public String toString() {
		return String.format("(%s %s)", token.value, node.toString());
	}
}
