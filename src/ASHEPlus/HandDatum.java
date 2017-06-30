package ASHEPlus;

public class HandDatum implements Comparable<HandDatum> {
	
	public HandDatum(String hand, float expRank) {
		this.hand = hand;
		this.expRank = expRank;
	}

	@Override
	public int compareTo(HandDatum other) {
		if (this.expRank == other.expRank)
			return 0;
		return this.expRank < other.expRank ? -1 : 1;
	}
	
	String hand;
	float expRank;
}
