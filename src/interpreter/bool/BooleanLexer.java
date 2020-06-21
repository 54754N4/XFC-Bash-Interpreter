package interpreter.bool;

import interpreter.exceptions.ParsingException;
import interpreter.generic.Lexer;
import interpreter.generic.Token;

public class BooleanLexer extends Lexer<Type> {

	public BooleanLexer(String text) {
		super(text);
	}

	private Token<Type> word() {
		StringBuilder word = new StringBuilder();
		boolean isQuoted = is('"'); 
		if (isQuoted) advance();	// we still have to consume quotes
		while ((!isSpace() || (isQuoted && !is('"'))) 
				&& !is(')') && notFinished()) {
			word.append(current);
			advance();
		}
		if (isQuoted) advance();
		String value = word.toString();
		if (isInteger(value))
			return new Token<>(Type.NUMBER, value);
		if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false"))
			return new Token<>(Type.CONSTANT, value);
		return new Token<>(Type.STRING, value);
	}
	
	private Token<Type> operator() throws ParsingException {
		StringBuilder op = new StringBuilder();
		op.append(current);	// add -
		advance();
		while (isLetter() && !isSpace() && notFinished()) {
			op.append(current);
			advance();
		}
		Type type = Type.from(op.toString());
		if (type == null)
			error("Non-existent operator");
		return new Token<>(type);
	}
	
	@Override
	public Token<Type> getNextToken() throws ParsingException {
		while (notFinished()) {
			if (isSpace()) skipWhiteSpace();
			else if (isNewline()) skipNewline();
			else if (is('-')) return operator();
			else if (is('"') || is('$') || isLetter() || isDigit()) return word();
			else {
				char c = current;
				advance();
				switch (c) {
					case '<': return new Token<>(Type.STR_LESS_THAN);
					case '>': return new Token<>(Type.STR_GREATER_THAN);
					case '(': return new Token<>(Type.LEFT_PAREN);
					case ')': return new Token<>(Type.RIGHT_PAREN);
					case '=': 
						if (is('=')) {
							advance();
							return new Token<>(Type.STR_EQUAL);
						} else if (is('~')) {
							advance();
							return new Token<>(Type.STR_MATCH);
						} return new Token<>(Type.STR_EQUAL_POSIX);
					case '!':
						if (is('=')) {
							advance();
							return new Token<>(Type.STR_NOT_EQUAL);
						} return new Token<>(Type.NOT);
					default: error("Unrecognised char");
				}
			}
		} return new Token<>(Type.EOF);
	}
}
