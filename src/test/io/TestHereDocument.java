package test.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import model.ConsoleContext;
import model.command.CustomCommand;
import model.command.Executable;
import model.command.NativeCommand;
import model.io.HereDocument;

@SuppressWarnings("unused")
public class TestHereDocument {
	private final static HereDocument heredoc = new HereDocument(
			"The current working directory is: $PWD\n"
			+"You are logged in as: $(whoami)");
	
	private static void testHeredoc() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(heredoc))){
			String line;
			while ((line = reader.readLine()) != null)
				System.out.println(line);
		}
	}
	
	private static void testCustomIn() throws InterruptedException, IOException {
		CustomCommand cat = new Cat();
		cat.stdin(heredoc);
		cat.stdout(System.out);
		Executable.THREAD_POOL.submit(cat);
		Thread.sleep(1000);
		Executable.THREAD_POOL.shutdown();
	}
	
	private static void testNativeIn() throws InterruptedException, IOException {
		NativeCommand cat = new NativeCommand("cat");
		cat.stdin(heredoc);
		cat.stdout(System.out);
		Executable.THREAD_POOL.submit(cat);
		Thread.sleep(1000);
		Executable.THREAD_POOL.shutdown();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		testHeredoc();
//		testCustomIn();
		testNativeIn();
	}

	static class Cat extends CustomCommand {
		protected Cat() throws IOException {
			super(ConsoleContext.INSTANCE, null);
		}
		
		@Override
		protected void execute() throws Exception {
			reader.lines().forEach(this::println);
		}
	}
}
