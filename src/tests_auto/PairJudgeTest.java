package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;
import holdem.*;

public class PairJudgeTest {
	
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
		PairJudge judge = new PairJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Pair: NULL", hand == null);
	}

	@Test
	public void testTwoPair() throws Exception {
		// "dK sQ dQ dJ cA" + "hJ cK"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(3));
		HoleCards holeCards = new HoleCards(new Card(13), new Card(7));
		PairJudge judge = new PairJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Two Pair (null)", hand != null);
		assertTrue("Two Pair (rank)", hand.getRank() == 2);
		assertTrue("Two Pair (hand)", hand.toString().equals("Two Pair: dK cK sQ dQ cA "));
	}

	@Test
	public void testOnePair() throws Exception {
		// "dK sQ d2 dJ c9" + "hQ cA"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(50));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(9), new Card(3));
		PairJudge judge = new PairJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("One Pair (null)", hand != null);
		assertTrue("One Pair (rank)", hand.getRank() == 1);
		assertTrue("One Pair (hand)", hand.toString().equals("One Pair: sQ hQ cA dK dJ "));
	}

}
