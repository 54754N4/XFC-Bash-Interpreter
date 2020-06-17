package interpreter.parameter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import interpreter.generic.Parser;
import model.ConsoleContext;

// Includes lexing code since it's simple
public class ParameterParser implements Parser<String> {
	private ParameterFilters paramFilters;
	private static final Point NOT_FOUND = new Point(-1, -1);
	private String input;
	private char current, previous;
	private int pos;
	
	public ParameterParser(String input, ConsoleContext context) {
		paramFilters = new ParameterFilters(context);
		this.input = input;
		current = input.charAt(pos = 0);
		previous = '\0';
	}
	
	private char next() {
		if (pos + 1 < input.length()) {
			previous = current;
			return current = input.charAt(++pos);
		} else 
			return current = '\0';
	}
	
	private Point findNext() {
		while ((current != '$' || previous == '\\') && current != '\0')	// !(c == $ && p != \\)
			next();
		if (current == '\0') 
			return NOT_FOUND;
		int start = pos;
		char match = (next() == '{') ? '}' : ' ';
		while (next() != match && current != '\0');
		int end = (current == '}') ? pos+1 : pos;
		return new Point(start, end);
	}
	
	@Override
	public String parse() {
		String result = input;
		List<Match> matches = new ArrayList<>();
		Point matched;
		while (!(matched = findNext()).equals(NOT_FOUND))
			matches.add(new Match(matched.x, matched.y, input, paramFilters));
		for (Match match : matches) 
			result = result.replace(match.name, match.replacement);
		return result;
	}
	
	private static final class Match {
		public final String name;
		public String replacement;
		
		public Match(int start, int end, String input, ParameterFilters paramFilters) {
			name = input.substring(start, end);
			int innerStart = (name.startsWith("${")) ? 2: 1,
				innerEnd = (name.endsWith("}")) ? name.length()-1: name.length();
			String innerName = name.substring(innerStart, innerEnd);
			for (int i=0; i<paramFilters.getFilters().size(); i++) {
				if (paramFilters.getFilters().get(i).test(innerName)) {
					replacement = paramFilters.getHandlers().get(i).apply(innerName);
					break;
				}
			}
		}
	}
}
