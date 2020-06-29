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
		real1 = "-e BashTokens.c && 1+2 -eq 10",
		math = "1+2+3+4 -eq 10",
		math1 = "1+2 -eq 4 &! 2+3 -ne 3 || 5 -lt 4";
	
	public static void testLexer(String input) throws ParsingException {
		BooleanLexer lexer = new BooleanLexer(input);
		Token<Type> token;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			System.out.println(token);
	}
	
	public static void testParser(String input) throws ParsingException {
		BooleanLexer lexer = new BooleanLexer(input);
		BooleanParser parser = new BooleanParser(lexer);
		AST tree = parser.parse();
		System.out.println(tree);
	}
	
	public static void testInterpreter() throws InterpretingException, ParsingException {
		String[] inputs = {real, real1, math, math1};
		for (String input: inputs) {
			BooleanLexer lexer = new BooleanLexer(input);
			BooleanParser parser = new BooleanParser(lexer);
			BooleanInterpreter interpreter = new BooleanInterpreter(parser, ConsoleContext.INSTANCE);
			System.out.println(String.format("%s gives : %b", input, interpreter.interpret()));
		}
	}
	
	public static void main(String[] args) throws ParsingException, InterpretingException {
//		testLexer(input1);
//		String[] ins = {input, input0, input1, real, real1, math, math1};
//		for (String in : ins) testParser(in);
		testInterpreter();
	}
}
