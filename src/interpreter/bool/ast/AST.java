package interpreter.bool.ast;

import interpreter.bool.Type;
import interpreter.generic.Token;

public abstract class AST {
	public final Token<Type> token;
	
	public AST(Token<Type> token) {
		this.token = token;
	}
}
