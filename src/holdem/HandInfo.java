package holdem;

public class HandInfo {

	HandInfo(Hand hand) {
		HoleCards holeCards = hand.getHoleCards();
		this.playerName = holeCards.seat.player.getName();
		this.playerID = holeCards.seat.player.id;
		this.holeCards = holeCards.toString();
		this.hand = hand.toString();
	}
	
	public String toString() {
		return "HAND INFO: " + playerName + ", Hole Cards: " + holeCards + ", " + hand;
	}
	
	public String playerName;
	public int playerID;
	public String holeCards;
	public String hand;
}
