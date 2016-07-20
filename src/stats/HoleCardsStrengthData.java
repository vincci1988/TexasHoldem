package stats;

import java.util.ArrayList;
import java.util.Collections;

public class HoleCardsStrengthData extends ArrayList<HoleCardsStrengthDatum> {

	void update(String holeCards, float rank) {
		if (contains(holeCards)) 
			get(indexOf(holeCards)).avgRank += rank;
		else {
			add(new HoleCardsStrengthDatum(holeCards));
			get(size() - 1).avgRank = rank;
		}
	}
	
	void finalize(int cnt) {
		for (int i = 0; i < size(); i++)
			get(i).avgRank /= cnt;
		Collections.sort(this);
	}
	
	private boolean contains(String holeCards) {
		for (int i = 0; i < size(); i++) {
			if (get(i).holeCards.equals(holeCards)) return true;
		}
		return false;
	}
	
	private int indexOf(String holeCards) {
		for (int i = 0; i < size(); i++) {
			if (get(i).holeCards.equals(holeCards)) return i;
		}
		return -1;
	}
	
	private static final long serialVersionUID = 1L;
}
