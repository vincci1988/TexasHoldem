package holdem;
import java.util.ArrayList;

public class Hand extends ArrayList<Card>implements Comparable<Hand> {

	public Hand() {
		super();
		rank = -1;
		holeCards = null;
	}
	
	public int compareTo(Hand other) {
		if (this.rank != other.rank)
			return other.rank - this.rank;
		for (int i = 0; i < handSize; i++) {
			if (this.get(i).getRank() != other.get(i).getRank())
				return other.get(i).getNumericRank() - this.get(i).getNumericRank();
		}
		return 0;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Hand)) return false;
		if (this.rank != ((Hand)other).rank) return false;
		for (int i = 0; i < size(); i++) {
			if (this.get(i).getRank() != ((Hand)other).get(i).getRank()) return false;
		}
		return true;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}
	
	public HoleCards getHoleCards() {
		return holeCards;
	}
	
	void setHoleCards(HoleCards holeCards) {
		this.holeCards = holeCards;
	}

	public String toString() {
		String result = new String();
		switch (rank) {
		case 0:
			result += "High Card: ";
			break;
		case 1:
			result += "One Pair: ";
			break;
		case 2:
			result += "Two Pair: ";
			break;
		case 3:
			result += "Three of a Kind: ";
			break;
		case 4:
			result += "Straight: ";
			break;
		case 5:
			result += "Flush: ";
			break;
		case 6:
			result += "Full House: ";
			break;
		case 7:
			result += "Four of a Kind: ";
			break;
		case 8:
			result += "Straight Flush: ";
			break;
		case 9:
			result += "Royal Flush: ";
			break;
		}
		for (int i = 0; i < size(); i++) {
			result += get(i);
		}

		return result;
	}

	private static final long serialVersionUID = 1L;
	private int rank;
	private HoleCards holeCards;
	public static final int handSize = 5;
}
