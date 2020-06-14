package interpreter.arithmetic.ast;

import interpreter.arithmetic.Type;
import interpreter.generic.Token;

public class Value extends AST {
	public final Token<Type> token;
	
	public Value(Token<Type> token) {
		this.token = token;
	}
}
