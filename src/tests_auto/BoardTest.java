package tests_auto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import holdem.Board;
import holdem.Card;
import holdem.Deck;

public class BoardTest {

	@Test
	public void testToStringAndDisplay() throws Exception {
		Board board = new Board();
		assertTrue("BOARD SIZE", board.size() == 0);
		int testCnt = 100;
		for (int i = 0; i < testCnt; i++) {
			String ansToString = new String();
			String ansDisplay = new String();
			Deck deck = new Deck();
			ArrayList<Card> cards = new ArrayList<Card>();
			deck.shuffle();
			for (int j = 0; j < riverSize; j++) {
				Card card = deck.draw();
				board.add(card);
				ansDisplay += card.toString();
				cards.add(card);
			}
			Collections.sort(cards);
			for (int j = 0; j < cards.size(); j++) 
				ansToString += cards.get(j);
			assertTrue("BOARD TO STRING: " + ansToString + " " + board.toString(),
					ansToString.equals(board.toString()));
			assertTrue("BOARD DISPLAY: " + ansDisplay + " " + board.toString(), ansDisplay.equals(board.display()));
			board.clear();
		}
	}
	
	@Test
	public void testSort() throws Exception {
		Board board = new Board();
		int testCnt = riverSize;
		for (int i = 0; i < testCnt; i++) {
			Deck deck = new Deck();
			ArrayList<Card> cards = new ArrayList<Card>();
			deck.shuffle();
			for (int j = 0; j < i; j++) {
				Card card = deck.draw();
				board.add(card);
				cards.add(card);
			}
			board.sort();
			Collections.sort(cards);
			for (int j = 0; j < cards.size(); j++) 
				assertTrue(cards.get(j).equals(board.get(j)));
			board.clear();
		}
	}

	static final int deckSize = 52;
	static final int riverSize = 5;
}
