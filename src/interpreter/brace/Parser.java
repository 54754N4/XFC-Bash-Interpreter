package interpreter.brace;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import interpreter.brace.ast.CSVExpression;
import interpreter.brace.ast.CharRangeExpression;
import interpreter.brace.ast.Expression;
import interpreter.brace.ast.RangeExpression;
import interpreter.brace.ast.Text;
import interpreter.exceptions.ParsingException;
import interpreter.generic.Token;

/*  expr:		word
 * 				| word? '{' rangeExpr
 *  			| word? '{' csvExpr
 *  rangeExpr:	num '..' num ['..' num] '}' expr
 *  			| char '..' char ['..' num] '}' expr	// char range differs from int range 
 *  csvExpr:	expr [',' expr]+ '}' expr
 *  word: 		text
 * 				| char
 * 				| number
 * 				| empty
 * 				| expression
 *  
 *  text: 		[^{}\.,]+
 *  char: 		[a-zA-Z]
 *  num: 		0*[0-9]
 */
public class Parser { 
	private Lexer lexer;
	private List<Token<Type>> tokens;
	private Stack<Integer> stack;
	private int current;
	
	public Parser(Lexer lexer) throws ParsingException {
		this.lexer = lexer;
		current = 0;
		stack = new Stack<>();
		tokens = new ArrayList<>();
		Token<Type> token;
		while ((token = lexer.getNextToken()).type != Type.EOF)
			tokens.add(token);
		tokens.add(new Token<>(Type.EOF));
	}
	
	private Token<Type> current() {
		return tokens.get(current);
	}
	
	private void save() {
		stack.push(current);
	}
	
	private void backtrack() {
		current = stack.pop();
	}
	
	private boolean isType(Type...types) {
		if (current >= tokens.size())
			return false;
		for (Type type : types)
			if (current().type == type)
				return true;
		return false;
	}
	
	private void consume(Type type) throws ParsingException {
		if (isType(type)) 
			current++;
		else 
			lexer.cry("Expected type :"+type.name()+" "+type);
	}

	// Production Rules
	
	/* word: 	text
	 * 			| char
	 * 			| number
	 * 			| empty
	 * 			| expression
	 */
	private Expression word() throws ParsingException {
		Token<Type> token = current();
		if (isType(Type.TEXT, Type.CHAR, Type.NUMBER)) {
			consume(token.type);
			return new Text(token);
		} else if (isType(Type.EOF, Type.EXPR_START))
			return new Text(new Token<>(Type.TEXT, ""));
		else return expression();
	}
	
	/*  rangeExpr:	num '..' num ['..' num] '}' expr
	 *  			| char '..' char ['..' num] '}' expr
	 */
	private Expression rangeExpression(Expression preamble) throws ParsingException {
		Expression postscript = new Text(new Token<>(Type.TEXT, ""));
		Token<Type> start = current(), 
				increment = new Token<>(Type.NUMBER, "1");
		consume(start.type);
		consume(Type.RANGE);
		Token<Type> end = current();
		consume(end.type);
		if (isType(Type.RANGE)) {
			consume(Type.RANGE);
			increment = current();
			consume(Type.NUMBER);
		}
		consume(Type.EXPR_END);
		postscript = expression();
		return (start.type == Type.CHAR) ?
				new CharRangeExpression(preamble, start, end, increment, postscript):
				new RangeExpression(preamble, start, end, increment, postscript);
	}
	
	// csvExpr:	expr [',' expr]+ '}' expr
	private Expression csvExpression(Expression preamble) throws ParsingException {
		Expression postscript = new Text(new Token<>(Type.TEXT, ""));
		List<Expression> expressions = new ArrayList<>();
		expressions.add(expression());
		while (isType(Type.COMMA)) {
			consume(Type.COMMA);
			expressions.add(expression());
		}
		consume(Type.EXPR_END);
		postscript = expression();
		return new CSVExpression(preamble, expressions, postscript);
	}
	
	/*  expr:		word
	 * 				| word? '{' rangeExpr
	 *  			| word? '{' csvExpr
	 */
	private Expression expression() throws ParsingException {
		Expression preamble = new Text(new Token<>(Type.TEXT, ""));
		if (isType(Type.TEXT, Type.CHAR, Type.NUMBER))
			preamble = word();
		if (!isType(Type.EXPR_START))
			return preamble;
		consume(Type.EXPR_START);
		save();	// save to backtrack in case it wasn't a range expression
		if (isType(Type.NUMBER, Type.CHAR)) {
			try { return rangeExpression(preamble); }
			catch (ParsingException e) { backtrack(); }
		} return csvExpression(preamble);
	}
	
	public Expression parse() throws ParsingException {
		return expression();
	}
	
	public List<String> parseResults() throws ParsingException {
		return parse().evaluate();
	}
	
	public String parseResultsString() throws ParsingException {
		StringBuilder sb = new StringBuilder();
		for (String token : parseResults())
			sb.append(token).append(" ");
		return sb.toString();
	}
}