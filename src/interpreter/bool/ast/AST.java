package interpreter.bool.ast;

import interpreter.bool.Type;
import interpreter.exceptions.InterpretingException;
import interpreter.generic.Token;

public abstract class AST implements Visitable {
	public final Token<Type> token;
	
	public AST(Token<Type> token) {
		this.token = token;
	}
	
	@Override
	public boolean accept(Visitor visitor) throws InterpretingException {
		return visitor.visit(this);
	}
}
