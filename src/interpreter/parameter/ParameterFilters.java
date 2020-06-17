package interpreter.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import model.ConsoleContext;
import util.StrUtil;

public class ParameterFilters {
	private ConsoleContext context;
	private List<Predicate<String>> filters;
	private List<Function<String, String>> handlers;
	
	public ParameterFilters(ConsoleContext context) {
		this.context = context;
		filters = createFilters();
		handlers = createHandlers();
	}
	
	private List<Predicate<String>> createFilters() {
		List<Predicate<String>> filters = new ArrayList<>(); 
		filters.add(ParameterFilters::checkSimple);
		filters.add(ParameterFilters::checkCaseModification);
		filters.add(ParameterFilters::checkVariableNameExpansion);
		filters.add(ParameterFilters::checkIndirection);
		filters.add(ParameterFilters::checkStringLength);
		filters.add(ParameterFilters::checkSubstringRemoval);
		filters.add(ParameterFilters::checkSearchReplace);
		filters.add(ParameterFilters::checkUseDefault);
		filters.add(ParameterFilters::checkAssignDefault);
		filters.add(ParameterFilters::checkUseAlternative);
		filters.add(ParameterFilters::checkDisplayError);
		filters.add(ParameterFilters::checkSubstringExpansion);
		return filters;
	}
	
	private List<Function<String, String>> createHandlers() {
		List<Function<String, String>> handlers = new ArrayList<>();
		handlers.add(this::handleSimple);
		handlers.add(this::handleCaseModification);
		handlers.add(this::handleVariableNameExpansion);
		handlers.add(this::handleIndirection);
		handlers.add(this::handleStringLength);
		handlers.add(this::handleSubstringRemoval);
		handlers.add(this::handleSearchReplace);
		handlers.add(this::handleUseDefault);
		handlers.add(this::handleAssignDefault);
		handlers.add(this::handleUseAlternative);
		handlers.add(this::handleDisplayError);
		handlers.add(this::handleSubstringExpansion);
		return handlers;
	}
	
	public List<Predicate<String>> getFilters() {
		return filters;
	}

	public List<Function<String, String>> getHandlers() {
		return handlers;
	}

	/* Filter checking implementations */
	
	public static boolean checkSimple(String str) {
		String ANY_SPECIAL_CHARS = ".*[\\Q:^,~*@#%/-+=!?\\E].*"; // \Q..\E disables special regex chars 
		return !str.matches(ANY_SPECIAL_CHARS);
	}
	
	public static boolean checkCaseModification(String str) {
		return str.endsWith("^^") || str.endsWith("^")
				|| str.endsWith(",,") || str.endsWith(",")
				|| str.endsWith("~~") || str.endsWith("~");
	}
	
	public static boolean checkVariableNameExpansion(String str) {
		return (str.startsWith("!") && str.endsWith("*"))
				|| (str.startsWith("!") && str.endsWith("@"));
	}
	
	public static boolean checkIndirection(String str) {
		return str.startsWith("!");
	}
	
	public static boolean checkStringLength(String str) {
		return str.startsWith("#");
	}
	
	public static boolean checkSubstringRemoval(String str) {
		return (str.contains("##") || (str.contains("#") && !str.startsWith("#")))
			|| (str.contains("%%") || (str.contains("%") && !str.startsWith("%")));
	}
	
	public static boolean checkSearchReplace(String str) {
		return str.contains("//") || (str.contains("/") && !str.startsWith("#"));
	}
	
	public static boolean checkUseDefault(String str) {
		return str.contains(":-") || str.contains("-");
	}
	
	public static boolean checkAssignDefault(String str) {
		return str.contains(":=") || str.contains("=");
	}
	
	public static boolean checkUseAlternative(String str) {
		return str.contains(":+") || str.contains("+");
	}
	
	public static boolean checkDisplayError(String str) {
		return str.contains(":?") || str.contains("?");
	}
	
	public static boolean checkSubstringExpansion(String str) {
		return str.contains(":");
	}
	
	/* Handlers implementations */
	
	public String handleSimple(String str) {
		String value = context.get(str); 
		value = (value == null) ? "" : value;
		return value;
	}
	
	public String handleCaseModification(String str) {
		if (str.endsWith("^^"))	// all upper case
			return handleSimple(str.substring(0, str.length()-2)).toUpperCase();
		else if (str.endsWith("^")) { // first upper case
			str = handleSimple(str.substring(0, str.length()-1));
			return (""+str.charAt(0)).toUpperCase() + str.substring(1);
		} else if (str.endsWith(",,")) // all lower case
			return handleSimple(str.substring(0, str.length()-2)).toLowerCase();
		else if (str.endsWith(",")) { // first lower case
			str = handleSimple(str.substring(0, str.length()-1));
			return (""+str.charAt(0)).toLowerCase() + str.substring(1);
		} else if (str.endsWith("~~"))
			return StrUtil.reverseCase(handleSimple(str.substring(0, str.length()-2)));
		else if (str.endsWith("~")) {
			str = handleSimple(str.substring(0, str.length()-1));
			return StrUtil.reverseCase(""+str.charAt(0)) + str.substring(1);
		}
 		return str;
	}
	
	public String handleVariableNameExpansion(String str) {
		final String prefix = str.substring(1, str.length()-1);
		StringBuilder sb = new StringBuilder();
		context.getEnvironment().forEach((k, v) -> {
			if (k.startsWith(prefix))
				sb.append(k+" ");
		});
		return sb.toString().trim();
	}
	
	public String handleIndirection(String str) {
		return handleSimple(handleSimple(str.substring(1)));
	}
	
	public String handleStringLength(String str) {
		return ""+handleSimple(str.substring(1)).length();
	}
	
	public String handleSubstringRemoval(String str) {
		String[] split;
		String result;
		if (str.contains("##")) {
			split = str.split(StrUtil.unescapedFormat("##"));
			result = handleSimple(split[0]);
			return StrUtil.removeLargestStart(split[1], result);
		} else if (str.contains("#")) {
			split = str.split(StrUtil.unescapedFormat("#"));
			result = handleSimple(split[0]);
			return StrUtil.removeSmallestStart(split[1], result);
		} else if (str.contains("%%")) {
			split = str.split(StrUtil.unescapedFormat("%%"));
			result = handleSimple(split[0]);
			return StrUtil.removeLargestEnd(split[1], result);
		} else if (str.contains("%")) {
			split = str.split(StrUtil.unescapedFormat("%"));
			result = handleSimple(split[0]);
			return StrUtil.removeSmallestEnd(split[1], result);
		}
		return str;
	}
	
	public String handleSearchReplace(String str) {
		String[] split0, split1;
		String result = "", match = "", replacement = "";
		boolean all = false;
		if (str.contains("//")) {
			all = true;
			split0 = str.split(StrUtil.unescapedFormat("//")); 
			split1 = split0[1].split(StrUtil.unescapedFormat("/"));
			result = handleSimple(split0[0]);
			if (split1.length == 2) {
				match = split1[0];
				replacement = split1[1];
			} else match = split0[1];
		} else if (str.contains("/")) {
			split0 = str.split(StrUtil.unescapedFormat("/"));
			result = handleSimple(split0[0]);
			match = split0[1];
			if (split0.length == 3)
				replacement = split0[2];
		}
		return all ? 
				result.replaceAll(match, replacement) : 
				result.replaceFirst(match, replacement);
	}
	
	public String handleUseDefault(String str) {
		String[] split;
		String result;
		if (str.contains(":-")) {
			split = str.split(StrUtil.unescapedFormat(":-"));
			result = context.get(split[0]);
			return (result == null || result.equals("")) ? split[1] : result;
		} else if (str.contains("-")) {
			split = str.split(StrUtil.unescapedFormat("-"));
			result = context.get(split[0]);
			return (result == null) ? split[1] : result;
		}
		return str;
	}
	
	public String handleAssignDefault(String str) {
		String[] split;
		String result;
		if (str.contains(":=")) {
			split = str.split(StrUtil.unescapedFormat(":="));
			result = context.get(split[0]);
			if (result == null || result.equals("")) {
				context.set(split[0], split[1]);
				return split[1];
			} return result;
		} else if (str.contains("=")) {
			split = str.split(StrUtil.unescapedFormat("="));
			result = context.get(split[0]);
			if (result == null) {
				context.set(split[0],  split[1]);
				return split[1];
			} return result;
		}
		return str;
	}
	
	public String handleUseAlternative(String str) {
		String[] split;
		String result;
		if (str.contains(":+")) {
			split = str.split(StrUtil.unescapedFormat(":\\+"));
			result = context.get(split[0]);
			return (result == null || result.equals("")) ? "" : split[1];
		} else if (str.contains("+")) {
			split = str.split(StrUtil.unescapedFormat("\\+"));
			result = context.get(split[0]);
			return (result == null) ? "" : split[1];
		}
		return str;
	}
	
	public String handleDisplayError(String str) {
		String[] split;
		String result;
		if (str.contains(":?")) {
			split = str.split(StrUtil.unescapedFormat(":\\?"));
			result = context.get(split[0]);
			if (result == null || result.equals("")) {
				String message = String.format("Error: %s: %s", split[0], split[1]);
//				context.getOutputStream().println(message);
				throw new IllegalArgumentException(message);
			} return result;
		} else if (str.contains("?")) {
			split = str.split(StrUtil.unescapedFormat("\\?"));
			result = context.get(split[0]);
			if (result == null) {
				String message = String.format("Error: %s: %s", split[0], split[1]);
//				context.getOutputStream().println(message);
				throw new IllegalArgumentException(message);
			} return result;
		}
		return str;
	}
	
	public String handleSubstringExpansion(String str) {
		String[] split = str.split(StrUtil.unescapedFormat(":"));
		String result = handleSimple(split[0]); 
		int offset = Integer.parseInt(split[1]), 
			length = (split.length == 3) ?
					length = Integer.parseInt(split[2]) : 
					-1;
		return (length == -1) ? 
				result.substring(offset) : 
				result.substring(offset, offset+length);
	}
}
