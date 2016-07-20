package holdem;

import java.util.ArrayList;
import java.util.Collections;

public class Board extends ArrayList<Card> {
	
	public Board() {
		super();
	}
	
	public String toString() {
		Board sorted = new Board();
		sorted.addAll(this);
		Collections.sort(sorted);
		String result = new String();
		for (int i = 0; i < sorted.size(); i++) 
			result += sorted.get(i);
		return result;
	}
	
	public String display() {
		String result = new String();
		for (int i = 0; i < size(); i++) 
			result += get(i);
		return result;
	}
	
	public void sort() {
		Collections.sort(this);
	}
	
	private static final long serialVersionUID = 1L;
}
