package interpreter.generic;

import interpreter.exceptions.ParsingException;

public abstract class IterativeParser<Type extends Enum<Type>, Tree> implements Parser<Tree> {
	protected Lexer<Type> lexer;
	protected Token<Type> current;
	
	public IterativeParser(Lexer<Type> lexer) throws ParsingException {
		this.lexer = lexer;
		current = lexer.getNextToken();
	}
	
	protected boolean matches(Type[] types) {
		for (Type type : types)
			if (current.type == type)
				return true;
		return false;
	}
	
	protected void consume(Type type) throws ParsingException {
		if (current.type == type) 
			current = lexer.getNextToken();
		else lexer.error("Expected token of type: "+type.name());
	}
	
	public Tree error() throws ParsingException {
		lexer.error();	// throws error with line and pos
		return null;
	}
}
