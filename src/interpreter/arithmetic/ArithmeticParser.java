package interpreter.arithmetic;

import interpreter.arithmetic.ast.AST;
import interpreter.arithmetic.ast.BinaryOperator;
import interpreter.arithmetic.ast.Composition;
import interpreter.arithmetic.ast.UnaryOperator;
import interpreter.arithmetic.ast.Value;
import interpreter.exceptions.ParsingException;
import interpreter.generic.IterativeParser;
import interpreter.generic.Token;

/** calculator.Parser grammar
input = expr
expr:			complex_expr
complex_expr: 	xor_expr ("|" xor_expr)*
xor_expr: 		and_expr ("^" and_expr)*
and_expr: 		shift_expr ("&" shift_expr)*
shift_expr: 	arith_expr (("<<"|">>") arith_expr)*
arith_expr: 	term (("+"|"-") term)*
term: 			factor (("*"|"/"|"%"|"//") factor)*
factor: 		("+"|"-"|"~") factor | power
power: 			atom ["**" factor]
atom: 			func_expr
                | "(" complex_expr ")"
                | number
                | "PI"
func_expr:     ("EXP"|"LN"|"LOG"|"SQRT"|"COS"|"SIN"|"TAN"|"ABS"|...) "(" complex_expr ")"
 */
public class ArithmeticParser extends IterativeParser<Type, AST> {
	private static final Type[] arithOps = Type.getArithOps(), 
		factorOps = Type.getFactorOps(),
		shiftOps = Type.getShiftOps();
	
	public ArithmeticParser(ArithmeticLexer lexer) throws ParsingException {
		super(lexer);
	}
	
	private boolean isFactorOp() {
		return matches(factorOps);
	}
	
	private boolean isArithOp() {
		return matches(arithOps);
	}
	
	private boolean isShiftOp() {
		return matches(shiftOps);
	}
	
	// atom: ("EXP"|"LN"|"LOG"|"SQRT"|"COS"|"SIN"|"TAN"|"ABS"|...) '(' complex_expr ')'
    //      | '(' complex_expr ')'
    //      | number
	//		| PI
	private AST atom() throws ParsingException {
		Token<Type> token = current;
		switch (token.type) {
			case COS: case COS_HYPERBOLIC: case ARC_COS: case ARC_COS_HYPERBOLIC:
			case SIN: case SIN_HYPERBOLIC: case ARC_SIN: case ARC_SIN_HYPERBOLIC:
			case TAN: case TAN_HYPERBOLIC: case ARC_TAN: case ARC_TAN_HYPERBOLIC:
			case ABS: case EXP: case LN: case LOG: case FLOOR: case CEIL: case ROUND: case SQRT:
			case COSECANT_HYPERBOLIC: case SECANT_HYPERBOLIC: case COTANGENT_HYPERBOLIC:
			case ARC_COSECANT_HYPERBOLIC: case ARC_SECANT_HYPERBOLIC: case ARC_COTANGENT_HYPERBOLIC:
				Token<Type> funcToken = token;
				consume(funcToken.type);
				consume(Type.LEFT_PARENTHESIS);
				AST expr = complex_expr();
				consume(Type.RIGHT_PARENTHESIS);
				return new Composition(funcToken, expr);
			case LEFT_PARENTHESIS:
				consume(Type.LEFT_PARENTHESIS);
				AST node = complex_expr();
				consume(Type.RIGHT_PARENTHESIS);
				return node;
			case NUMBER:
				consume(Type.NUMBER);
				return new Value(token);
			case PI:
				consume(Type.PI);
				return new Value(token);
			default: return error();
		}
	}
	
	// power: atom ['**' factor]
	private AST power() throws ParsingException {
		AST node = atom();
		Token<Type> token = current;
		if (token.type == Type.POWER) {
			consume(Type.POWER);
			node = new BinaryOperator(node, token, factor());
		} 
		return node;
	}
	
	// factor: ('+'|'-'|'~') factor | power
	private AST factor() throws ParsingException {
		Token<Type> token = current;
		switch (token.type) {
			case PLUS:
			case MINUS:
			case FLIP:
				consume(token.type);
				return new UnaryOperator(token, factor());
			default: return power();
		}
	}
	
	// term: factor (('*'|'/'|'%'|'//') factor)*
	private AST term() throws ParsingException {
		AST node = factor();
		Token<Type> token;
		while (isFactorOp()) {
			token = current;
			consume(token.type);
			node = new BinaryOperator(node, token, factor());
		}
		return node;
	}
	
	// arith_expr: term (('+'|'-') term)*
	private AST arith_expr() throws ParsingException {
		AST node = term();
		Token<Type> token;
		while (isArithOp()) {
			token = current;
			consume(token.type);
			node = new BinaryOperator(node, token, term());
		}
		return node;
	}
	
	// shift_expr: arith_expr (('<<'|'>>') arith_expr)*
	private AST shift_expr() throws ParsingException {
		AST node = arith_expr();
		Token<Type> token;
		while (isShiftOp()) {
			token = current;
			consume(token.type);
			node = new BinaryOperator(node, token, arith_expr());
		}
		return node;
	}
	
	// and_expr: shift_expr ('&' shift_expr)*
	private AST and_expr() throws ParsingException {
		AST node = shift_expr();
		Token<Type> token;
		while (current.type == Type.AND) {
			token = current;
			consume(token.type);
			node = new BinaryOperator(node, token, shift_expr());
		}
		return node;
	}
	
	// xor_expr: and_expr ('^' and_expr)*
	private AST xor_expr() throws ParsingException {
		AST node = and_expr();
		Token<Type> token;
		while (current.type == Type.XOR) {
			token = current;
			consume(token.type);
			node = new BinaryOperator(node, token, and_expr());
		}
		return node;
	}
	
	// complex_expr: xor_expr ('|' xor_expr)*
	private AST complex_expr() throws ParsingException {
		AST node = xor_expr();
		Token<Type> token;
		while (current.type == Type.OR) {
			token = current;
			consume(token.type);
			node = new BinaryOperator(node, token, xor_expr());
		} 
		return node;
	} 
	
	// expr: complex_expr
	@Override
	public AST parse() throws ParsingException {
		return complex_expr();
	}
}
