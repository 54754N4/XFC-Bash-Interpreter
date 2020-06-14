package interpreter.brace;

import interpreter.exceptions.ParsingException;
import interpreter.generic.Token;


/*  expr:		word? '{' num '..' num ['..' num] '}' word?
 *  			| word? '{' char '..' char ['..' num] '}' word?
 *  			| word? '{' expr [',' expr]+ '}' word?
 *  			| word
 *  word: 		text
 *  			| char
 *  
 *  text: 		[^{}\.,]+
 *  char: 		[a-zA-Z]
 *  num: 		0*[0-9]
 */
public class Lexer {
	private String input;
	private char currentChar;
	private int pos, line;
	
	public Lexer(String input) {
		this.input = input+'\0';
		currentChar = input.charAt(pos = line = 0);
	}
	
	private void advance() {
		currentChar = input.charAt(++pos);
	}
	
	private void advance(int i) {
		while (i-->0) advance();
	}
	
	private boolean peekBehind(String target) {
		if (pos - target.length() < 0) return false;
		return input.substring(pos - target.length(), pos).startsWith(target.toLowerCase());
	}
	
	private boolean peek(String target) {
		return input.substring(pos).toLowerCase().startsWith(target.toLowerCase());
	}
	
	private boolean isEscaped() {
		return peekBehind("\\");
	}
	
	public static boolean isInteger(String word) {
		try { Integer.parseInt(word); }
		catch (Exception e) { return false; }
		return true;
	}
	
	private boolean isSpace() {
		return currentChar == ' ';
	}
	
	private boolean isNewline() {
		return currentChar == '\n';
	}
	
	private void skipWhiteSpace() {
		while (isSpace()) 
			advance();
	}
	
	private void skipNewline() {
		while (isNewline() && !finished()) {
			advance();
			line++;
		}
	}
	
	private boolean finished() {
		return currentChar == '\0';
	}
	
	// Custom lexing methods
	
	private boolean isChar() {	// if it's not a reserved token
		return currentChar != '{' 
				&& currentChar != '}'
				&& currentChar != ','
				&& !peek("..");
	}
	
	private Token<Type> word() {
		String word = "";
		while ((isChar() || (!isChar() && isEscaped())) && !finished()) {
			word += currentChar;
			advance();
		}
		word = word.replaceAll("\\\\", "");	// remove escapes after word has been extracted
		if (isInteger(word))
			return new Token<>(Type.NUMBER, word);
		return new Token<>((word.length() == 1) ? Type.CHAR : Type.TEXT, word);
	}
	
	public Token<Type> getNextToken() throws ParsingException {
		while (!finished()) {
			skipWhiteSpace();
			skipNewline();
			if (isChar()) return word();
			else if (peek("..")) {
				advance(2);
				return new Token<>(Type.RANGE);
			} else {
				char c = currentChar;
				advance();
				switch (c) {
					case '{': return new Token<>(Type.EXPR_START);
					case '}': return new Token<>(Type.EXPR_END);
					case ',': return new Token<>(Type.COMMA);
					default: cry();
				} 
			}
		} return new Token<>(Type.EOF);
	}
	
	public void cry() throws ParsingException {
		throw new ParsingException(line, pos, currentChar);
	}
	
	public void cry(String msg) throws ParsingException {
		throw new ParsingException(line, pos, currentChar, msg);
	}
	
	public static void main(String[] args) throws ParsingException {
		String[] inputs = {
				"{4}",
				"4",
				"a{abc,def,ghi}z",
				"a{20..0..2}z",
				"a{a..z..-2}z",
				"a{b{c..x..-2}y,def,ghi}z",
				"abc.we3-0xd{asd{a..z..-2},zibi{tas,toozi}}post"
		};
		String input = inputs[6];
		Lexer lexer = new Lexer(input);
		Token<Type> token;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			System.out.println(token);
	}
}
