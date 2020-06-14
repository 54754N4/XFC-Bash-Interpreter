package test;

import java.io.FileNotFoundException;

import interpreter.brace.Lexer;
import interpreter.brace.Parser;
import interpreter.exceptions.ParsingException;

public class TestBraceParser {
	public static void main(String[] args) throws ParsingException, FileNotFoundException {
		String[] inputs = {
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
//				"{A..Z}{0..9}{z..a}{9..0}",
				"{3..-2}",
				"{a,b{1..3},c}",
				"{,{,gotta have{ ,\\, again\\, }}more }cowbell!",
				"It{{em,alic}iz,erat}e{d,}",	
				"~/{Downloads,Pictures}/*.{jpg,gif,png}"
		};
		for (String input : inputs) {
			Parser parser = new Parser(new Lexer(input));
			System.out.println(String.format("\"%s\" expands to%n%s%n", input, parser.parseResultsString()));
		}
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
}
