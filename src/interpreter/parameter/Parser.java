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
		for (Match match : matches) 
			result = result.replace(match.name, match.replacement);
		return result;
	}
	
	private final class Match {
		public String name, innerName;
		public String replacement;
		
		public Match(int start, int end) {
			name = input.substring(start, end);
			int innerStart = (name.startsWith("${")) ? 2: 1,
				innerEnd = (name.endsWith("}")) ? name.length()-1: name.length();
			innerName = name.substring(innerStart, innerEnd);
			for (ParameterFilters.Type type : ParameterFilters.Type.values()) {
				if (type.filter.test(innerName)) {
					replacement = type.handler.apply(innerName);
					break;
				}
			}
		}
	}
	
	/* Less object creation and memory footprint (due to less string storage):
	
	public String parse(String input) {
		reset(input);
		String result = input;
		List<Point> matches = new ArrayList<>();
		Point matched;
		String name, innerName, replacement = "";
		while (!(matched = findNext()).equals(NOT_FOUND)) 
			matches.add(matched);
		for (int i=0, offset; i<matches.size(); i++) {
			matched = matches.get(i);
			name = input.substring(matched.x, matched.y);
			int innerStart = (name.startsWith("${")) ? 2: 1,
				innerEnd = (name.endsWith("}")) ? name.length()-1: name.length();
			innerName = name.substring(innerStart, innerEnd);
			for (ParameterFilters.Type type : ParameterFilters.Type.values()) {
				if (type.filter.test(innerName)) {
					replacement = type.handler.apply(innerName);
					break;
				}
			};
			result = result.replace(name, replacement);
			offset = replacement.length()-name.length();
			offset(offset, i, matches);
		}
		return result;
	}
	
	private static void offset(int offset, int from, List<Point> matches) {
		Point point, offsetted;
		for (int i=from; i<matches.size(); i++) {
			point = matches.get(i);
			offsetted = new Point(point.x+offset, point.y+offset);
			matches.set(i, offsetted);
		}
	} 
	
	 */
}
