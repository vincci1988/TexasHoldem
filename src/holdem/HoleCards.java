package holdem;

public class HoleCards {
	
	public HoleCards(Card first, Card second) throws Exception {
		if (first.equals(second)) throw new Exception("HoleCards Constructor: Invalid hole cards (identical).");
		if (first.compareTo(second) < 0) {
			this.first = first;
			this.second = second;
		}
		else {
			this.first = second;
			this.second = first;
		}
		seat = null;
	}
	
	public Card getHighCard() {
		return first;
	}
	
	public Card getKicker() {
		return second;
	}
	
	public boolean contains(Card card) {
		return first.equals(card) || second.equals(card);
	}
	
	public String toString() {
		return first.toString() + second.toString();
	}
	
	private Card first;
	private Card second;
	Seat seat;
}
