package model.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.user.User;

public class UserSerialiser {
	public static final String LOCATION = "users";

	public static File serialise(User user) throws FileNotFoundException, IOException {
		File file = new File(LOCATION+"/"+user.getUsername()+".user");
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
			return User.class.cast(ois.readObject());
		}
	}
	
}
