package interpreter.arithmetic.ast;

import interpreter.arithmetic.Type;
import interpreter.generic.Token;

public class BinaryOperator extends AST {
	public final AST left, right;
	public final Token<Type> token;
	
	public BinaryOperator(AST left, Token<Type> token, AST right) {
		this.left = left;
		this.right = right;
		this.token = token;
	}
}
