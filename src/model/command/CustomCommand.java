package model.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.stream.Stream;

import model.ConsoleContext;
import model.io.StreamTransfer;

public abstract class CustomCommand implements RedirectableExecutable {
	protected ConsoleContext context;
	protected final String[] parameters;	// akin to bash's positional parameters
	private int exitCode;
	private boolean merge;
	private InputStream innerInput, outerOutput, outerError, redirectedInput;
	private OutputStream outerInput, innerOutput, innerError, redirectedOutput, redirectedError;
	private PrintWriter outWriter, errWriter;
	protected BufferedReader reader;
	
	protected CustomCommand(ConsoleContext context, String[] parameters) throws IOException {
		this.parameters = parameters;
		exitCode = Executable.SUCCESS;
		merge = false;
		innerInput = new PipedInputStream();
		outerOutput = new PipedInputStream();
		outerError = new PipedInputStream();
		outerInput = new PipedOutputStream((PipedInputStream) innerInput);
		innerOutput = new PipedOutputStream((PipedInputStream) outerOutput);
		innerError = new PipedOutputStream((PipedInputStream) outerError);
	}
	
	@Override
	public Integer call() throws Exception {
		prepareIO();
		preExecute();
		execute();
		postExecute();
		finaliseIO();
		return exitCode;
	}
	
	private final void prepareIO() {
		if (redirectedInput != null)
			StreamTransfer.transfer(redirectedInput, stdin());
		reader = new BufferedReader(new InputStreamReader(innerInput));
		outWriter = new PrintWriter(innerOutput);
		if (!merge) errWriter = new PrintWriter(innerError);
	}
	
	private final void finaliseIO() throws IOException {
		if (redirectedOutput != null)
			StreamTransfer.transfer(stdout(), redirectedOutput);
		if (redirectedError != null && !merge)
			StreamTransfer.transfer(stderr(), redirectedError);
		reader.close();
		outWriter.close();
		if (!merge) errWriter.close();
	}
	
	@Override
	public void mergeSTDOUT() {
		merge = true;
	}
	
	@Override
	public OutputStream stdin() {
		return outerInput;
	}

	@Override
	public InputStream stdout() {
		return outerOutput;
	}

	@Override
	public InputStream stderr() {
		return outerError;
	}

	@Override
	public void stdin(InputStream stream) {
		redirectedInput = stream;
	}

	@Override
	public void stdout(OutputStream stream) {
		redirectedOutput = stream;
	}

	@Override
	public void stderr(OutputStream stream) {
		redirectedError = stream;
	}
	
	protected abstract void execute() throws Exception;
	protected void preExecute() {}
	protected void postExecute() {}
	
	protected boolean hasParameter(int index) {
		return index < parameters.length;
	}
	
	protected String getParameter(int index) {
		return (hasParameter(index)) ? parameters[index] : "";
	}
	
	protected void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}
	
	public int getExitCode() {
		return exitCode;
	}
	
	protected Stream<String> getLinesStream() {
		return reader.lines();
	}
	
	protected CustomCommand print(String format, Object...args) {
		outWriter.print(String.format(format, args));
		return this;
	}
	
	protected CustomCommand println(String format, Object...args) {
		return print(format+System.lineSeparator(), args);
	}
	
	protected CustomCommand errorPrint(String format, Object...args) {
		if (merge) 
			return print(format, args);
		errWriter.print(String.format(format, args));
		return this;
	}
	
	protected CustomCommand errorPrintln(String format, Object...args) {
		if (merge) 
			return println(format, args);
		return errorPrint(format+System.lineSeparator(), args);
	}
}
