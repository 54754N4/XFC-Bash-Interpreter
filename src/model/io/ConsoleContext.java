package model.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import model.user.User;

public class ConsoleContext {
	public static final ConsoleContext INSTANCE = new ConsoleContext();
	public static final String GLOBAL_KEY = "--";
	private User user;	// current profile
	private String home, pwd, oldpwd;
	private DirectoryStack directoryStack;
	private InputStream in;
	private PrintStream out, err;
	private Map<String, String> environment;
	
	private ConsoleContext() {
		this(User.DEFAULT_ACCOUNT, System.in, System.out, System.err);
	}
	
	public ConsoleContext(User user, InputStream in, PrintStream out, PrintStream err) {
		this.in = in;
		this.out = out;
		this.err = err;
		this.user = user;
		directoryStack = new DirectoryStack();
		environment = new HashMap<>();
	}
	
	public ConsoleContext(User user, InputStream in, PrintStream out) {
		this(user, in, out, null);
	}
	
	public User getCurrentUser() {
		return user;
	}
	
	public void setCurrentUser(User user) {
		this.user = user;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public PrintStream getOutputStream() {
		return out;
	}
	
	public PrintStream getErrorStream() {
		return err;
	}
	
	public Map<String, String> getEnvironment() {
		return environment;
	}
	
	public String pushd(String directory) {
		return directoryStack.push(directory);
	}
	
	public String popd() {
		return directoryStack.pop();
	}
	
	public String get(String key) {
		return environment.get(key);
	}
	
	public String set(String key, String value) {
		return environment.put(key, value);
	}
}
