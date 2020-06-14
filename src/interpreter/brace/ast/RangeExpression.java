package interpreter.brace.ast;

import java.util.ArrayList;
import java.util.List;

import interpreter.brace.Type;
import interpreter.generic.Token;

public class RangeExpression extends Expression {
	public final Token<Type> start, end, increment;
	public final Expression preamble, postscript;
	
	public RangeExpression(Expression preamble, Token<Type> start, Token<Type> end, Token<Type> increment, Expression postscript) {
		this.preamble = preamble;
		this.start = start;
		this.end = end;
		this.increment = increment;
		this.postscript = postscript;
	}
	
	private String createPad(int size) {
		if (size <= 0) return "";
		StringBuilder sb = new StringBuilder();
		while (size-->0) sb.append('0');
		return sb.toString();
	}
	
	public List<String> generate() {
		int size = start.value.length();
		List<String> results = new ArrayList<>();
		int first = Integer.parseInt(start.value),
			last = Integer.parseInt(end.value),
			inc = Integer.parseInt(increment.value),
			sign = 1;
		if ((first > last && inc > 0) || (first < last && inc < 0)) 
			sign = -1;
		for (int i=first; (first<last) ? i<=last : i>=last; i+= sign*inc) 
			results.add(createPad(size-(""+i).length())+i);
		return results;
	}

	@Override
	public List<String> evaluate() {
		List<String> results = new ArrayList<>();
		for (String token : generate())
			for (String pre : preamble.evaluate())
				for (String post : postscript.evaluate())
					results.add(pre+token+post);
		return results;
	}
}