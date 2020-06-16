package interpreter.bash;

public enum Type {
	IF, THEN, ELSE, ELIF, FI, CASE, ESAC, FOR, SELECT, WHILE, UNTIL, DO, DONE, FUNCTION, COPROC,
	COND_START("[["), COND_END("]]"),
	IN, BANG("!"), TIME, TIMEOPT("-p"), TIMEIGN("--"),
	
	AND_AND("&&"), BAR_BAR("||"), GREATER_GREATER(">>"), LESS_LESS("<<"), LESS_AND("<&"), 
	GREATER_AND(">&"), SEMI_SEMI(";;"), SEMI_AND(";&"), SEMI_SEMI_AND(";;&"),
	LESS_LESS_MINUS("<<-"), LESS_LESS_LESS("<<<"),	// i might not implement here-docs
	AND_GREATER("&>"), AND_GREATER_GREATER("&>>"), LESS_GREATER("<>"), GREATER_BAR(">|"), 
	BAR_AND("|&"),
	CURLY_START("{"), CURLY_END("}"), PAREN_START("("), PAREN_END(")"),
	GREATER(">"), LESS("<"), MINUS("-"), SEMI(";"), BAR("|"), AND("&"), 
	
	NEWLINE("\n"),
	EOF("EOF"),
	
	//CUSTOM WORDS
	WORD, ASSIGNMENT_WORD, NUMBER, ARITH_CMD, ARITH_FOR_EXPRS, COND_CMD;
	
	public String value;
	
	private Type() {
		value = name().toLowerCase();
	}
	
	private Type(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
