package interpreter.bool;

import interpreter.bool.ast.AST;
import interpreter.bool.ast.BinaryOperator;
import interpreter.bool.ast.Constant;
import interpreter.bool.ast.UnaryOperator;
import interpreter.exceptions.ParsingException;
import interpreter.generic.IterativeParser;
import interpreter.generic.Lexer;
import interpreter.generic.Token;

/** >Notes
 * - Unary ops have higher precendence
 * - All ops have same precendence relative to other 
 * ops with the same operand count
 * - stacking/nesting unary ops doesn't make sense (in our case) 
 * 
 * >Grammar
 * expression: 	binary
 * binary:		unary [BINARY_OP binary]*
 * unary:		UNARY_OP? factor
 * factor:		true|false|number|string
 * 				| LEFT_PAREN binary RIGHT_PAREN
 */
//Verify : https://docs.oracle.com/javase/tutorial/essential/io/fileAttr.html

public class BooleanParser extends IterativeParser<Type, AST> {
	
	public BooleanParser(Lexer<Type> lexer) throws ParsingException {
		super(lexer);
	}
	
	private boolean isConstant() {
		return is(new Type[] {Type.CONSTANT, Type.NUMBER, Type.STRING});
	}
	
	/* factor:		true|false|number|string
	 * 				| LEFT_PAREN binary RIGHT_PAREN
	 */
	private AST factor() throws ParsingException {
		Token<Type> token;
		if (isConstant()) {
			token = current;
			consume(token.type);
			return new Constant(token);
		} else if (is(Type.LEFT_PAREN)) {
			consume(Type.LEFT_PAREN);
			AST ast = binary();
			consume(Type.RIGHT_PAREN);
			return ast;
		} return error();
	}
	
	// unary:		UNARY_OP? factor
	private AST unary() throws ParsingException {
		if (current.type.isUnary()) {
			Token<Type> token = current;
			consume(token.type);
			return new UnaryOperator(token, factor());
		} 
		return factor();
	}
	
	// binary:		unary [BINARY_OP binary]*
	private AST binary() throws ParsingException {
		AST unary = unary();
		Token<Type> token;
		while (current.type.isBinary()) {
			token = current;
			consume(token.type);
			unary = new BinaryOperator(unary, token, unary());
		}
		return unary;
	}
	
	@Override
	public AST parse() throws ParsingException {
		return binary();
	}
}
