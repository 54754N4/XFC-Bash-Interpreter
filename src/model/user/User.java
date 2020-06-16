package model.user;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import model.command.Executable;

public class User implements Serializable {
	private static final long serialVersionUID = -4472377149898201463L;

	public static final String DEFAULT_COMMAND_SHELL = Executable.getNativeInterpreter().split(" ")[0],
			DEFAULT_HOME_DIR = "/home/default", 
			DEFAULT_USERNAME = "default",
			DEFAULT_COMMENT = "Default account";
	
	public static final User DEFAULT_ACCOUNT = new User(
			DEFAULT_USERNAME, DEFAULT_COMMENT, 
			DEFAULT_HOME_DIR, DEFAULT_COMMAND_SHELL
	);
	
	private String username;
	private String comment; 		// extra info
	private String homeDirectory; 	// if doesn't exist, defaults to /, used for ~
	private String commandShell; 	// by default /bin/bash
	
	private User(String username, String comment, String homeDirectory,	String commandShell) {
		this.username = username;
		this.comment = comment;
		this.homeDirectory = homeDirectory;
		this.commandShell = commandShell;
	}
	
	public String getUsername() {
		return username;
	}

	public String getComment() {
		return comment;
	}

	public String getHomeDirectory() {
		return homeDirectory;
	}

	public String getCommandShell() {
		return commandShell;
	}
	
	public static File serialise(User user) throws FileNotFoundException, IOException {
		File file = new File(user.username+".user");
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(file)))) {
			oos.writeObject(user);
		}
		return file;
	}
	
	public static User deserialise(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream(file)))) {
			return (User) ois.readObject();
		}
	}
	
	public static User[] retrieve() throws FileNotFoundException, ClassNotFoundException, IOException {
		String[] serialised = new File("users").list(new UserFilenameFilter());
		User[] users = new User[serialised.length];
		for (int i=0; i<serialised.length; i++) 
			users[i] = deserialise(new File(serialised[i]));
		return users;
	}

	public static class UserFilenameFilter implements FilenameFilter {
		@Override
		public boolean accept(File file, String name) {
			return name.toLowerCase().endsWith(".user");
		}
	}
	
	public static class Builder {
		private String username;
		private String comment; 		// extra info
		private String homeDirectory; 	// if doesn't exist, defaults to /
		private String commandShell; 	// by default /bin/bash
	
		public Builder() {
			commandShell = DEFAULT_COMMAND_SHELL;
			homeDirectory = DEFAULT_HOME_DIR;
		}
		
		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}
		
		public Builder setComment(String comment) {
			this.comment = comment;
			return this;
		}
		
		public Builder setHomeDir(String homeDirectory) {
			this.homeDirectory = homeDirectory;
			return this;
		}
		
		public Builder setCommandShell(String commandShell) {
			this.commandShell = commandShell;
			return this;
		}
		
		public User build() {
			return new User(username, comment, homeDirectory, commandShell);
		}
	}
}
