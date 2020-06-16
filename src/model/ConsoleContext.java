package model;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.user.User;

public class ConsoleContext {
	public static final ConsoleContext INSTANCE = new ConsoleContext(User.DEFAULT_USER);
	public static final String GLOBAL_KEY = "--", HOME = "HOME", 
			PWD = "PWD", OLDPWD = "OLDPWD", DIRSTACK = "DIRSTACK";
	private User user;	// current profile
	private InputStream in;
	private PrintStream out, err;
	private DirectoryStack directoryStack;
	private Map<String, String> environment;
	
	public ConsoleContext(User user) {
		this(user, System.in, System.out, System.err);
	}
	
	public ConsoleContext(User user, InputStream in, PrintStream out) {
		this(user, in, out, null);
	}
	
	public ConsoleContext(User user, InputStream in, PrintStream out, PrintStream err) {
		this.in = in;
		this.out = out;
		this.err = err;
		this.user = user;
		environment = new ConcurrentHashMap<>();
		directoryStack = new DirectoryStack();
		changeDirectory(user.getHomeDirectory());
		environment.put(HOME, user.getHomeDirectory());
	}
	
	public void pushd(String directory) {
		directoryStack.push(environment.get(PWD));
		changeDirectory(directory);
		updateDIRSTACK();
	}
	
	public String popd() {
		String popped = directoryStack.pop();
		changeDirectory(popped);
		updateDIRSTACK();
		return popped;
	}
	
	public String getHomeDirectory() {
		return user.getHomeDirectory();
	}
	
	public String get(String key) {
		return environment.get(key);
	}
	
	public String set(String key, String value) {
		return environment.put(key, value);
	}
	
	public void changeDirectory(String dir) {
		String pwd = environment.get(PWD);
		if (pwd != null)
			environment.put(OLDPWD, pwd);
		environment.put(PWD, dir);
	}
	
	public void updateDIRSTACK() {
		StringBuilder sb = new StringBuilder();
		for (String dir : directoryStack.toArray(new String[0]))
			sb.append(dir).append(" ");
		environment.put(DIRSTACK, sb.toString().trim());
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
	
	public DirectoryStack getDirectoryStack() {
		return directoryStack;
	}
	
}