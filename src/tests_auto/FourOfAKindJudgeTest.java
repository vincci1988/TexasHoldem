package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;

import holdem.*;

public class FourOfAKindJudgeTest {

	@Test
	public void testOnNull() throws Exception {
		Board board = new Board();
		// "hK sQ dQ cQ c9" + "dA dK"
		board.add(new Card(5));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(11));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(6), new Card(2));
		FourOfAKindJudge judge = new FourOfAKindJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Four of a Kind: Null", hand == null);
	}
	
	@Test
	public void testFourOfAKind() throws Exception {
		Board board = new Board();
		// "hK sQ dQ cQ c9" + "hQ dA"
		board.add(new Card(5));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(11));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(9), new Card(2));
		FourOfAKindJudge judge = new FourOfAKindJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Four of a Kind (null)", hand != null);
		assertTrue("Four of a Kind (rank)", hand.getRank() == 7);
		assertTrue("Four of a Kind (hand)", hand.toString().equals("Four of a Kind: sQ hQ dQ cQ dA "));
		
	}
}
