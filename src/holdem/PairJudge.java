package holdem;

public class PairJudge extends JudgeBase {
	
	public PairJudge(Board board, HoleCards holeCards) throws Exception {
		super(board, holeCards);			
	}

	public Hand getBestHand() {
		int pairCnt = 0;
		for (int i = 0; i < sorted.size() - 1 && pairCnt < 2;) {
			if (sorted.get(i).getRank() == sorted.get(i + 1).getRank()) {
				if (hand == null) hand = new Hand();
				extractPair(sorted, i);
				pairCnt++;
			}
			else i++;
		}

		if (hand != null) {
			int kickerCnt = 5 - pairCnt * 2;
			for (int i = 0; i < kickerCnt; i++) 
				hand.add(sorted.get(i));
			hand.setRank(pairCnt == 1 ? 1 : 2);
		}
		return hand;
	}

	private void extractPair(Hand sorted, int index) {
		for (int j = 0; j < 2; j++) {
			hand.add(sorted.get(index));
			sorted.remove(index);
		}
	}

}
