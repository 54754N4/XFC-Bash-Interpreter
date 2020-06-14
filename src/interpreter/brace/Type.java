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
    
    private String representation;
	
	private Type(String representation) {
		this.representation = representation;
	}
	
	@Override
	public String toString() {
		return representation;
	}
}
