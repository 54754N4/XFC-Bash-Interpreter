package test.parsing;

import interpreter.brace.BraceLexer;
import interpreter.brace.BraceParser;
import interpreter.brace.Type;
import interpreter.exceptions.ParsingException;
import interpreter.generic.Token;

public class TestBraceParser {
	static String[] inputs = {
		"{4,2}",
		"4",
		"i am just text",
		"a{abc,def,ghi}z",
		"a{20..0..2}z",
		"a{a..z..-2}z",
		"a{b{c..x..-2}y,def,ghi}z",
		"abc.we3-0xd{asd{a..z..-2},zibi{tas,toozi}}post",
		"{a,b}$PATH",
		"{1..10}",
		"{0001..10}",
		"1.{0..9}",
		"{{A..Z},{a..z}}",
		"{A..Z}{0..9}",
		"{A..Z}{0..9}{z..a}",
//		"{A..Z}{0..9}{z..a}{9..0}",
		"{3..-2}",
		"{a,b{1..3},c}",
		"{,{,gotta have{ ,\\, again\\, }}more }cowbell!",
		"It{{em,alic}iz,erat}e{d,}",	
		"~/{Downloads,Pictures}/*.{jpg,gif,png}"
	};
	
	public static void testLexer() throws ParsingException {
		String text = "a{b{c..x..-2}y,def,ghi}z";
		BraceLexer lexer = new BraceLexer(text);
		Token<Type> token;
		int i=0;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			System.out.println(i+++": "+token);
	}
	
	public static void testParser() throws ParsingException {
		for (String input : inputs) {
			BraceParser parser = new BraceParser(new BraceLexer(input));
			System.out.println(String.format("\"%s\" expands to%n%s%n", input, parser.parseResultsString()));
		}
	}
	
	public static void main(String[] args) throws ParsingException {
//		testLexer();
		testParser();
	}

//	public static List<String> expand(String s) {
//		List<String> results = new ArrayList<>();
//		expandR(results, "", s, "");
//		return results;
//	}
//
//	private static void expandR(List<String> results, String pre, String s, String suf) {
//		int i1 = -1, i2 = 0;
//		String noEscape = s.replaceAll("([\\\\]{2}|[\\\\][,}{])", "  ");
//		StringBuilder sb = null;
//
//		outer:
//			while ((i1 = noEscape.indexOf('{', i1 + 1)) != -1) {
//				i2 = i1 + 1;
//				sb = new StringBuilder(s);
//				for (int depth = 1; i2 < s.length() && depth > 0; i2++) {
//					char c = noEscape.charAt(i2);
//					depth = (c == '{') ? ++depth : depth;
//					depth = (c == '}') ? --depth : depth;
//					if (c == ',' && depth == 1) {
//						sb.setCharAt(i2, '\u0000');
//					} else if (c == '}' && depth == 0 && sb.indexOf("\u0000") != -1)
//						break outer;
//				}
//			}
//		if (i1 == -1) {
//			if (suf.length() > 0)
//				expandR(results, pre + s, suf, "");
//			else
//				results.add(String.format("%s%s%s", pre, s, suf));
//		} else {
//			for (String m : sb.substring(i1 + 1, i2).split("\u0000", -1))
//				expandR(results, pre + s.substring(0, i1), m, s.substring(i2 + 1) + suf);
//		}
//	}
	
	
	// LEXING TEST
	
	/*
	 * public static void main(String[] args) throws ParsingException {
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
		BraceLexer lexer = new BraceLexer(input);
		Token<Type> token;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			System.out.println(token);
	}
	 */
}
