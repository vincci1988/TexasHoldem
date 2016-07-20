package holdem;

public class SFandFLJudge extends JudgeBase {
	public SFandFLJudge(Board cards, HoleCards holeCards) throws Exception {
		super(cards, holeCards);
	}

	public Hand getBestHand() {
		Hand diamonds = new Hand();
		Hand spades = new Hand();
		Hand hearts = new Hand();
		Hand clubs = new Hand();

		for (int i = 0; i < sorted.size(); i++) {
			switch (sorted.get(i).getSuit()) {
			case 'd':
				diamonds.add(sorted.get(i));
				break;
			case 's':
				spades.add(sorted.get(i));
				break;
			case 'h':
				hearts.add(sorted.get(i));
				break;
			case 'c':
				clubs.add(sorted.get(i));
				break;
			}
		}

		if (diamonds.size() > 4)
			hand = diamonds;
		else if (spades.size() > 4)
			hand = spades;
		else if (hearts.size() > 4)
			hand = hearts;
		else if (clubs.size() > 4)
			hand = clubs;

		if (hand != null) {
			if (hand.get(0).getRank() == 'A')
				hand.add(hand.get(0));
			hand.setRank(5);
			for (int i = 0; i < hand.size() - 4; i++) {
				if (hand.get(i).getNumericRank() - hand.get(i + 4).getNumericRank() == 4 || hand.get(i).getNumericRank() == 4) {
					Hand buffer = hand;
					hand = new Hand();
					for (int j = 0; j < 5; j++) {
						hand.add(buffer.get(i + j));
					}
					hand.setRank(hand.get(0).getRank() == 'A' ? 9 : 8);
					break;
				}
			}
			if (hand.getRank() == 5) {
				while (hand.size() > 5)
					hand.remove(hand.size() - 1);
			}
		}
		return hand;
	}

	Hand hand;
}
