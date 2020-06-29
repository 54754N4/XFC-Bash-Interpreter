package interpreter.bool;

import interpreter.bool.ast.AST;
import interpreter.bool.ast.BinaryOperator;
import interpreter.bool.ast.Constant;
import interpreter.bool.ast.UnaryOperator;
import interpreter.exceptions.ParsingException;
import interpreter.generic.IterativeParser;
import interpreter.generic.Lexer;
import interpreter.generic.Token;

/** 
 * >> Notes
 * - Extra production rule is needed to handle AND, OR separately from other binary ops
 * - All ops have same precendence relative to other ops with the same operand count
 * - Unary ops have higher precendence
 * - stacking/nesting unary ops doesn't make sense (in our case, but grammar allows it) 
 * 
 * >Grammar
 * conjunction:	binary [(AND|OR|AND_NOT) binary]* 
 * binary:		unary [BINARY_OP unary]*
 * unary:		UNARY_OP? unary
 * 				| factor
 * factor:		constant
 * 				| LEFT_PAREN conjunction RIGHT_PAREN
 * 				| conjunction
 * constant: 	true|false|number|string
 */
//Verify: https://docs.oracle.com/javase/tutorial/essential/io/fileAttr.html
/* >> References + Nice reads
 * Boolean Grammars - https://pdf.sciencedirectassets.com/272575/1-s2.0-S0890540100X02489/1-s2.0-S0890540104001075/main.pdf?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEHIaCXVzLWVhc3QtMSJGMEQCIE7J4GOpuBAdpMWEnt6U6cBouCImjEHl5BBW9IdOlyW7AiB1Qq6PpGZ%2FsL1t%2F2n2Cyvun3XOVAqABrWagG4c%2F%2F0n8iq9Awj6%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAMaDDA1OTAwMzU0Njg2NSIMFS4GyfGWEgplti4cKpEDCJxOWknxK1ZuFT%2Fs%2FwH%2BYR%2Bw0RLp6%2FOvfm16W%2FwEkE5uhG6ynoUs%2Bips2w2njtBBh10guMCL6OoPyV6nzMZexTkmKkpShxTky9GJzVBBYN8LNoX4IJEPgALuW8u5gqlb6cRwldbzpMqjtmq7NvtM3c2X95jFFxKX3ZArJ94HAMOtL6QgA6l4r3sB13vU2GhbHCkhwsSeDkR3yR1usP1XNTFfSuLKhE29LEM2C2i1BAajJ1%2Fgdlsy8rBDRuA%2BpOoL0LjLSAmEXgx%2Fnkeju7wYOwAvGvS0FwM2Stfagfwn0zC5yw6G0FSKDN%2BUIbnEylPFctXNKMnm1xH%2F8MRrqCuTdlhhwUO66weELedvfHe%2FlzGID3OE03dpUTtvvMHj34gCkZZ63%2BVD9BK9419o%2FrLaldT63XUh7Az5D5dTanWWn3iitavxUIq4wgyPNfn33b1vAQZeER5NnbCkLXhjiHzIxzuRaBsy46lj2niUnEeiEj85TDraDCvswL8g%2BZ9KGGRbhr27WupRJQv3LfpSLPGHTS4wrIDl9wU67AGLKyS4Fux58SrlUa1LDbrG62G9AAeNyR2ZfQ0qBTSddJXbIxTa8MRRsjdcbBg%2Bd8gXdcThYnvvyU%2F%2BX%2BxnKSewNdOjs91sfXOTZ68KSjcZ%2F5PfTSDRtcy3F8luNvatUyHcQqL8zKCfaAJhg6YM9hKZUCqiGIu272l7o99dszThrsCOREnZcGM%2BnGfOYA4b1aACUPPf7li7gdW%2FIetgmIJTzx2L1By0XlFLac1itzLGmjomBrrTxGg7B5Ja8%2BeoT0%2BeOVx4XHJ6jwlSnuGkrXuBqg2DxEFzxtjPl9fEV2AqYLYmgBkEJPTUlzoydg%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20200629T015244Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAQ3PHCVTYUNF7MVVD%2F20200629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=565091febd5ed0e109f770187dccb7924fbe912f653b17806bf22776d86de9d0&hash=6ec06c7ad21e2dde9b97758313891fd3b667601effbd058c7f80c3e6e5b1cd2a&host=68042c943591013ac2b2430a89b270f6af2c76d8dfd086a07176afe7c76c2c61&pii=S0890540104001075&tid=spdf-09145b46-b40a-482e-9d20-f8a6339dd022&sid=2c48fcac869504434b6bafc5d17461fa842egxrqb&type=client
 * Pratt parsing - https://web.archive.org/web/20151223215421/http://hall.org.ua/halls/wizzard/pdf/Vaughan.Pratt.TDOP.pdf
 */
public class BooleanParser extends IterativeParser<Type, AST> {
	
	public BooleanParser(Lexer<Type> lexer) throws ParsingException {
		super(lexer);
	}
	
	// constant: 	true|false|number|string
	private boolean isConstant() {
		return is(new Type[] {Type.CONSTANT, Type.NUMBER, Type.STRING});
	}
	
	/* factor:		constant
	 * 				| LEFT_PAREN conjunction RIGHT_PAREN
	 * 				| conjunction
	 */
	private AST factor() throws ParsingException {
		Token<Type> token;
		if (isConstant()) {
			token = current;
			consume(token.type);
			return new Constant(token);
		} else if (is(Type.LEFT_PAREN)) {
			consume(Type.LEFT_PAREN);
			AST ast = conjunction();
			consume(Type.RIGHT_PAREN);
			return ast;
		} return conjunction();
	}
	
	/* unary:		UNARY_OP? unary
	 * 				| factor
	 */
	private AST unary() throws ParsingException {
		if (current.type.isUnary()) {
			Token<Type> token = current;
			consume(token.type);
			return new UnaryOperator(token, unary());
		} 
		return factor();
	}
	
	// binary:		unary [BINARY_OP unary]*
	private AST binary() throws ParsingException {
		AST unary = unary();
		Token<Type> token;
		while (current.type.isBinary() && !(is(Type.AND) || is(Type.OR) || is(Type.AND_NOT))) {
			token = current;
			consume(token.type);
			unary = new BinaryOperator(unary, token, unary());
		}
		return unary;
	}
	
	// conjunction:	binary [(AND|OR|AND_NOT) binary]*
	private AST conjunction() throws ParsingException {
		AST binary = binary();
		Token<Type> token;
		while (is(Type.AND) || is(Type.OR) || is(Type.AND_NOT)) {
			token = current;
			consume(token.type);
			binary = new BinaryOperator(binary, token, binary());
		}
		return binary;
	}
	
	@Override
	public AST parse() throws ParsingException {
		return conjunction();
	}
}
