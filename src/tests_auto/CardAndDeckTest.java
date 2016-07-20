package tests_auto;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;
import holdem.Card;
import holdem.Deck;

public class CardAndDeckTest {

	@Test
	public void testDeckConstructor() throws Exception {
		Deck deck = new Deck();
		assertTrue(deck.size() == deckSize);
		for (int i = 0; i < deck.size(); i++) {
			assertTrue(deck.get(i).equals(new Card(i)));
		}
		assertTrue(deck.get(0).equals(deck.draw()));
	}
	
	@Test
	public void testShuffle() throws Exception {
		Deck deck = new Deck();
		deck.shuffle();
		assertTrue(deck.get(0).equals(deck.draw()));
	}
	
	@Test
	public void testDraw() throws Exception {
		Deck deck = new Deck();
		for (int i = 0; i < deckSize; i++) {
			Card card = deck.draw();
			assertFalse(card == null);
			assertTrue(card == deck.get(i));
		}
	}
	
	@Test
	public void testSort() throws Exception {
		Deck deck1 = new Deck();
		deck1.shuffle();
		deck1.sort();
		Deck deck2 = new Deck();
		assertEquals(deck1.size(), deck2.size());
		for (int i = 0; i < deck1.size(); i++)
			assertTrue(deck1.get(i).equals(deck2.get(i)));
	}
	
	@Test
	public void testRemoveByCode() throws Exception {
		
		Random rand = new Random();
		for (int i = 0; i < deckSize; i++) {
			Deck deck = new Deck();
			int code = rand.nextInt(deckSize);
			Card toRemove = new Card(code);
			assertTrue(deck.contains(toRemove));
			deck.removeByCode(code);
			assertTrue(!deck.contains(toRemove));
		}
	}
	
	@Test
	public void testCompareRank() throws Exception {
		Deck deck1 = new Deck();
		Deck deck2 = new Deck();
		deck2.shuffle();
		for (int i = 0; i < deckSize; i++) {
			Card first = deck1.draw();
			Card second = deck2.draw();
			if (first.getRank() == second.getRank()) {
				assertTrue(first.compareRank(second) == 0);
			}
			else assertTrue(first.compareRank(second) * first.compareTo(second) < 0);
		}
	}

	static final int deckSize = 52;
}
