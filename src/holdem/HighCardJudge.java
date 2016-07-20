package holdem;
public class HighCardJudge extends JudgeBase {
	
	public HighCardJudge(Board board, HoleCards holeCards) throws Exception {
		super(board, holeCards);		
	}
	
	public Hand getBestHand() {
		hand = new Hand();
		hand.addAll(sorted);
		while (hand.size() != 5)
			hand.remove(hand.size() - 1);
		hand.setRank(0);
		return hand;
	}
	
	Hand hand;
}
