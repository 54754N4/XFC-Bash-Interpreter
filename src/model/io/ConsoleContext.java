package model.io;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import model.user.User;

public class ConsoleContext {
	public static final ConsoleContext INSTANCE = new ConsoleContext(User.DEFAULT_ACCOUNT);
	public static final String GLOBAL_KEY = "--";
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
		environment = new HashMap<>();
		directoryStack = new DirectoryStack();
		updatePWD(user.getHomeDirectory());
		ShellVariable.HOME.value = user.getHomeDirectory();
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
	
	public void pushd(String directory) {
		directoryStack.push(ShellVariable.PWD.value);
		updatePWD(directory);
		updateDIRSTACK();
	}
	
	public String popd() {
		String popped = directoryStack.pop();
		updatePWD(popped);
		updateDIRSTACK();
		return popped;
	}
	
	public String getHomeDirectory() {
		return user.getHomeDirectory();
	}
	
	public String get(String key) {
		ShellVariable var = matchBuiltin(key);
		return (var != null) ? var.value : environment.get(key);
	}
	
	public String set(String key, String value) {
		return environment.put(key, value);
	}
	
	public void updatePWD(String dir) {
		ShellVariable.OLDPWD.value = ShellVariable.PWD.value;
		ShellVariable.PWD.value = dir;
	}
	
	public void updateDIRSTACK() {
		StringBuilder sb = new StringBuilder();
		for (String dir : directoryStack.toArray(new String[0]))
			sb.append(dir).append(" ");
		ShellVariable.DIRSTACK.value = sb.toString().trim();
	}
	
	public static ShellVariable matchBuiltin(String var) {
		for (ShellVariable variable : ShellVariable.values())
			if (variable.name.equals(var))
				return variable;
		return null;
	}
	
	public enum ShellVariable {
		HOME, DIRSTACK, PWD, OLDPWD;
		
		public final String name;
		private String value;
		
		private ShellVariable() {
			this.name = name();
			this.value = "";
		}
		
		private ShellVariable(String name) {
			this(name, "");
		}
		
		private ShellVariable(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		public String setValue(String value) {
			return this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
}
