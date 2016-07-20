package holdem;

public class FourOfAKindJudge extends JudgeBase {
	
	public FourOfAKindJudge(Board board, HoleCards holeCards) throws Exception {
		super(board, holeCards);
	}

	public Hand getBestHand() {
		for (int i = 0, runner = 1; i < sorted.size() - 3; runner++) {
			if (sorted.get(runner).getRank() != sorted.get(i).getRank()) i = runner;
			else if (runner - i == 3) {
				hand = new Hand();
				for (int j = 0; j < 4; j++) {
					hand.add(sorted.get(i));
					sorted.remove(i);
				}
				break;
			}
		}

		if (hand != null) {
			hand.add(sorted.get(0));
			hand.setRank(7);
		}
		return hand;
	}

	Hand hand;
}
