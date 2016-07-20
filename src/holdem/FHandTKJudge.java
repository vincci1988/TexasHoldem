package holdem;

public class FHandTKJudge extends JudgeBase {
	public FHandTKJudge(Board board, HoleCards holeCards) throws Exception {
		super(board, holeCards);
	}

	public Hand getBestHand() {
		int i = 0;
		for (int runner = 1; i < sorted.size() - 2; runner++) {
			if (sorted.get(i).getRank() != sorted.get(runner).getRank())
				i = runner;
			else if (runner - i == 2) {
				hand = new Hand();
				for (int j = 0; j < 3; j++) {
					hand.add(sorted.get(i));
					sorted.remove(i);
				}
				break;
			}
		}

		if (hand != null) {
			for (i = 0; i < sorted.size() - 1; i++) {
				if (sorted.get(i).getRank() == sorted.get(i + 1).getRank()) {
					hand.add(sorted.get(i));
					hand.add(sorted.get(i + 1));
					break;
				}
			}

			if (hand.size() == 3) {
				hand.add(sorted.get(0));
				hand.add(sorted.get(1));
				hand.setRank(3);
			} else
				hand.setRank(6);
		}
		return hand;
	}

	Hand hand;
}
