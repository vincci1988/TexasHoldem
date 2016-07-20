package holdem;

public class Judge {
	
	public static Hand getBestHand(Board board, HoleCards holeCards) throws Exception {
		Hand hand = computeBestHand(board, holeCards);
		hand.setHoleCards(holeCards);
		return hand;
	}

	private static Hand computeBestHand(Board board, HoleCards holeCards) throws Exception {
		Hand hand = null;
		Hand temp = null;
		SFandFLJudge SF_FLJudge = new SFandFLJudge(board, holeCards);
		hand = SF_FLJudge.getBestHand();
		if (hand != null && hand.getRank() > 7) 
			return hand;
		FourOfAKindJudge FK_Judge = new FourOfAKindJudge(board, holeCards);
		temp = FK_Judge.getBestHand();
		if (temp != null)
			return temp;
		FHandTKJudge FH_TKJudge = new FHandTKJudge(board, holeCards);
		temp = FH_TKJudge.getBestHand();
		if (temp != null && temp.getRank() == 6)
			return temp;
		if (hand != null) 
			return hand;
		hand = temp;
		StraightJudge STJudge = new StraightJudge(board, holeCards);
		temp = STJudge.getBestHand();
		if (temp != null)
			return temp;
		if (hand != null)
			return hand;
		PairJudge OPJudge = new PairJudge(board, holeCards);
		hand = OPJudge.getBestHand();
		if (hand != null)
			return hand;
		HighCardJudge HCJudge = new HighCardJudge(board, holeCards);
		hand = HCJudge.getBestHand();
		return hand;
	}

}
