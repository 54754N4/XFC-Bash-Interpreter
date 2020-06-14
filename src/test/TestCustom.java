package test;

import java.io.IOException;

import model.command.CustomCommand;
import model.command.Executable;

public class TestCustom {
	public static void main(String[] args) throws InterruptedException, IOException {
		CustomCommand c0 = new Spit(),
				c1 = new Cat(),
				c2 = new Grep("7");
//		c0.stdout(c1.stdin());
		c1.stdin(c0.stdout());	// equivalent to above
		c1.stdout(c2.stdin());
		c2.stdout(System.out);
		Executable.THREAD_POOL.submit(c0);
		Executable.THREAD_POOL.submit(c1);
		Executable.THREAD_POOL.submit(c2);
		Thread.sleep(5000);
		Executable.THREAD_POOL.shutdown();
	}
	
	static class Spit extends CustomCommand {
		protected Spit() throws IOException {
			super(null);
		}

		@Override
		protected void execute() throws Exception {
			for (int i=0; i<10; i++)
				println("%d.123409534562", i);
		}
	}

	static class Cat extends CustomCommand {
		protected Cat() throws IOException {
			super(null);
		}
		
		@Override
		protected void execute() throws Exception {
			reader.lines().forEach(this::println);
		}
	}

	static class Grep extends CustomCommand {
		protected Grep(String...parameters) throws IOException {
			super(parameters);
		}
		
		@Override
		protected void execute() throws Exception {
			reader.lines()
				.filter(line -> line.contains(parameters[0]))
				.forEach(this::println);
		}
	}
}