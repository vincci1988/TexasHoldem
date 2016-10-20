package stats;

import java.text.DecimalFormat;

import holdem.HoleCards;

public class DominationDatum implements Comparable<DominationDatum> {

	DominationDatum(HoleCards holeCards, double dominated, double dominance) {
		this.holeCards = holeCards;
		this.dominated = dominated;
		this.dominance = dominance;
	}

	@Override
	public int compareTo(DominationDatum other) {
		if (dominated == 0) return other.dominated == 0 ? 0 : -1;
		if (other.dominated == 0) return 1;
		if (dominated == other.dominated)
			return 0;
		return dominated < other.dominated ? -1 : 1;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(4);
		return holeCards + " " + dominated + " " + (int)((1 - dominated) / dominance);
	}

	HoleCards holeCards;
	double dominated;
	double dominance;
}
