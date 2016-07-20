package stats;

class HoleCardsStrengthDatum implements Comparable<HoleCardsStrengthDatum> {

	HoleCardsStrengthDatum(String holeCards) {
		this.holeCards = holeCards;
		avgRank = 0;
	}

	@Override
	public int compareTo(HoleCardsStrengthDatum other) {
		if (this.avgRank != other.avgRank)
			return this.avgRank - other.avgRank < 0 ? -1 : 1;
		return compareHoleCards(this.holeCards, other.holeCards);
	}

	public boolean equals(Object other) {
		if (other instanceof String) {
			return this.holeCards.equals((String) other);
		}
		if (other instanceof HoleCardsStrengthDatum) {
			return this.holeCards.equals(((HoleCardsStrengthDatum) other).holeCards)
					&& this.avgRank == ((HoleCardsStrengthDatum) other).avgRank;
		}
		return false;
	}
	
	public String toString() {
		return holeCards + " " + avgRank;
	}

	static private int compareHoleCards(String first, String second) {
		return holeCardsStringToCode(first) - holeCardsStringToCode(second);
	}

	static private int holeCardsStringToCode(String holeCards) {
		int highCard = cardStringToCode(holeCards.substring(0, 2));
		int kicker = cardStringToCode(holeCards.substring(2));
		return highCard * 52 + kicker;
	}

	static private int cardStringToCode(String card) {
		int suit = 0;
		switch (card.charAt(0)) {
		case 's':
			suit = 0;
			break;
		case 'h':
			suit = 1;
			break;
		case 'd':
			suit = 2;
			break;
		case 'c':
			suit = 3;
			break;
		}
		int rank = 0;
		switch (card.charAt(1)) {
		case 'A':
			rank = 0;
			break;
		case 'K':
			rank = 1;
			break;
		case 'Q':
			rank = 2;
			break;
		case 'J':
			rank = 3;
			break;
		case 'T':
			rank = 4;
			break;
		case '9':
			rank = 5;
			break;
		case '8':
			rank = 6;
			break;
		case '7':
			rank = 7;
			break;
		case '6':
			rank = 8;
			break;
		case '5':
			rank = 9;
			break;
		case '4':
			rank = 10;
			break;
		case '3':
			rank = 11;
			break;
		case '2':
			rank = 12;
			break;
		}
		return rank * 4 + suit;
	}

	String holeCards;
	float avgRank;
}
