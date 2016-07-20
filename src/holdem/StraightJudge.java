package holdem;

public class StraightJudge extends JudgeBase {

	public StraightJudge(Board board, HoleCards holeCards) throws Exception {
		super(board, holeCards);
	}

	public Hand getBestHand() {
		if (sorted.get(0).getRank() == 'A')
			sorted.add(sorted.get(0));

		for (int i = 0; i < sorted.size() - 1;) {
			if (sorted.get(i).getNumericRank() == sorted.get(i + 1).getNumericRank())
				sorted.remove(i);
			else if ((sorted.get(i).getNumericRank() != sorted.get(i + 1).getNumericRank() + 1)
					&& !(sorted.get(i).getRank() == '2' && sorted.get(i + 1).getRank() == 'A')) {
				for (int j = 0; j <= i; j++)
					sorted.remove(0);
				i = 0;
			} else {
				i++;
				if (i == 4) break;
			}
		}
		if (sorted.size() >= 5) {
			hand = new Hand();
			for(int i = 0; i < 5; i++) hand.add(sorted.get(i));
			hand.setRank(4);
		}
		return hand;
	}

	Hand hand;
}
