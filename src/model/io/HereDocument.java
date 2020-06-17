package model.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// For when u wanna test multiple IO :
// https://linuxize.com/post/bash-heredoc/#:~:text=In%20Bash%20and%20other%20shells,of%20input%20to%20a%20command.&text=The%20first%20line%20starts%20with,%3C%3C%20and%20the%20delimiting%20identifier.
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
