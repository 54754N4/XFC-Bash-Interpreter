package test.parsing;

import java.util.Arrays;

import interpreter.util.StrUtil;

@SuppressWarnings("unused")
public class TestRegex {

	public static void main(String[] args) {
//		String[] strs = {"$var0", "${var1^}", "${var//pattern/match}"};
//		for (String str : strs)
//			System.out.printf("%s matches: %b%n", str, str.matches(".*[\\Q:^,~*@#%/-+=?\\E].*"));

//		String input = "/home/bash/bash_hackers.txt";
//		System.out.println(StrUtil.removeSmallestEnd("\\..*", input));

		String input = "/home/bash\\/bash_hackers.txt";
		System.out.println(Arrays.toString(input.split("(?<!\\\\)/"))); // checks that it's NOT escaped
		// using a regex lookahead (zero-length assertion)
	}

}
