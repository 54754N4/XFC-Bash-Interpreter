package test.io.command;

import java.io.IOException;

import model.ConsoleContext;
import model.command.CustomCommand;
import model.command.Executable;
import model.command.NativeCommand;

public class TestMerged {
	public static void main(String[] args) throws InterruptedException, IOException {
		CustomCommand c0 = new Spit(),
				c1 = new Cat();
		NativeCommand n2 = new NativeCommand("grep 8");
//		c0.stdout(c1.stdin());
		c1.stdin(c0.stdout());	// equivalent to above
		c1.stdout(n2.stdin());
		n2.stdout(System.out);
		Executable.THREAD_POOL.submit(c0);
		Executable.THREAD_POOL.submit(c1);
		Executable.THREAD_POOL.submit(n2);
		Thread.sleep(1000);
		Executable.THREAD_POOL.shutdown();
	}
	
	static class Spit extends CustomCommand {
		protected Spit() throws IOException {
			super(ConsoleContext.INSTANCE, null);
		}

		@Override
		protected void execute() throws Exception {
			for (int i=0; i<10; i++)
				println("%d.123409534562", i);
		}
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