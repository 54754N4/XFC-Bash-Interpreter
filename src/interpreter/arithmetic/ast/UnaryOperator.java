package interpreter.arithmetic.ast;

import interpreter.arithmetic.Type;
import interpreter.generic.Token;

public class UnaryOperator extends AST {
	public final Token<Type> token;
	public final AST expr;
	
	public UnaryOperator(Token<Type> token, AST expr) {
		this.token = token;
		this.expr = expr;
	}
}
