package holdem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Deck extends ArrayList<Card> {

	public Deck() throws Exception {
		for (int code = 0; code < 52; code++) 
			this.add(new Card(code));
		iterator = this.iterator();
	}
	
	public void shuffle() {
		Collections.shuffle(this);
		reset();
	}
	
	public void reset() {
		iterator = this.iterator();
	}
	
	public void sort() {
		Collections.sort(this);
		iterator = this.iterator();
	}
	
	public Card draw() {
		if (iterator.hasNext())
			return iterator.next();
		return null;
	}
	
	public void removeByCode(int code) throws Exception {
		Card toRemove = new Card(code);
		remove(toRemove);
	}
	
	public String toString() {
		String result = new String();
		for (int i = 0; i < this.size(); i++) 
			result += (this.get(i) + " ");
		return result;
	}

	private static final long serialVersionUID = 1L;
	Iterator<Card> iterator;
	
	public static final int size = 52;
}
