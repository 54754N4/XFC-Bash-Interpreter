package interpreter.util;

public class StrUtil {

	public static String reverseCase(String text) {	// helper
	    StringBuilder sb = new StringBuilder();
	    for (char c : text.toCharArray())
	        sb.append((Character.isUpperCase(c)) ? 
	    			Character.toLowerCase(c) : 
	    			Character.toUpperCase(c));
	    return sb.toString();
	}

	public static int indexOfUnescaped(String match, String input, int from) {
		for (int i=from; i<input.length(); i++)
			if (input.substring(i).startsWith(match) && input.charAt(i-1) != '\\')
					return i;
		return -1;
	}

	public static String unescapedFormat(String regex) {
		return "(?<!\\\\)"+regex;
	}
	
	private static String fixGlobbingRegex(String regex) {
		return (regex.contains("*") && !regex.contains(".*")) 
				? regex.replace("*", ".*") : regex;
	}
	
	public static String removeSmallestStart(String regex, String input) {
		regex = fixGlobbingRegex(regex);
		for (int i=0; i<input.length(); i++)
			if (input.substring(0, i).matches(regex))
				return input.substring(i);
		return input;
	}

	public static String removeLargestStart(String regex, String input) {
		regex = fixGlobbingRegex(regex);
		int max = 0;
		for (int i=0; i<input.length(); i++)
			if (input.substring(0, i).matches(regex) && i > max)
				max = i;
		return input.substring(max);
	}

	public static String removeSmallestEnd(String regex, String input) {
		regex = fixGlobbingRegex(regex);
		for (int i=input.length()-1; i>=0; i--)
			if (input.substring(i, input.length()).matches(regex))
				return input.substring(0, i);
		return input;
	}

	public static String removeLargestEnd(String regex, String input) {
		regex = fixGlobbingRegex(regex);
		int min = input.length();
		for (int i=input.length()-1; i>=0; i--)
			if (input.substring(i, input.length()).matches(regex) && i < min)
				min = i;
		return input.substring(0, min);
	}
}
