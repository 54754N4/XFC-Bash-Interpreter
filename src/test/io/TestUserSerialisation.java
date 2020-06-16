package test.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import model.user.User;

public class TestUserSerialisation {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		User user = User.DEFAULT_ACCOUNT;
		System.out.println(user);
		File exported = User.serialise(user);
		System.out.println(exported);
		User extracted = User.deserialise(exported);
		System.out.println(extracted);
		System.out.println(extracted.equals(user));
	}
}
