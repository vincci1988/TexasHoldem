package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;
import holdem.*;

public class JudgeTest {

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
		Hand hand = Judge.getBestHand(board, holeCards);
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
		Hand hand = Judge.getBestHand(board, holeCards);
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
		hand = Judge.getBestHand(board, holeCards);
		assertTrue("Straight Flush (wheel/null)", hand != null);
		assertTrue("Straight Flush (wheel/rank)", hand.getRank() == 8);
		assertTrue("Straight Flush (wheel/hand)", hand.toString().equals("Straight Flush: c5 c4 c3 c2 cA "));
	}
	
	@Test
	public void testFourOfAKind() throws Exception {
		Board board = new Board();
		// "hK sQ dQ cQ c9" + "hQ dK"
		board.add(new Card(5));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(11));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(9), new Card(6));
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("Four of a Kind (null)", hand != null);
		assertTrue("Four of a Kind (rank)", hand.getRank() == 7);
		assertTrue("Four of a Kind (hand)", hand.toString().equals("Four of a Kind: sQ hQ dQ cQ hK "));
		
	}
	
	@Test
	public void testFullHouse() throws Exception {
		// "dK sQ dQ hQ c9" + "hK cK"
		Board board = new Board();
		board.add(new Card(6));
		board.add(new Card(8));
		board.add(new Card(10));
		board.add(new Card(9));
		board.add(new Card(23));
		HoleCards holeCards = new HoleCards(new Card(5), new Card(7));
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("Full House (null)", hand != null);
		assertTrue("Full House (rank)", hand.getRank() == 6);
		assertTrue("Full House (hand)", hand.toString().equals("Full House: hK dK cK sQ hQ "));
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
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("Flush (null)", hand != null);
		assertTrue("Flush (rank)", hand.getRank() == 5);
		assertTrue("Flush (hand)", hand.toString().equals("Flush: dK dQ dJ dT d8 "));
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
		Hand hand = Judge.getBestHand(board, holeCards);
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
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("Straight (wheel/null)", hand != null);
		assertTrue("Straight (wheel/rank)", hand.getRank() == 4);
		assertTrue("Straight (wheel/hand)", hand.toString().equals("Straight: d5 c4 c3 c2 cA "));
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
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("Three of a Kind (null)", hand != null);
		assertTrue("Three of a Kind (rank)", hand.getRank() == 3);
		assertTrue("Three of a Kind (hand)", hand.toString().equals("Three of a Kind: sQ hQ dQ cA dK "));
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
		Hand hand = Judge.getBestHand(board, holeCards);
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
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("One Pair (null)", hand != null);
		assertTrue("One Pair (rank)", hand.getRank() == 1);
		assertTrue("One Pair (hand)", hand.toString().equals("One Pair: sQ hQ cA dK dJ "));
	}
	
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
		Hand hand = Judge.getBestHand(board, holeCards);
		assertTrue("High Card (null)", hand != null);
		assertTrue("High Card (rank)", hand.getRank() == 0);
		assertTrue("High Card (hand)", hand.toString().equals("High Card: cA dK sQ dJ h9 "));
	}

}
