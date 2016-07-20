package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;
import holdem.*;

public class StraightJudgeTest {

	@Test
	public void testOnNull() throws Exception {
		Board board = new Board();
		// "hK sQ dQ dJ c9" + "dA dK"
		board.add(new Card(5));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(6), new Card(2));
		StraightJudge judge = new StraightJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Straight: Null", hand == null);
	}
	
	@Test
	public void testRegularStraight() throws Exception {
		// "dK sQ cQ dJ c9" + "c9 dT"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(11));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(23), new Card(18));
		StraightJudge judge = new StraightJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Straight (regular/null)", hand != null);
		assertTrue("Straight (regular/rank)", hand.getRank() == 4);
		assertTrue("Straight (regular/hand)", hand.toString().equals("Straight: dK cQ dJ dT c9 "));
	}

	@Test
	public void testWheel() throws Exception {
		// "cA c3 d5 d4 d2" + "c4 c2"
		Board board = new Board();
		board.add(new Card(3));
		board.add(new Card(47));
		board.add(new Card(38));
		board.add(new Card(42));
		board.add(new Card(50));
		HoleCards holeCards = new HoleCards(new Card(43), new Card(51));
		StraightJudge judge = new StraightJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Straight (wheel/null)", hand != null);
		assertTrue("Straight (wheel/rank)", hand.getRank() == 4);
		assertTrue("Straight (wheel/hand)", hand.toString().equals("Straight: d5 c4 c3 c2 cA "));
	}

}
