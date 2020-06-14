package interpreter.arithmetic;

import interpreter.arithmetic.ast.BinaryOperator;
import interpreter.arithmetic.ast.Composition;
import interpreter.arithmetic.ast.UnaryOperator;
import interpreter.arithmetic.ast.Value;
import interpreter.arithmetic.ast.Visitor;
import interpreter.exceptions.ParsingException;

public class Interpreter implements Visitor {
	public String input;
	private Parser parser;
	
	public Interpreter(String input)  {
		this.input = input;
	}
	
	public Double interpret() throws ParsingException {
		parser = new Parser(new Lexer(input));
		return visit(parser.parse());
	}
	
	@Override
	public Double visit(Value node) {
		if (node.token.type == Type.PI)
			return Math.PI;
		return Double.parseDouble(node.token.value);
	}

	@Override
	public Double visit(UnaryOperator node) throws ParsingException {
		switch (node.token.type) {
			case PLUS: return visit(node.expr);
			case MINUS: return -visit(node.expr);
			case FLIP: return (double) ~visit(node.expr).intValue();
			default:
				parser.error();
				return null;
		} 
	}

	@Override
	public Double visit(BinaryOperator node) throws ParsingException {
		switch (node.token.type) {
			case PLUS: return visit(node.left) + visit(node.right);
			case MINUS: return visit(node.left) - visit(node.right);
			case MULTIPLY: return visit(node.left) * visit(node.right);
			case DIVIDE: return visit(node.left) / visit(node.right);
			case FLOOR_DIVIDE: return (double) (visit(node.left).intValue() / visit(node.right).intValue());
			case POWER: return Math.pow(visit(node.left), visit(node.right));
			case AND: return (double) (visit(node.left).intValue() & visit(node.right).intValue());
			case OR: return (double) (visit(node.left).intValue() | visit(node.right).intValue());
			case XOR: return (double) (visit(node.left).intValue() ^ visit(node.right).intValue());
			case LEFT_SHIFT: return (double) (visit(node.left).intValue() << visit(node.right).intValue());
			case RIGHT_SHIFT: return (double) (visit(node.left).intValue() >> visit(node.right).intValue());
			default: 
				parser.error(); 
				return null; 
		}
	}

	@Override
	public Double visit(Composition node) throws ParsingException {
		Double value = visit(node.expr);
		switch (node.token.type) {
			case ABS: return Math.abs(value);
			case SQRT: return Math.sqrt(value);
			case EXP: return Math.exp(value);
			case LN: return Math.log(value);
			case LOG: return Math.log10(value);
			case COS: return Math.cos(value);
			case SIN: return Math.sin(value);
			case TAN: return Math.tan(value);
			case ARC_COS: return Math.acos(value);
			case ARC_SIN: return Math.asin(value);
			case ARC_TAN: return Math.atan(value);
			case COS_HYPERBOLIC: return Math.cosh(value);
			case SIN_HYPERBOLIC: return Math.sinh(value);
			case TAN_HYPERBOLIC: return Math.tanh(value);
			case COSECANT_HYPERBOLIC: return Trigonometry.csch(value);
			case SECANT_HYPERBOLIC: return Trigonometry.sech(value);
			case COTANGENT_HYPERBOLIC: return Trigonometry.coth(value);
			case ARC_COS_HYPERBOLIC: return Trigonometry.acosh(value);
			case ARC_SIN_HYPERBOLIC: return Trigonometry.asinh(value);
			case ARC_TAN_HYPERBOLIC: return Trigonometry.atanh(value);
			case ARC_COSECANT_HYPERBOLIC: return Trigonometry.acsch(value);
			case ARC_SECANT_HYPERBOLIC: return Trigonometry.asech(value);
			case ARC_COTANGENT_HYPERBOLIC: return Trigonometry.acoth(value);
			case CEIL: return Math.ceil(value);
			case FLOOR: return Math.floor(value);
			case ROUND: return (double) Math.round(value);
			default: 
				parser.error();
				return null;
		} 
	}

}
