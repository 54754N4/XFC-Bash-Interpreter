package test.parsing;

import interpreter.tilde.TildeParser;

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
		for (String input : inputs)
			System.out.println(new TildeParser(input).parse());
	}
}
