package interpreter.arithmetic;

import java.util.Scanner;

import interpreter.exceptions.ParsingException;

public class Test {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String input;
		while (true) {
			System.out.print("calc> ");
			input = sc.nextLine();
			if (input.toLowerCase().equals("exit"))
				break;
			try {
				System.out.println(new Interpreter(input).interpret());
			} catch (ParsingException e) {
				System.err.println(e.getMessage());
			}
		}
		sc.close();
	}
}
