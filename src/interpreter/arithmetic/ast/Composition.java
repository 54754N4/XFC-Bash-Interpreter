package interpreter.arithmetic.ast;

import interpreter.arithmetic.Type;
import interpreter.generic.Token;

public class Composition extends AST {
	public final Token<Type> token;
	public final AST expr;
	
	public Composition(Token<Type> token, AST expr) {
		this.token = token;
		this.expr = expr;
	}
}
