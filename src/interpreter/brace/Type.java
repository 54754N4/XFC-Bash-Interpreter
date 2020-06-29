package interpreter.brace;

/*
 * text: 	[^{}\.,]+
 * char: 	[a-zA-Z]
 * num: 	0*[0-9]
 */
public enum Type {
	CHAR("CHAR"), TEXT("TEXT"), NUMBER("NUMBER"), 
	EXPR_START("{"), EXPR_END("}"),
    COMMA(","), RANGE(".."), MINUS("-"),
    EOF("EOF");
    
    private final String value;
	
	private Type(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
