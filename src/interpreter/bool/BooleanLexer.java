package interpreter.bool;

import interpreter.exceptions.ParsingException;
import interpreter.generic.Lexer;
import interpreter.generic.Token;

public class BooleanLexer extends Lexer<Type> {

	public BooleanLexer(String text) {
		super(text);
	}
	
	private Token<Type> quotedWord() {
		StringBuilder word = new StringBuilder();
		advance();
		while (isNot('"') && notFinished()) {
			word.append(current);
			advance();
		}
		advance();
		return tokenize(word);
	}

	private Token<Type> word() {
		StringBuilder word = new StringBuilder();
		while (!isSpace() && isNot(')') && notFinished()) {
			word.append(current);
			advance();
		}
		return tokenize(word);
	}
	
	private Token<Type> tokenize(StringBuilder word) {
		String value = word.toString();
		if (isInteger(value))
			return new Token<>(Type.NUMBER, value);
		if (value.toLowerCase().equals("true") 
				|| value.toLowerCase().equals("false"))
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
			else if (is('"')) return quotedWord();
			else if (is('$') || isLetter() || isDigit()) return word();
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
					case '&':
						if (is('&')) {
							advance();
							return new Token<>(Type.AND);
						} else if (is('!')) {
							advance();
							return new Token<>(Type.AND_NOT);
						}
						return error("Unrecognised char");
					case '|':
						if (is('|')) {
							advance();
							return new Token<>(Type.OR);
						}
					default: return error("Unrecognised char");
				}
			}
		} return new Token<>(Type.EOF);
	}
}
