package test.parsing;

import interpreter.bool.BooleanInterpreter;
import interpreter.bool.BooleanLexer;
import interpreter.bool.BooleanParser;
import interpreter.bool.Type;
import interpreter.bool.ast.AST;
import interpreter.exceptions.InterpretingException;
import interpreter.exceptions.ParsingException;
import interpreter.generic.Token;
import model.ConsoleContext;

public class TestBooleanParser {
	public static String input = "-e filepath -eq -o (strin1 == string2) < -S socket.file",  
		input0 = "-e filepath -eq (strin1 == \"some result\") > -S socket.file",
		input1 = "-e filepath -eq (\"$file\" == $result) =~ -S socket.file",
		real = "BashTokens.c =~ Bash.* && -e BashTokens.c",
		math = "1+2+3+4 -gt 0";
	
	public static void testLexer() throws ParsingException {
		BooleanLexer lexer = new BooleanLexer(input1);
		Token<Type> token;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			System.out.println(token);
	}
	
	public static void testParser() throws ParsingException {
		BooleanLexer lexer = new BooleanLexer(input1);
		BooleanParser parser = new BooleanParser(lexer);
		AST tree = parser.parse();
		System.out.println(tree);
	}
	
	public static void testInterpreter() throws InterpretingException, ParsingException {
		BooleanLexer lexer = new BooleanLexer(math);
		BooleanParser parser = new BooleanParser(lexer);
		BooleanInterpreter interpreter = new BooleanInterpreter(parser, ConsoleContext.INSTANCE);
		System.out.println(interpreter.interpret());
	}
	
	public static void main(String[] args) throws ParsingException, InterpretingException {
//		testLexer();
//		testParser();
		testInterpreter();
	}
}
