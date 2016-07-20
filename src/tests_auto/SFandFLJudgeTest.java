package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;

import holdem.*;

public class SFandFLJudgeTest {

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
		SFandFLJudge judge = new SFandFLJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Sraight Flush and Flush: Null", hand == null);
	}

	@Test
	public void testRoyalFlush() throws Exception {
		// "dK sQ dQ dJ c9" + "dA dT"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(2), new Card(18));
		SFandFLJudge judge = new SFandFLJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Royal Flush (null)", hand != null);
		assertTrue("Royal Flush (rank)", hand.getRank() == 9);
		assertTrue("Royal Flush (hand)", hand.toString().equals("Royal Flush: dA dK dQ dJ dT "));
	}

	@Test
	public void testStraightFlush() throws Exception {
		// "dK sQ dQ dJ c9" + "d9 dT"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(22), new Card(18));
		SFandFLJudge judge = new SFandFLJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Straight Flush (regular/null)", hand != null);
		assertTrue("Straight Flush (regular/rank)", hand.getRank() == 8);
		assertTrue("Straight Flush (regular/hand)", hand.toString().equals("Straight Flush: dK dQ dJ dT d9 "));
		board.clear();
		// "cA c3 c5 d4 d2" + "c4 c2"
		board.add(new Card(3));
		board.add(new Card(47));
		board.add(new Card(39));
		board.add(new Card(42));
		board.add(new Card(50));
		holeCards = new HoleCards(new Card(43), new Card(51));
		judge = new SFandFLJudge(board, holeCards);
		hand = judge.getBestHand();
		assertTrue("Straight Flush (wheel/null)", hand != null);
		assertTrue("Straight Flush (wheel/rank)", hand.getRank() == 8);
		assertTrue("Straight Flush (wheel/hand)", hand.toString().equals("Straight Flush: c5 c4 c3 c2 cA "));
	}

	@Test
	public void testFlush() throws Exception {
		// "dK sQ dQ dJ c9" + "d8 dT"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(26), new Card(18));
		SFandFLJudge judge = new SFandFLJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Flush (null)", hand != null);
		assertTrue("Flush (rank)", hand.getRank() == 5);
		assertTrue("Flush (hand)", hand.toString().equals("Flush: dK dQ dJ dT d8 "));
	}

}
