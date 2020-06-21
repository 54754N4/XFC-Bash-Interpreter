package interpreter.bash;

public enum Type {
	IF, THEN, ELSE, ELIF, FI, CASE, ESAC, FOR, SELECT, 
	WHILE, UNTIL, DO, DONE, FUNCTION, COPROC, IN, 
	COND_START("[["), COND_END("]]"),
	BANG("!"), TIME, TIMEOPT("-p"), TIMEIGN("--"),
	
	AND_AND("&&"), BAR_BAR("||"), BAR_AND("|&"), GREATER_GREATER(">>"), LESS_AND("<&"), 
	GREATER_AND(">&"), AND_GREATER("&>"), AND_GREATER_GREATER("&>>"), 
	LESS_GREATER("<>"), GREATER_BAR(">|"),
	LESS_LESS("<<"), LESS_LESS_MINUS("<<-"), LESS_LESS_LESS("<<<"),						// heredocs
	SEMI_SEMI(";;"), SEMI_AND(";&"), SEMI_SEMI_AND(";;&"),								// case terminators
	GREATER(">"), LESS("<"), DASH("-"), SEMI(";"), BAR("|"), AND("&"), 
	CURLY_START("{"), CURLY_END("}"), PAREN_START("("), PAREN_END(")"),
	
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
