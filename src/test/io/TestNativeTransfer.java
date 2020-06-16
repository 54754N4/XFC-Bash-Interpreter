package test.io;

import java.io.IOException;

import model.io.StreamTransfer;

public class TestNativeTransfer {
	private static ProcessBuilder builder = new ProcessBuilder();
	
	private static Process launch(String...cmd) throws IOException {
		return builder.command(cmd).start();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Process echo = launch("ls"),
				cat = launch("cat");
		StreamTransfer.transfer(echo.getInputStream(), cat.getOutputStream());
		StreamTransfer.transfer(cat.getInputStream(), System.out);
	}
}

