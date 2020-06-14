package interpreter.generic;

public class Token<T extends Enum<T>> {
	public final T type;
	public final String value;
	
	public Token(T type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public Token(T type) {
		this(type, type.toString());
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", type.name(), value);
	}
}
