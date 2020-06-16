package test.parsing;

import interpreter.tilde.Parser;

public class TestTildeParser {
	public static void main(String[] args) {
		String[] inputs = {
			"~",
			"~+",
			"~-",
			"~default",
			"~\"default\"",
			"PATH=~/mybins:~peter/mybins:$PATH"
		};
		Parser parser = new Parser();
		for (String input : inputs)
			System.out.println(parser.parse(input));
	}
}
