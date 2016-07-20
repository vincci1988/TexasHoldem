package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;
import holdem.*;

public class FullHouseAndThreeOfAKindTest {

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
		FHandTKJudge judge = new FHandTKJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Full House and Three of a Kind: Null", hand == null);
	}

	@Test
	public void testFullHouse() throws Exception {
		// "dK sQ dQ dJ c9" + "hQ cK"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(9), new Card(7));
		FHandTKJudge judge = new FHandTKJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Full House (null)", hand != null);
		assertTrue("Full House (rank)", hand.getRank() == 6);
		assertTrue("Full House (hand)", hand.toString().equals("Full House: sQ hQ dQ dK cK "));
	}

	@Test
	public void testThreeOfAKind() throws Exception {
		// "dK sQ dQ dJ c9" + "hQ cA"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(14));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(9), new Card(3));
		FHandTKJudge judge = new FHandTKJudge(board, holeCards);
		Hand hand = judge.getBestHand();
		assertTrue("Three of a Kind (null)", hand != null);
		assertTrue("Three of a Kind (rank)", hand.getRank() == 3);
		assertTrue("Three of a Kind (hand)", hand.toString().equals("Three of a Kind: sQ hQ dQ cA dK "));
	}

}
