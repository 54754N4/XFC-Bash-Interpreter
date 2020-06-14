package model.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import model.command.Executable;

public class StreamTransfer implements Callable<Void> {
	public static final int BUFFER_SIZE = 1024;
	private InputStream in;
	private OutputStream out;
	
	public static StreamTransfer transfer(InputStream in, OutputStream out) {
		StreamTransfer transfer = new StreamTransfer(in, out);
		Executable.THREAD_POOL.submit(transfer);
		return transfer;
	}
	
	private StreamTransfer(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	@Override
	public Void call() throws Exception {
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = in.read(buffer)) != -1) 
			out.write(buffer, 0, read);
		in.close();
		out.close();
		return null;
	}
}