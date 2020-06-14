package test;

import model.command.Executable;
import model.command.NativeCommand;

public class TestNative {
	public static void main(String[] args) throws Exception {
		NativeCommand n0 = new NativeCommand("ls"),
				n1 = new NativeCommand("cat"),
				n2 = new NativeCommand("grep src");
//		n1.stdin(n0.stdout());						// n1.in -> n0.out	(equivalent to line below)
		n0.stdout(n1.stdin()); 						// n0.out -> n1.in
		n1.stdout(n2.stdin());						// n1.out -> n2.in
		n2.stdout(System.out);						// n2.out -> System.out
		Executable.THREAD_POOL.submit(n0);
		Executable.THREAD_POOL.submit(n1);
		Executable.THREAD_POOL.submit(n2);
		Thread.sleep(2000);
		Executable.THREAD_POOL.shutdown();
	}
}
