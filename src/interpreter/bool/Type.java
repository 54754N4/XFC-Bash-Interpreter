package interpreter.bool;

// Refs.: https://www.gnu.org/software/bash/manual/html_node/Bash-Conditional-Expressions.html#Bash-Conditional-Expressions
public enum Type {
	// File based unary operators
	EXISTS("-e"), BLOCK_SPECIAL("-b"), CHAR_SPECIAL("-c"),
	DIRECTORY("-d"), REGULAR("-f"), SYMBOLIC_LINK_1("-h"), SYMBOLIC_LINK_2("-L"),
	STICKY_BIT_SET("-k"), NAMED_PIPE("-p"), READABLE("-r"),
	NON_ZERO_SIZE("-s"), TERMINAL("-t"), SET_UID_BIT_SET("-u"), WRITABLE("-w"),
	EXECUTABLE("-x"), GROUP_ID("-G"), MODIFIED_SINCE_READ("-N"), 
	OWNED_BY_EUID("-O"), SOCKET("-S"),
	// File based binary operators
	FILE_EQUAL("-ef"), NEWER_THAN("-nt"), OLDER_THAN("-ot"),
	// Unary operators
	IS_ENABLED("-o"), IS_ASSIGNED("-v"), IS_REFERENCE("-R"), 					// for variables
	IS_ZERO("-z"), IS_NON_ZERO("-n"), 											// for strings length
	NOT("!"),
	// Binary operators
	STR_EQUAL("=="), STR_EQUAL_POSIX("="), STR_MATCH("=~"), 
	STR_NOT_EQUAL("!="), STR_LESS_THAN("<"), STR_GREATER_THAN(">"), 			// sorted lexicographically
	EQUAL("-eq"), NOT_EQUAL("-ne"), LESS_THAN("-lt"),
	LESS_EQUAL("-le"), GREATER_THAN("-gt"), GREATER_EQUAL("-ge"),
	// Grammar tokens
	CONSTANT, NUMBER, STRING, LEFT_PAREN("("), RIGHT_PAREN(")"), EOF;
	
	public final String value;
	
	private Type() {
		value = name();
	}
	
	private Type(String value) {
		this.value = value;
	}
	
	public boolean isGrammarToken() {
		switch (this) {
			case CONSTANT: case NUMBER: case STRING: 
			case LEFT_PAREN: case RIGHT_PAREN: case EOF:
				return true;
			default: return false;
		}
	}
	
	public boolean isUnary() {	 // cause too many unary =v
		return !isGrammarToken() && !isBinary();
	}
	
	public boolean isBinary() {
		switch (this) {
			case STR_EQUAL: case STR_EQUAL_POSIX: case STR_MATCH: case STR_NOT_EQUAL: 
			case STR_LESS_THAN: case STR_GREATER_THAN: case EQUAL: case NOT_EQUAL: 
			case LESS_THAN: case LESS_EQUAL: case GREATER_THAN: case GREATER_EQUAL:
			case FILE_EQUAL: case NEWER_THAN: case OLDER_THAN:
				return true;
			default: return false;
		}
	}
	
	public static Type from(String value) {
		for (Type type: values())
			if (type.value.equals(value))
				return type;
		return null;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
