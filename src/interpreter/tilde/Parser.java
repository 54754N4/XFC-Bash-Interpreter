package interpreter.tilde;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.ConsoleContext;
import model.user.User;

public class Parser {
	
	public String parse(String input) {
		String result = input, name, target, pwd, oldpwd, home;
		Matcher m = Pattern.compile("(~([a-z\" ]+))").matcher(result);
		if (m.matches()) {
			target = m.group(1);
			name = m.group(2);
			if (name.startsWith("\"") && name.endsWith("\""))
				name = name.substring(1, name.length()-1);
			User user = User.retrieve(name);
			if (user != null) 
				result = result.replace(target, user.getHomeDirectory());
		} if (result.contains("~+") && (pwd = ConsoleContext.INSTANCE.get(ConsoleContext.PWD)) != null) 
			result = result.replace("~+", pwd);
		if (result.contains("~-") && (oldpwd = ConsoleContext.INSTANCE.get(ConsoleContext.OLDPWD)) != null)
			result = result.replace("~-", oldpwd);
		if (result.contains("~") && (home = ConsoleContext.INSTANCE.get(ConsoleContext.HOME)) != null)
			result = result.replaceAll("~(?!(\\+|-|[a-zA-Z]))", home);
		return result;
	}
	
}
