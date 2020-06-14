package test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Test {
	
	private static String create() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<100; i++)
			sb.append(i+".123401234").append(System.lineSeparator());
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		ByteArrayInputStream initial_in = new ByteArrayInputStream(create().getBytes(StandardCharsets.UTF_8));
		PipedInputStream in0 = new PipedInputStream();								
		PipedOutputStream out0 = new PipedOutputStream(in0);						 
		NativeToNativeTransfer ntnt = new NativeToNativeTransfer(initial_in, out0);
		Future<Void> f1 = executor.submit(ntnt);
		StringBuilder sb0 = new StringBuilder();
		NativeToCustomTransfer ntct = new NativeToCustomTransfer(in0, sb0);
		Future<Void> f2 = executor.submit(ntct);
		StringBuilder sb1 = new StringBuilder();
		CustomToCustomTransfer ctct = new CustomToCustomTransfer(sb0, sb1);
		Future<Void> f3 = executor.submit(ctct);
		PipedInputStream in1 = new PipedInputStream();
		PipedOutputStream out1 = new PipedOutputStream(in1);
		CustomToNativeTransfer ctnt = new CustomToNativeTransfer(sb1, out1);
		Future<Void> f4 = executor.submit(ctnt);
		StringBuilder final_sb = new StringBuilder();
		NativeToCustomTransfer ntctf = new NativeToCustomTransfer(in1, final_sb);
		Future<Void> f5 = executor.submit(ntctf);
		f1.get();
		f2.get();
		f3.get();
//		System.out.println(sb1);	// this works
		f4.get();
		f5.get();
		System.out.println(final_sb); // but not this ?
		executor.shutdown();
		System.out.println("Done");
	}
	
	public static boolean isClosed(InputStream in, OutputStream out) {
		try {
			if (in == null) out.write(0);
			else in.read();
			return false;
		} catch (IOException e) { 
			System.out.println(e.getMessage());
			return true;	
		}
	}
	
	public static class NativeToCustomTransfer implements Callable<Void> {
		private InputStream in;
		private StringBuilder out;
		
		public NativeToCustomTransfer(InputStream in, StringBuilder out) {
			this.in = in;
			this.out = out;
		}
		
		@Override
		public Void call() throws Exception {
			byte[] buffer = new byte[StreamTransfer.BUFFER_SIZE];
			int read = 0;
			while ((read = in.read(buffer)) != -1) 
				out.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
			in.close();
			return null;
		}
	}
	
	public static class CustomToNativeTransfer extends StreamTransfer {	
		public CustomToNativeTransfer(StringBuilder in, OutputStream out) {
			super(new ByteArrayInputStream(in.toString().getBytes(StandardCharsets.UTF_8)), out);
		}
	}
	
	public static class NativeToNativeTransfer extends StreamTransfer {
		public NativeToNativeTransfer(InputStream in, OutputStream out) {
			super(in, out);
		}
	}
	
	public static class CustomToCustomTransfer implements Callable<Void> {
		private StringBuilder in, out;
		
		public CustomToCustomTransfer(StringBuilder in, StringBuilder out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public Void call() throws Exception {
			in.chars().forEach(c -> out.append((char) c));
			return null;
		}
	}
	
	public static class StreamTransfer implements Callable<Void> {
		public static final int BUFFER_SIZE = 1024;
		private InputStream in;
		private OutputStream out;
		
		public StreamTransfer(InputStream in, OutputStream out) {
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
}
