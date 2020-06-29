package interpreter.bool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import interpreter.arithmetic.ArithmeticInterpreter;
import interpreter.bool.ast.AST;
import interpreter.bool.ast.BinaryOperator;
import interpreter.bool.ast.Constant;
import interpreter.bool.ast.UnaryOperator;
import interpreter.bool.ast.Visitor;
import interpreter.exceptions.InterpretingException;
import interpreter.exceptions.ParsingException;
import model.ConsoleContext;

public class BooleanInterpreter implements Visitor {
	private BooleanParser parser;
	private ConsoleContext context; 
	
	public BooleanInterpreter(BooleanParser parser, ConsoleContext context) {
		this.parser = parser;
		this.context = context;
	}
	
	public boolean interpret() throws InterpretingException, ParsingException {
		return visit(parser.parse());
	}
	
	@Override
	public boolean visit(UnaryOperator op) throws InterpretingException {
		// for boolean ops
		if (op.token.type == Type.NOT)
			return !visit(op.node);
		// for strings
		String value = value(op.node);
		if (op.token.type == Type.IS_ZERO)		
			return value.length() == 0;
		else if (op.token.type == Type.IS_NON_ZERO)
			return value.length() != 0;
		// for files
		else if (op.token.type == Type.EXISTS)
			return file(value).exists();
		else if (op.token.type == Type.BLOCK_SPECIAL)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.CHAR_SPECIAL)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.DIRECTORY)
			return file(value).isDirectory();
		else if (op.token.type == Type.REGULAR)
			return file(value).isFile();
		else if (op.token.type == Type.SYMBOLIC_LINK_1 || op.token.type == Type.SYMBOLIC_LINK_2) {
			return attr(value).isSymbolicLink();
		} else if (op.token.type == Type.STICKY_BIT_SET)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.NAMED_PIPE)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.READABLE)
			return file(value).canRead();
		else if (op.token.type == Type.NON_ZERO_SIZE)
			return file(value).length() != 0;
		else if (op.token.type == Type.TERMINAL)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.SET_UID_BIT_SET)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.WRITABLE)
			return file(value).canWrite();
		else if (op.token.type == Type.EXECUTABLE)
			return file(value).canExecute();
		else if (op.token.type == Type.GROUP_ID)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.MODIFIED_SINCE_READ) {
			BasicFileAttributes attr = attr(value); 
			return attr.lastAccessTime().to(TimeUnit.SECONDS) < attr.lastModifiedTime().to(TimeUnit.SECONDS);   
		} else if (op.token.type == Type.OWNED_BY_EUID)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.SOCKET)
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.IS_ENABLED) 
			throw new InterpretingException("Unimplemented yet");	// TODO
		else if (op.token.type == Type.IS_ASSIGNED) 
			return context.get(value) != null;
		else if (op.token.type == Type.IS_REFERENCE) {
			value = context.get(value); 
			return value != null && value.startsWith("$");
		} else throw new InterpretingException("Undefined token : "+op.token);
	}

	@Override
	public boolean visit(BinaryOperator op) throws InterpretingException {
		boolean first;
		if (op.token.type == Type.AND) {
			if (!(first = visit(op.left)))	// short-circuit on false
				return false;
			return first && visit(op.right);
		} else if (op.token.type == Type.OR) { 
			if (first = visit(op.left))		// short-circuit on true
				return true;
			return first || visit(op.right);	
		} else if (op.token.type == Type.AND_NOT) {
			if (!(first = visit(op.left)))	// short-circuit on false
				return false;
			return first && !visit(op.right);
		} 
		// Otherwise everything else requires string constants initially
		String left = Constant.class.cast(op.left).token.value, 
			right = Constant.class.cast(op.right).token.value;
//		// This should be handled by parameter expansion 
//		if (left.startsWith("$")) left = context.get(left.substring(1));
//		if (right.startsWith("$")) right = context.get(right.substring(1));
		switch (op.token.type) {
			case STR_EQUAL: return left.equals(right);
			case STR_EQUAL_POSIX: 
			case STR_MATCH: return left.matches(right);
			case STR_NOT_EQUAL: return !left.equals(right);
			case STR_LESS_THAN: return left.compareTo(right) < 0;
			case STR_GREATER_THAN: return left.compareTo(right) > 0;
			// args evaluated as files
			case FILE_EQUAL: return file(left).equals(file(right));
			case NEWER_THAN: 
				return attr(file(left)).creationTime().to(TimeUnit.SECONDS) 
						< attr(file(right)).creationTime().to(TimeUnit.SECONDS);
			case OLDER_THAN:
				return attr(file(left)).creationTime().to(TimeUnit.SECONDS) 
						> attr(file(right)).creationTime().to(TimeUnit.SECONDS);
			// args evaluated as arithmetic expressions
			case EQUAL: return arithmetic(left, right, (l,r) -> l.equals(r));	// if you use == it wont work cause Double will be pointers
			case NOT_EQUAL: return arithmetic(left, right, (l,r) -> !l.equals(r));
			case LESS_THAN: return arithmetic(left, right, (l,r) -> l < r);
			case LESS_EQUAL: return arithmetic(left, right, (l,r) -> l <= r);
			case GREATER_THAN: return arithmetic(left, right, (l,r) -> l > r);
			case GREATER_EQUAL: return arithmetic(left, right, (l,r) -> l >= r);
			default: throw new InterpretingException();		
		}		
	}
	
	/*
	 * Helper/convenience methods
	 */
	
	private static String value(AST node) {
		return Constant.class.cast(node).token.value;
	}
	
	private static File file(String name) {
		return new File(name);
	}
	
	private static BasicFileAttributes attr(File file) throws InterpretingException {
		try {
			return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		} catch (IOException e) {
			throw new InterpretingException("Can't read file attributes :"+file.getName());
		}
	}
	
	private static BasicFileAttributes attr(String name) throws InterpretingException {
		try { return attr(file(name)); } 
		catch (IOException e) {
			throw new InterpretingException("Can't read file attributes :"+name);
		}
	}
	
//	Handle weird posix cases if u can 				// TODO
//	private static PosixFileAttributes attrp(AST node) throws InterpretingException {
//		try {
//			return Files.readAttributes(file(value(node)).toPath(), PosixFileAttributes.class);
//		} catch (IOException e) {
//			throw new InterpretingException("Can't read file attributes :"+node);
//		}
//	}
	
	private static ArithmeticInterpreter arithmetic(String input) {
		return new ArithmeticInterpreter(input);
	}
	
	private static boolean arithmetic(String left, String right, 
			BiFunction<Double, Double, Boolean> consumer) throws InterpretingException {
		try {
			Double l = arithmetic(left).interpret(), 
				r = arithmetic(right).interpret();
			return consumer.apply(l, r);
		} catch (ParsingException e) {
			throw new InterpretingException(e.getLocalizedMessage());
		}
	}
}
