package model.user;

import java.io.File;
import java.io.FilenameFilter;

public class UserFilenameFilter implements FilenameFilter {
	@Override
	public boolean accept(File file, String name) {
		return name.toLowerCase().endsWith(".user");
	}
}