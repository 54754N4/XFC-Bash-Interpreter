package model.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import model.io.StreamTransfer;

public class NativeCommand implements RedirectableExecutable {
	private ProcessBuilder builder;
	private Process process;
	// Store redirection streams
	private InputStream in;
	private OutputStream out, err;
	
	public NativeCommand(String...command) throws IOException {
		builder = new ProcessBuilder(Executable.wrapNativeInterpreter(command));	// 
		process = builder.start();
	}

	@Override
	public Integer call() throws Exception {
		if (in != null)		// write inputs to process
			StreamTransfer.transfer(in, stdin());
		if (out != null) 	// write output to output
			StreamTransfer.transfer(stdout(), out);
		if (err != null && !builder.redirectErrorStream())
			StreamTransfer.transfer(stderr(), err);
		return process.waitFor();
	}
	
	@Override
	public void mergeSTDOUT() {
		builder.redirectErrorStream(true); // merges out + err streams
	}
	
	@Override
	public OutputStream stdin() {
		return process.getOutputStream();
	}

	@Override
	public InputStream stdout() {
		return process.getInputStream();
	}

	@Override
	public InputStream stderr() {
		return process.getErrorStream();
	}
	
	@Override
	public void stdin(InputStream input) {
		this.in = input;
	}

	@Override
	public void stdout(OutputStream output) {
		this.out = output;
	}

	@Override
	public void stderr(OutputStream error) {
		this.err = error;
	}
}
