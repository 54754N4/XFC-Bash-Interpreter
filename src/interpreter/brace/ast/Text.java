package interpreter.brace.ast;

import java.util.Arrays;
import java.util.List;

import interpreter.brace.Type;
import interpreter.generic.Token;

public class Text extends Expression {
	public final Token<Type> token;
	
	public Text(Token<Type> token) {
		this.token = token;
	}

	@Override
	public List<String> evaluate() {
		return Arrays.asList(token.value);
	}
}