package model.io;

import java.util.ArrayList;
import java.util.Arrays;

// Top of the stack starts at index 0
public class DirectoryStack extends ArrayList<String> {
	private static final long serialVersionUID = -6371502326395607903L;

	public void push(Object path) {
		add(0, path.toString());
	}
	
	public String pop() {
		return remove(0);
	}
	
	public String fromLeft(int n) {		// dirs +N
		return get(n);
	}
	
	public String fromRight(int n) {	// dirs -N
		return get(size()-(n+1));
	}
	
	public DirectoryStack shiftLeft(int n) {	// pushd +i
		String first;
		int j;
		for(int i = size()-1; i > size()-1-n; i--) { 	// towards the left (n times) 
            first = get(0);    							// track first element
            for(j = 0; j < size()-1; j++)  				// shift all by 1
                set(j, get(j+1));						// previous = current
            set(size()-1, first);    					// first is new tail
        }    
		return this;
	}
	
	public DirectoryStack shiftRight(int n) {	// pushd -i
		String last;
		int j;
		for(int i = 0; i < n; i++) {       	// towards the right (n times)
            last = get(size()-1);    		// track last element
            for(j = size()-1; j > 0; j--)   // shift all by 1
                set(j, get(j-1));    		// current = previous
            set(0, last);    				// last is new head
        }    
		return this;
	}
	
	@Override
	public DirectoryStack clone() {
		DirectoryStack stack = new DirectoryStack();
		for (String elem : this) stack.add(elem);
		return stack;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}
}
