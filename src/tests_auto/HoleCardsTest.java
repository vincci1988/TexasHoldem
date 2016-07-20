package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;

import holdem.Card;
import holdem.Deck;
import holdem.HoleCards;

public class HoleCardsTest {

	@Test
	public void testHoleCards() throws Exception {
		for (int i = 0; i < testCnt; i++) {
			Deck deck = new Deck();
			deck.shuffle();
			Card first = deck.draw();
			Card second = deck.draw();
			HoleCards holeCards = new HoleCards(first, second);
			if (first.compareTo(second) < 0) assertTrue(holeCards.getHighCard().equals(first));
			else assertTrue(holeCards.getKicker().equals(first));
		}
	}

	static final int testCnt = 100;
}
