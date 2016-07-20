package tests_auto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import holdem.*;

public class HandTest {

	@Test
	public void testSort() throws Exception {
		ArrayList<Hand> hands = new ArrayList<Hand>();
		
		//Royal Flush
		Hand hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(4));
		hand.add(new Card(8));
		hand.add(new Card(12));
		hand.add(new Card(16));
		hand.setRank(9);
		hands.add(hand);
		
		//Straight Flush
		hand = new Hand();
		hand.add(new Card(4));
		hand.add(new Card(8));
		hand.add(new Card(12));
		hand.add(new Card(16));
		hand.add(new Card(20));
		hand.setRank(8);
		hands.add(hand);
		
		//Four of a Kind
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.add(new Card(2));
		hand.add(new Card(3));
		hand.add(new Card(4));
		hand.setRank(7);
		hands.add(hand);
		
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.add(new Card(2));
		hand.add(new Card(3));
		hand.add(new Card(8));
		hand.setRank(7);
		
		hand = new Hand();
		hand.add(new Card(4));
		hand.add(new Card(5));
		hand.add(new Card(6));
		hand.add(new Card(7));
		hand.add(new Card(0));
		hand.setRank(7);
		hands.add(hand);
		
		//Full House
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.add(new Card(2));
		hand.add(new Card(4));
		hand.add(new Card(5));
		hand.setRank(6);
		hands.add(hand);
		
		hand = new Hand();
		hand.add(new Card(4));
		hand.add(new Card(5));
		hand.add(new Card(6));
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.setRank(6);
		hands.add(hand);
		
		//Flush
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(4));
		hand.add(new Card(8));
		hand.add(new Card(12));
		hand.add(new Card(20));
		hand.setRank(5);
		hands.add(hand);
		
		hand = new Hand();
		hand.add(new Card(1));
		hand.add(new Card(5));
		hand.add(new Card(9));
		hand.add(new Card(13));
		hand.add(new Card(25));
		hand.setRank(5);
		hands.add(hand);
		
		//Straight
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(4));
		hand.add(new Card(8));
		hand.add(new Card(12));
		hand.add(new Card(17));
		hand.setRank(4);
		hands.add(hand);
		
		hand = new Hand();
		hand.add(new Card(36));
		hand.add(new Card(40));
		hand.add(new Card(44));
		hand.add(new Card(48));
		hand.add(new Card(1));
		hand.setRank(4);
		hands.add(hand);
		
		//Three of a Kind
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.add(new Card(2));
		hand.add(new Card(12));
		hand.add(new Card(20));
		hand.setRank(3);
		hands.add(hand);
		
		hand = new Hand();
		hand.add(new Card(4));
		hand.add(new Card(5));
		hand.add(new Card(6));
		hand.add(new Card(0));
		hand.add(new Card(11));
		hand.setRank(3);
		hands.add(hand);
		
		//Two Pair
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.add(new Card(4));
		hand.add(new Card(5));
		hand.add(new Card(51));
		hand.setRank(2);
		hands.add(hand);
		
		//One Pair
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(1));
		hand.add(new Card(8));
		hand.add(new Card(13));
		hand.add(new Card(20));
		hand.setRank(1);
		hands.add(hand);
		
		//High Card
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(4));
		hand.add(new Card(8));
		hand.add(new Card(12));
		hand.add(new Card(21));
		hand.setRank(0);
		hands.add(hand);
		
		hand = new Hand();
		hand.add(new Card(0));
		hand.add(new Card(5));
		hand.add(new Card(13));
		hand.add(new Card(16));
		hand.add(new Card(21));
		hand.setRank(0);
		hands.add(hand);
		
		ArrayList<Hand> others = new ArrayList<Hand>();
		others.addAll(hands);
		Collections.shuffle(others);
		Collections.sort(others);
		
		for (int i = 0; i < hands.size(); i++) 
			assertTrue("Hand.compareTo and Hand.equals", hands.get(i).equals(others.get(i)));
		
		hand = new Hand();
		hand.add(new Card(1));
		hand.add(new Card(5));
		hand.add(new Card(9));
		hand.add(new Card(13));
		hand.add(new Card(17));
		hand.setRank(9);
		assertTrue("Hand.equals", hands.get(0).equals(hand));
	}

}
