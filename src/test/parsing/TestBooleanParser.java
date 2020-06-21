package test.parsing;

import interpreter.bool.BooleanLexer;
import interpreter.bool.BooleanParser;
import interpreter.bool.Type;
import interpreter.bool.ast.AST;
import interpreter.exceptions.ParsingException;
import interpreter.generic.Token;

public class TestBooleanParser {
	private static String input = "((-e filepath) -a ((strin1 == string2) == (-S socket.file)))";
	
	public static void testLexer() throws ParsingException {
		BooleanLexer lexer = new BooleanLexer(input);
		Token<Type> token;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			System.out.println(token);
	}
	
	public static void testParser() throws ParsingException {
		BooleanLexer lexer = new BooleanLexer(input);
		BooleanParser parser = new BooleanParser(lexer);
		AST tree = parser.parse();
		System.out.println(tree);
	}
	
	public static void main(String[] args) throws ParsingException {
//		testLexer();
		testParser();
	}
}
