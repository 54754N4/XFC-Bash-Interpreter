package interpreter.brace.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CSVExpression extends Expression {
	public final List<Expression> atoms;
	public final Expression preamble, postscript;
	
	public CSVExpression(Expression preamble, Collection<Expression> atoms, Expression postscript) {
		this.atoms = new ArrayList<>(atoms);
		this.preamble = preamble;
		this.postscript = postscript;
	}

	@Override
	public List<String> evaluate() {
		List<String> results = new ArrayList<>();
		for (Expression atom: atoms)
			for (String token : atom.evaluate())
				for (String pre : preamble.evaluate())
					for (String post : postscript.evaluate())
						results.add(pre+token+post);
		return results;
	}
}