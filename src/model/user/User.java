package model.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.command.Executable;
import model.io.UserSerialiser;

public class User implements Serializable {
	private static final long serialVersionUID = 3850931405417490261L;

	public static final String DEFAULT_COMMAND_SHELL = Executable.getNativeInterpreter().split(" ")[0],
			DEFAULT_HOME_DIR = "/home/default", 
			DEFAULT_USERNAME = "default",
			DEFAULT_COMMENT = "Default account";
	
	public static final User DEFAULT_USER = new User(
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
	
	@Override
	public boolean equals(Object o) {
		return User.class.isInstance(o) ? equals(User.class.cast(o)) : false;
	}
	
	public boolean equals(User u) {
		return u.username.equals(username) && u.homeDirectory.equals(homeDirectory)
				&& u.comment.equals(comment) && u.commandShell.equals(commandShell);
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s:%s:%s", username, homeDirectory, comment, commandShell);
	}
	
	public File serialise() throws FileNotFoundException, IOException {
		return UserSerialiser.serialise(this);
	}
	
	public static List<User> retrieveAll() throws FileNotFoundException, ClassNotFoundException, IOException {
		String[] serialised = new File(UserSerialiser.LOCATION).list(new UserFilenameFilter());
		List<User> users = new ArrayList<>(serialised.length);
		for (int i=0; i<serialised.length; i++)
			users.add(UserSerialiser.deserialise(new File(UserSerialiser.LOCATION+"/"+serialised[i])));
		return users;
	}

	public static User retrieve(String username) {
		List<User> users;
		try { users = retrieveAll(); }
		catch (ClassNotFoundException | IOException e) { return null; }
		Optional<User> found = users.stream()
				.filter(u -> u.getUsername().equals(username))
				.findFirst();
		return (found.isPresent()) ? found.get() : null;
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
