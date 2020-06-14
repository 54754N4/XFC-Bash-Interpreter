package interpreter.brace.ast;

import java.util.ArrayList;
import java.util.List;

import interpreter.brace.Type;
import interpreter.generic.Token;

public class CharRangeExpression extends RangeExpression {

	public CharRangeExpression(Expression preamble, Token<Type> start, Token<Type> end, Token<Type> increment, Expression postscript) {
		super(preamble, start, end, increment, postscript);
	}
	
	@Override
	public List<String> generate() {
		List<String> results = new ArrayList<>();
		char first = start.value.charAt(0),
			last = end.value.charAt(0);
		int inc = Integer.parseInt(increment.value),
			sign = 1;
		if ((first > last && inc > 0) || (first < last && inc < 0)) 
			sign = -1;
		for (char i=first; (first<last) ? i<=last : i>=last; i+= sign*inc) 
			results.add(""+i);
		return results;
	}
}