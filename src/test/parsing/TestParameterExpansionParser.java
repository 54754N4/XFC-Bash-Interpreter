package test.parsing;

import interpreter.parameter.ParameterParser;
import model.ConsoleContext;

public class TestParameterExpansionParser {

	public static void main(String[] args) {
		ConsoleContext context = ConsoleContext.INSTANCE;
		context.set("var0", "i am a value");
		context.set("var1", "fuck this");
		context.set("var2", "I AM UPPERCASE");
		context.set("var3", "var1");
		context.set("var4", "");
		context.set("file", "/home/bin/bash/source.txt");
		context.set("string", "Be liberal in what you accept, and conservative in what you send");
		String[] inputs = {
			"echo $var0 ${var4:-default} ${var5-short default} ${var1} ${var1^^} ${var2~~} ${!var3} ${#var3}",
			"echo I love trolling ${var4:=default} ${var5=short default}",
			"echo ${var3:+alternative} ${var4+short default}",
			"echo ${var3:?not set} ${var4?empty} ${!var*}",
			"${file##*\\.} ${file##*/} ${file%/*} ${file%\\..*}", // prints format, filename, directory & everything except .format
			"${string//in/with}",	// replace all "in" with "with"
			"${string:35:13} ${string:35}"
		}; 
		for (String input : inputs)
			System.out.println(new ParameterParser(input, context).parse());
	}

}
