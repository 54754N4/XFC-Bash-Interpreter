package interpreter.generic;

import interpreter.exceptions.ParsingException;

public abstract class Lexer<Type extends Enum<Type>> {
	private String text;
	private int pos, line;
	protected char current;
	
	public Lexer(String text) {
		this.text = text+"\0";
		pos = line = 0;
		current = text.charAt(0);
	}
	
	public abstract Token<Type> getNextToken() throws ParsingException;
	
	protected void advance() {
		if (notFinished()) current = text.charAt(++pos);
	}
	
	protected void advance(int i) {
		while (i-->0) advance();
	}
	
	protected boolean is(char c) {
		return current == c;
	}
	
	protected boolean isFinished() {
		return is('\0');
	}
	
	protected boolean notFinished() {
		return !isFinished();
	}
	
	protected boolean isSpace() {
		return is(' ');
	}
	
	protected boolean isNewline() {
		return is('\n');
	}
	
	protected boolean isEscaped() {
		return peekBehind("\\");
	}
	
	protected boolean isDot() {
		return is('.');
	}
	
	protected boolean isDigit() {
		return Character.isDigit(current);
	}
	
	protected boolean isLetter() {
		return Character.isLetter(current);
	}
	
	protected boolean peek(String target) {
		return text.substring(pos)
				.toLowerCase()
				.startsWith(target);
	}
	
	protected boolean peekBehind(String target) {
		if (pos - target.length() < 0) 
			return false;
		return text.substring(pos - target.length(), pos)
				.startsWith(target.toLowerCase());
	}
	
	protected void skipWhiteSpace() {
		while (isSpace()) 
			advance();
	}
	
	protected void skipNewline() {
		while (isNewline()) { // no need for !isFinished
			advance();
			line++;
		}
	}
	
	public static boolean isInteger(String word) {
		try { Integer.parseInt(word); }
		catch (Exception e) { return false; }
		return true;
	}
	
	public Token<Type> error() throws ParsingException {
		throw new ParsingException(line, pos, current);
	}
	
	public Token<Type> error(String msg) throws ParsingException {
		throw new ParsingException(line, pos, current, msg);
	}
}
