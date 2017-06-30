package advanced_players;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public class HandRangeEvaluator {
	public HandRangeEvaluator() {
		String separator = System.getProperty("os.name").contains("Windows") ? "\\" : "/";
		preflopPath = HSDBPath + separator + "preflop" + separator + "preflop.txt";
		flopPath = HSDBPath + separator + "flop" + separator;
		turnPath = HSDBPath + separator + "turn" + separator;
		riverPath = HSDBPath + separator + "river" + separator;
		board = "INIT";
		fullRange = new Vector<HandDatum>();
		handRange = null;
		myHand = null;
	}

	public void gameStart() {
		board = "INIT";
		fullRange.clear();
		handRange = null;
		myHand = null;
	}
	
	public void update(String myCards, String board) throws IOException {
		if (board.compareTo(this.board) == 0)
			return;
		this.board = board;
		fullRange.clear();
		FileReader fileReader = new FileReader(getFilePath(board));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int lineCnt = getLineCnt();
		float current = -1, absRank = -1;
		for (int i = 0; i < lineCnt; i++) {
			String line = bufferedReader.readLine();
			String cards = line.substring(0, 4);
			Float expRank = Float.parseFloat(line.substring(5));
			if (expRank > current) {
				current = expRank;
				absRank = fullRange.size();
			}
			if (compatible(myCards, cards) || myCards.equals(cards)) {
				HandDatum datum = new HandDatum(cards, absRank, expRank);
				if (myCards.equals(cards))
					myHand = datum;
				fullRange.add(datum);
			}
		}
		bufferedReader.close();
		if (handRange == null)
			initRange();
		else
			updateRange();
	}

	public void adjustRange(double cutoff) {
		int rangeSize = (int) (handRange.size() * cutoff);
		HashSet<String> newRange = new HashSet<String>();
		for (int i = 0; newRange.size() < rangeSize && i < fullRange.size(); i++) {
			String hand = fullRange.get(i).hand;
			if (handRange.contains(hand) && compatible(hand, board + myHand.hand))
				newRange.add(hand);
		}
		handRange = newRange;
	}
	
	public double getRankStrength() {
		int absRank = (int) myHand.absRank;
		float rank = 0;
		for (int i = 0; i < absRank; i++) {
			String hand = fullRange.get(i).hand;
			if (handRange.contains(hand))
				rank++;
		}
		return 1.0 - rank / handRange.size();
	}
	
	public void printRange() {
		int rank = 0;
		for (int i = 0; i < fullRange.size(); i++) {
			String hand = fullRange.get(i).hand;
			if (handRange.contains(hand)) {
				System.out.println((++rank) + " :" + hand);
			}
		}
	}

	private void initRange() {
		handRange = new HashSet<String>();
		for (int i = 0; i < fullRange.size(); i++) {
			String hand = fullRange.get(i).hand;
			if (!hand.equals(myHand.hand))
				handRange.add(hand);
		}
	}
	
	private void updateRange() {
		for (Iterator<String> i = handRange.iterator(); i.hasNext();) {
			String hand = i.next();
			if (!compatible(hand, board))
				handRange.remove(i);
		}
	}

	private String getFilePath(String board) {
		if (board.length() == 0)
			return preflopPath;
		if (board.length() == 6)
			return flopPath + board + ".txt";
		if (board.length() == 8)
			return turnPath + board + ".txt";
		if (board.length() == 10)
			return riverPath + board + ".txt";
		return null;
	}

	private int getLineCnt() {
		if (board.length() == 0)
			return preflopLineCnt;
		if (board.length() == 6)
			return flopLineCnt;
		if (board.length() == 8)
			return turnLineCnt;
		if (board.length() == 10)
			return riverLineCnt;
		return 0;
	}

	private boolean compatible(String myCards, String cards) {
		String myFirst = myCards.substring(0, 2);
		String mySecond = myCards.substring(2, 4);
		return !(cards.contains(myFirst) || cards.contains(mySecond));
	}

	public static final String HSDBPath = "B:\\HSDB";
	
	String preflopPath;
	String flopPath;
	String turnPath;
	String riverPath;
	String board;
	Vector<HandDatum> fullRange;
	HashSet<String> handRange;
	HandDatum myHand;

	private static final int preflopLineCnt = 1326;
	private static final int flopLineCnt = 1176;
	private static final int turnLineCnt = 1128;
	private static final int riverLineCnt = 1081;
}
