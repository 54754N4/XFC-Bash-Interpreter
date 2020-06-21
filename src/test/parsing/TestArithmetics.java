package test.parsing;

import interpreter.arithmetic.Interpreter;
import interpreter.exceptions.ParsingException;

public class TestArithmetics {
	public static void main(String[] args) throws ParsingException {
		interpreter.arithmetic.Interpreter interpreter = new Interpreter("2+3*4+PI+exp(ln(1))\n+2**(1+2)");
		System.out.println(interpreter.interpret());
	}
}
