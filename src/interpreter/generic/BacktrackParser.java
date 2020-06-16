package interpreter.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import interpreter.exceptions.ParsingException;

public abstract class BacktrackParser<Type extends Enum<Type>, Tree> implements Parser<Tree> {
	private Lexer<Type> lexer;
	private List<Token<Type>> tokens;
	private Stack<Integer> stack;	// to backtrack at different levels
	private int current;			// index in tokens list
	
	public BacktrackParser(Lexer<Type> lexer) throws ParsingException {
		this.lexer = lexer;
		current = 0;
		stack = new Stack<>();
		tokens = new ArrayList<>();
		Token<Type> token;
		Type eof = finalToken();
		while ((token = lexer.getNextToken()).type != eof)
			tokens.add(token);
		tokens.add(new Token<>(eof));
	}
	
	protected abstract Type finalToken();
	
	protected Token<Type> current() {
		return tokens.get(current);
	}
	
	protected void save() {
		stack.push(current);
	}
	
	protected void backtrack() {
		current = stack.pop();
	}
	
	protected boolean is(Type type) {
		return current().type == type;
	}
	
	@SuppressWarnings("unchecked")		// the types will always be correct ?
	protected boolean is(Type...types) {
		if (current >= tokens.size())
			return false;
		for (Type type : types)
			if (is(type))
				return true;
		return false;
	}
	
	protected void consume(Type type) throws ParsingException {
		if (is(type)) 
			current++;
		else 
			lexer.error("Expected type :"+type.name()+" "+type);
	}
}
