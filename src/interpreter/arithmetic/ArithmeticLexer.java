package interpreter.arithmetic;

import interpreter.exceptions.ParsingException;
import interpreter.generic.Lexer;
import interpreter.generic.Token;

public class ArithmeticLexer extends Lexer<Type> {
	
	public ArithmeticLexer(String text) {
		super(text);
	}
	
	private double number() throws ParsingException {
		StringBuilder result = new StringBuilder();
		boolean foundDot = false;
		while ((isDigit() || isDot()) && !isFinished()) {
			if (isDot() && foundDot)
				error();
			else if (isDot())
				foundDot = true;
			result.append(current);
			advance();
		}
		return Double.parseDouble(result.toString());
	}
	
	private Token<Type> matchWord() throws ParsingException {
		for (Type word : Type.getWords()) {
			if (peek(word.toString().toLowerCase())) {
				advance(word.toString().length());
				return new Token<>(word, word.toString());
			}
		}
		return error();
	}
	
	@Override
	public Token<Type> getNextToken() throws ParsingException {
		while (!isFinished()) {
			if (isSpace()) skipWhiteSpace();
			else if (isNewline()) skipNewline();
			else if (isLetter()) return matchWord();
			else if (isDigit()) return new Token<>(Type.NUMBER, ""+number());
			else switch (current) {
				case '+': advance(); return new Token<>(Type.PLUS);
				case '-': advance(); return new Token<>(Type.MINUS);
				case '~': advance(); return new Token<>(Type.FLIP);
				case '%': advance(); return new Token<>(Type.MODULUS);
				case '|': advance(); return new Token<>(Type.OR); 
				case '&': advance(); return new Token<>(Type.AND);
				case '^': advance(); return new Token<>(Type.XOR);
				case '(': advance(); return new Token<>(Type.LEFT_PARENTHESIS);
				case ')': advance(); return new Token<>(Type.RIGHT_PARENTHESIS);
				case '*': 
					advance();
					if (is('*')) {
						advance();
						return new Token<>(Type.POWER);
					}
					return new Token<>(Type.MULTIPLY);
				case '/':
					advance();
					if (is('/')) {
						advance();
						return new Token<>(Type.FLOOR_DIVIDE);
					}
					return new Token<>(Type.DIVIDE);
				case '<':
					advance();
					if (is('<')) {
						advance();
						return new Token<>(Type.LEFT_SHIFT);
					}
					return error();
				case '>':
					advance();
					if (is('>')) {
						advance();
						return new Token<>(Type.RIGHT_SHIFT);
					}
					return error();
				default: return error();
			}
		}
		return new Token<>(Type.EOF);
	}
}
