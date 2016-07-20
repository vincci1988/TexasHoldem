package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;
import holdem.*;

public class HighCardJudgeTest {

	@Test
	public void testHighCardJudge() throws Exception {
		// "dK sQ d2 dJ h9" + "cA d3"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(50));
		board.add(new Card(14));
		board.add(new Card(21));
		HoleCards holeCards = new HoleCards(new Card(3), new Card(46));
		HighCardJudge judge = new HighCardJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("High Card (null)", hand != null);
		assertTrue("High Card (regular/rank)", hand.getRank() == 0);
		assertTrue("High Card (regular/hand)", hand.toString().equals("High Card: cA dK sQ dJ h9 "));
	}

}
