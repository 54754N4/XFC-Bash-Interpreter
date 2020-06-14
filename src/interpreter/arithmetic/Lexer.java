package interpreter.arithmetic;

import interpreter.exceptions.ParsingException;
import interpreter.generic.Token;

public class Lexer {
	private String text;
	private int pos, line;
	private char currentChar;
	private boolean finished;
	
	public Lexer(String text) {
		this.text = text;
		pos = line = 0;
		currentChar = text.charAt(0);
		finished = false;
	}
	
	private void advance() {
		try { currentChar = text.charAt(++pos); }
		catch (IndexOutOfBoundsException e) { finished = true; }
	}
	
	private void advance(int i) {
		while (i-->0) advance();
	}
	
	private boolean peek(String target) {
		return text.substring(pos).toLowerCase().startsWith(target);
	}
	
	private boolean isSpace() {
		return currentChar == ' ';
	}
	
	private boolean isNewline() {
		return currentChar == '\n';
	}
	
	private boolean isDot() {
		return currentChar == '.';
	}
	
	private boolean isDigit() {
		return Character.isDigit(currentChar);
	}
	
	private boolean isLetter() {
		return Character.isLetter(currentChar);
	}
	
	private void skipWhiteSpace() {
		while (isSpace()) 
			advance();
	}
	
	private void skipNewline() {
		while (isNewline() && !finished) {
			advance();
			line++;
		}
	}
	
	private double number() throws ParsingException {
		StringBuilder result = new StringBuilder();
		boolean foundDot = false;
		while ((isDigit() || isDot()) && !finished) {
			if (isDot() && foundDot)
				error();
			else if (isDot())
				foundDot = true;
			result.append(currentChar);
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
	
	public Token<Type> error() throws ParsingException {
		throw new ParsingException(line, pos, currentChar);
	}
	
	public Token<Type> getNextToken() throws ParsingException {
		while (!finished) {
			if (isSpace()) skipWhiteSpace();
			else if (isNewline()) skipNewline();
			else if (isLetter()) return matchWord();
			else if (isDigit()) return new Token<>(Type.NUMBER, ""+number());
			else switch (currentChar) {
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
					if (currentChar == '*') {
						advance();
						return new Token<>(Type.POWER);
					}
					return new Token<>(Type.MULTIPLY);
				case '/':
					advance();
					if (currentChar == '/') {
						advance();
						return new Token<>(Type.FLOOR_DIVIDE);
					}
					return new Token<>(Type.DIVIDE);
				case '<':
					advance();
					if (currentChar == '<') {
						advance();
						return new Token<>(Type.LEFT_SHIFT);
					}
					return error();
				case '>':
					advance();
					if (currentChar == '>') {
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
