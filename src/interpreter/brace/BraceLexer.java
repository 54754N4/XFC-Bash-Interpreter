package interpreter.brace;

import interpreter.exceptions.ParsingException;
import interpreter.generic.Lexer;
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
public class BraceLexer extends Lexer<Type> {
	
	public BraceLexer(String input) {
		super(input);
	}
	
	private boolean isChar() {	// if it's not a reserved token
		return !is('{') && !is('}') && !is(',') && !peek("..");
	}
	
	private Token<Type> word() {
		String word = "";
		while ((isChar() || (!isChar() && isEscaped())) && notFinished()) {
			word += current;
			advance();
		}
		word = word.replaceAll("\\\\", "");	// remove escapes after word has been extracted
		if (isInteger(word))
			return new Token<>(Type.NUMBER, word);
		return new Token<>((word.length() == 1) ? Type.CHAR : Type.TEXT, word);
	}
	
	public Token<Type> getNextToken() throws ParsingException {
		while (notFinished()) {
			skipWhiteSpace();
			skipNewline();
			if (isChar()) return word();
			else if (peek("..")) {
				advance(2);
				return new Token<>(Type.RANGE);
			} else {
				char c = current;
				advance();
				switch (c) {
					case '{': return new Token<>(Type.EXPR_START);
					case '}': return new Token<>(Type.EXPR_END);
					case ',': return new Token<>(Type.COMMA);
					default: error();
				} 
			}
		} return new Token<>(Type.EOF);
	}
}
