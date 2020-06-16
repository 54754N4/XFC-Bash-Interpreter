package test.context;

import model.io.ConsoleContext;

import model.user.User;

public class TestContextPushd {
	public static void main(String[] args) {
		ConsoleContext context = new ConsoleContext(User.DEFAULT_ACCOUNT);
		System.out.println(context.get("PWD"));			// /home/default
		context.pushd("/bin/bash/source/");
		System.out.println(context.get("PWD"));			// /bin/bash/source/
		System.out.println(context.get("OLDPWD"));		// /home/default
		System.out.println(context.get("DIRSTACK"));	// /home/default
		context.popd();
		System.out.println(context.get("PWD"));			// /home/default
		System.out.println(context.get("OLDPWD"));		// /bin/bash/source/
		System.out.println(context.get("DIRSTACK"));	// *nothing cause popped*
	}
}
