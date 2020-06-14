package interpreter.parameter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Parser {
	private static final Point NOT_FOUND = new Point(-1, -1);
	private String input;
	private char current, previous;
	private int pos;
	
	private void reset(String input) {
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
	
	public String parse(String input) {
		reset(input);
		String result = input;
		List<Match> matches = new ArrayList<>();
		Point matched;
		while (!(matched = findNext()).equals(NOT_FOUND))
			matches.add(new Match(matched.x, matched.y));
		for (Match match : matches) {
//			System.out.printf("%s -> %s%n", match.name, match.replacement);
			result = result.replace(match.name, match.replacement);
		}
		return result;
	}
	
	public class Match {
		public String name, innerName;
		public String replacement;
		
		public Match(int start, int end) {
			name = input.substring(start, end);
			int innerStart = (name.startsWith("${")) ? 2: 1,
				innerEnd = (name.endsWith("}")) ? name.length()-1: name.length();
			innerName = name.substring(innerStart, innerEnd);
			for (ParameterFilters.Type type : ParameterFilters.Type.values())
				if (type.filter.test(innerName)) {
//					System.out.println("Filtered by "+type.name());
					replacement = type.handler.apply(innerName);
					break;
				}
		}
	}
}
