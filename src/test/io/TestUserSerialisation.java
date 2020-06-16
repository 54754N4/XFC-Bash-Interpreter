package test.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import model.io.UserSerialiser;
import model.user.User;

public class TestUserSerialisation {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		User user = User.DEFAULT_USER;
		System.out.println(user);
		File exported = UserSerialiser.serialise(user);
		System.out.println(exported);
		User extracted = UserSerialiser.deserialise(exported);
		System.out.println(extracted);
		System.out.println(extracted.equals(user));
		List<User> users = User.retrieveAll();
		System.out.println(Arrays.toString(users.toArray()));
		User d = User.retrieve(User.DEFAULT_USER.getUsername());
		System.out.println(d);
	}
}
