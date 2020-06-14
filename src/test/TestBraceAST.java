package test;

import java.util.Arrays;

import interpreter.brace.Type;
import interpreter.brace.ast.CSVExpression;
import interpreter.brace.ast.CharRangeExpression;
import interpreter.brace.ast.RangeExpression;
import interpreter.brace.ast.Text;
import interpreter.generic.Token;

public class TestBraceAST {
	
	public static void main(String[] args) {
		// {4} = 4
		System.out.println(Arrays.toString(new Text(new Token<>(Type.NUMBER, "4")).evaluate().toArray()));
		// a{abc,def,ghi}z = aabcz adefz aghiz
		System.out.println(Arrays.toString(
				new CSVExpression(
						new Text(new Token<>(Type.CHAR, "a")), 
						Arrays.asList(
								new Text(new Token<>(Type.TEXT, "abc")),
								new Text(new Token<>(Type.TEXT, "def")),
								new Text(new Token<>(Type.TEXT, "ghi"))
						),
						new Text(new Token<>(Type.CHAR, "z"))
					).evaluate().toArray()));
		// a{20..0..2}z = a20z a18z a16z a14z a12z a10z a8z a6z a4z a2z a0z
		System.out.println(Arrays.toString(
				new RangeExpression(
						new Text(new Token<>(Type.CHAR, "a")), 
						new Token<>(Type.NUMBER, "20"),
						new Token<>(Type.NUMBER, "0"),
						new Token<>(Type.NUMBER, "2"),
						new Text(new Token<>(Type.CHAR, "z"))
					).evaluate().toArray()));
		// a{a..z..-2}z = aaz acz aez agz aiz akz amz aoz aqz asz auz awz ayz 
		System.out.println(Arrays.toString(
				new CharRangeExpression(
						new Text(new Token<>(Type.CHAR, "a")), 
						new Token<>(Type.CHAR, "a"),
						new Token<>(Type.CHAR, "z"),
						new Token<>(Type.NUMBER, "-2"),
						new Text(new Token<>(Type.CHAR, "z"))
					).evaluate().toArray()));
		// a{b{c..x..-2}y,def,ghi}z = abcyz abeyz abgyz abiyz abkyz abmyz aboyz abqyz absyz abuyz abwyz adefz aghiz
		System.out.println(Arrays.toString(
				new CSVExpression(
						new Text(new Token<>(Type.CHAR, "a")), 
						Arrays.asList(
								new CharRangeExpression(
										new Text(new Token<>(Type.CHAR, "b")), 
										new Token<>(Type.CHAR, "c"),
										new Token<>(Type.CHAR, "x"),
										new Token<>(Type.NUMBER, "-2"),
										new Text(new Token<>(Type.CHAR, "y"))
									),
								new Text(new Token<>(Type.TEXT, "def")),
								new Text(new Token<>(Type.TEXT, "ghi"))
						),
						new Text(new Token<>(Type.CHAR, "z"))
					).evaluate().toArray()));
	}
}
