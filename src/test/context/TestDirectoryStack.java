package test.context;

import model.io.DirectoryStack;

public class TestDirectoryStack {
	public static void main(String[] args) {
		DirectoryStack stack = new DirectoryStack();
		int[] dirs = {1,2,3,4,5,6};
		for (int dir : dirs) stack.push(dir);
		System.out.println(stack);
		for (int i=1; i<dirs.length; i++)
			System.out.println(stack.clone().shiftLeft(i));
		for (int i=1; i<dirs.length; i++)
			System.out.println(stack.clone().shiftRight(i));
	}
}
