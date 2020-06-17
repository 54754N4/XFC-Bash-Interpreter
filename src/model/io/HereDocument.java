package model.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HereDocument extends InputStream {
	private byte[] bytes;
	private int current;
	
	public HereDocument(String input) {
		bytes = input.getBytes(StandardCharsets.UTF_8);
		current = 0;
	}
	
	@Override
	public int read() throws IOException {
		return (current < bytes.length) ? bytes[current++] : -1;
	}

}
