package interpreter.parameter;

import java.util.function.Function;
import java.util.function.Predicate;

import model.ConsoleContext;
import util.StrUtil;

public class ParameterFilters {
	private static ConsoleContext context = ConsoleContext.INSTANCE;
	
	public static enum Type {
		SIMPLE(
				ParameterFilters::checkSimple,
				ParameterFilters::handleSimple),
		CASE_MODIFICATION(
				ParameterFilters::checkCaseModification,
				ParameterFilters::handleCaseModification),
		VARIABLE_NAME_EXPANSION(
				ParameterFilters::checkVariableNameExpansion,
				ParameterFilters::handleVariableNameExpansion),
		INDIRECTION(
				ParameterFilters::checkIndirection,
				ParameterFilters::handleIndirection),	// nneds to be after variable_name_expansion
		STRING_LENGTH(
				ParameterFilters::checkStringLength,
				ParameterFilters::handleStringLength),	// needs to be before substring_removal
		SUBSTRING_REMOVAL(
				ParameterFilters::checkSubstringRemoval,
				ParameterFilters::handleSubstringRemoval),
		SEARCH_REPLACE(
				ParameterFilters::checkSearchReplace,
				ParameterFilters::handleSearchReplace),
		USE_DEFAULT(
				ParameterFilters::checkUseDefault,
				ParameterFilters::handleUseDefault),
		ASSIGN_DEFAULT(
				ParameterFilters::checkAssignDefault,
				ParameterFilters::handleAssignDefault),
		USE_ALTERNATIVE(
				ParameterFilters::checkUseAlternative,
				ParameterFilters::handleUseAlternative),
		DISPLAY_ERROR(
				ParameterFilters::checkDisplayError,
				ParameterFilters::handleDisplayError),
		SUBSTRING_EXPANSION(
				ParameterFilters::checkSubstringExpansion,
				ParameterFilters::handleSubstringExpansion);	// needs to be last check
		
		public final Predicate<String> filter;
		public final Function<String, String> handler;
		
		private Type(Predicate<String> filter, Function<String, String> handler) {
			this.filter = filter;
			this.handler = handler;
		}
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
	
	public static String handleSimple(String str) {
		String value = context.get(str); 
		value = (value == null) ? "" : value;
		return value;
	}
	
	public static String handleCaseModification(String str) {
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
	
	public static String handleVariableNameExpansion(String str) {
		final String prefix = str.substring(1, str.length()-1);
		StringBuilder sb = new StringBuilder();
		context.getEnvironment().forEach((k, v) -> {
			if (k.startsWith(prefix))
				sb.append(k+" ");
		});
		return sb.toString().trim();
	}
	
	public static String handleIndirection(String str) {
		return handleSimple(handleSimple(str.substring(1)));
	}
	
	public static String handleStringLength(String str) {
		return ""+handleSimple(str.substring(1)).length();
	}
	
	public static String handleSubstringRemoval(String str) {
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
	
	public static String handleSearchReplace(String str) {
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
	
	public static String handleUseDefault(String str) {
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
	
	public static String handleAssignDefault(String str) {
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
	
	public static String handleUseAlternative(String str) {
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
	
	public static String handleDisplayError(String str) {
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
	
	public static String handleSubstringExpansion(String str) {
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
