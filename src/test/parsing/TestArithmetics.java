package test.parsing;

import interpreter.arithmetic.Interpreter;
import interpreter.exceptions.ParsingException;

public class TestArithmetics {
	public static void main(String[] args) throws ParsingException {
		interpreter.arithmetic.Interpreter interpreter = new Interpreter("2+PI\n");
		System.out.println(interpreter.interpret());
	}
}
