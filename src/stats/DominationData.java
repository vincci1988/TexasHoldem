package stats;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import holdem.*;

public class DominationData {

	public void compute(String outputFile) throws Exception {
		PrintWriter writer = new PrintWriter(outputFile);
		ArrayList<DominationDatum> dominations = new ArrayList<DominationDatum>();
		for (int i = 0; i < 51; i++) {
			for (int j = i + 1; j < 52; j++) {
				Deck deck = new Deck();
				HoleCards holeCards = new HoleCards(deck.get(i), deck.get(j));
				deck.remove(holeCards.getHighCard());
				deck.remove(holeCards.getKicker());
				double dominated = 0;
				double dominance = 0;
				for (int k = 0; k < deck.size() - 1; k++) {
					for (int l = k + 1; l < deck.size(); l++) {
						HoleCards other = new HoleCards(deck.get(k), deck.get(l));
						double wr = preflopWinRate(holeCards, other);
						if (wr < 0.5) {
							dominated++;
							dominance += (1 - 2 * wr);
						}
					}
				}
				dominated /= 1225;
				dominance = dominance / 1225;
				dominations.add(new DominationDatum(holeCards, dominated, dominance));
			}
		}
		Collections.sort(dominations);
		for (int i = 0; i < dominations.size(); i++)
			writer.println(dominations.get(i));
		writer.close();
	}
	
	public double preflopWinRate(HoleCards mine, HoleCards other) {
		return estimateWinRate(mine, other) + ((mine.suited() && !other.suited()) ? 0.015 : 0);
	}

	private double estimateWinRate(HoleCards mine, HoleCards other) {
		if (mine.paired()) {
			if (other.paired()) {
				if (other.getHighCard().getNumericRank() == mine.getHighCard().getNumericRank())
					return 0.50;
				return other.getHighCard().getNumericRank() > mine.getHighCard().getNumericRank() ? 0.20 : 0.80;
			} else {
				int myRank = mine.getHighCard().getNumericRank();
				int herHighCard = other.getHighCard().getNumericRank();
				int herKicker = other.getKicker().getNumericRank();
				if (herKicker > myRank)
					return 0.55;
				if (herKicker == myRank)
					return 0.70;
				if (herHighCard > myRank && myRank > herKicker)
					return 0.70;
				if (herHighCard == myRank)
					return 0.90;
				return 0.85;
			}
		} else {
			if (other.paired()) {
				return 1.0 - preflopWinRate(other, mine);
			} else {
				int myHighCard = mine.getHighCard().getNumericRank();
				int myKicker =mine.getKicker().getNumericRank();
				int herHighCard = other.getHighCard().getNumericRank();
				int herKicker = other.getKicker().getNumericRank();
				if (herHighCard < myKicker)
					return 0.65;
				if (herHighCard == myKicker)
					return 0.73;
				if (herHighCard < myHighCard && herHighCard > myKicker && myKicker > herKicker)
					return 0.62;
				if (herHighCard < myHighCard && herKicker == myKicker)
					return 0.73;
				if (herHighCard < myHighCard && herKicker > myKicker)
					return 0.57;
				if (herHighCard == myHighCard && herKicker < myKicker)
					return 0.74;
				if (herHighCard == myHighCard && herKicker == myKicker)
					return 0.50;
				return 1 - preflopWinRate(other, mine);
			}
		}
	}
}
