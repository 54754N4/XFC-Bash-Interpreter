package interpreter.bool.ast;

import interpreter.bool.Type;
import interpreter.generic.Token;

public class Constant extends AST {

	public Constant(Token<Type> token) {
		super(token);
	}

	@Override
	public String toString() {
		return String.format("%s", token.value);
	}
}
