package model.command;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Executable extends Callable<Integer> {
	final int SUCCESS = 0, THREAD_POOL_SIZE = 10;
	final String WINDOWS_NATIVE = "cmd.exe /C", LINUX_NATIVE = "/bin/bash -c";
	final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	
	static String getNativeInterpreter() {
		return (File.separatorChar == '\\') ? WINDOWS_NATIVE : LINUX_NATIVE;	// cause '\' = windows
	}
	
	static String[] wrapNativeInterpreter(String... cmd) {
		String[] interpreterList = getNativeInterpreter().split(" ");
		String[] c = new String[interpreterList.length + cmd.length];
		System.arraycopy(interpreterList, 0, c, 0, interpreterList.length);
		System.arraycopy(cmd, 0, c, interpreterList.length, cmd.length);
		return c;
	}
}
