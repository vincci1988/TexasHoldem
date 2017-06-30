package ASHEPlus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class HandRangeEvaluator {
	public HandRangeEvaluator() {
		String separator = System.getProperty("os.name").contains("Windows") ? "\\" : "/";
		preflopPath = HSDBPath + separator + "preflop" + separator + "preflop.txt";
		flopPath = HSDBPath + separator + "flop" + separator;
		turnPath = HSDBPath + separator + "turn" + separator;
		riverPath = HSDBPath + separator + "river" + separator;

		board = "INIT";
		handRange = null;
		myHand = null;

		boards = new String[stageCnt];
		for (int i = 0; i < boards.length; i++)
			boards[i] = "INIT";
		maps = new Vector<HashMap<String, Float>>();
		for (int i = 0; i < stageCnt; i++)
			maps.add(new HashMap<String, Float>());
	}

	public void init() throws Exception {
		FileReader fileReader = new FileReader(preflopPath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		for (int i = 0; i < preflopLineCnt; i++) {
			String line = bufferedReader.readLine();
			String cards = line.substring(0, 4);
			Float expRank = Float.parseFloat(line.substring(5));
			maps.get(0).put(cards, expRank);
		}
		bufferedReader.close();
	}

	public void gameStart() {
		board = "INIT";
		for (int i = 0; i < boards.length; i++)
			boards[i] = "INIT";
		handRange = null;
		myHand = null;
		for (int i = 1; i < maps.size(); i++)
			maps.get(i).clear();
	}

	public void update(String myCards, String board) throws Exception {
		int index = getIndex(board);
		if (boards[index].equals(board))
			return;
		this.board = board;
		boards[index] = board;
		HashMap<String, Float> map = maps.get(index);
		if (board.length() > 0) {
			FileReader fileReader = new FileReader(getFilePath(board));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int lineCnt = getLineCnt();
			for (int i = 0; i < lineCnt; i++) {
				String line = bufferedReader.readLine();
				String cards = line.substring(0, 4);
				Float expRank = Float.parseFloat(line.substring(5));
				map.put(cards, expRank);
				if (myCards.equals(cards))
					myHand = new HandDatum(cards, expRank);
			}
			bufferedReader.close();
		}
		if (myHand == null)
			myHand = new HandDatum(myCards, maps.get(0).get(myCards));
		if (handRange == null)
			initRange();
		else
			updateRange();
	}

	public void adjustRange(double cutoff) {
		int boundIndex = (int) (handRange.size() * cutoff);
		String hand = handRange.get(boundIndex).hand;
		double bound = maps.get(getIndex(board)).get(hand);
		for (int i = boundIndex + 1; i < handRange.size();) {
			if (handRange.get(i).expRank > bound)
				handRange.remove(i);
			else
				i++;
		}
	}
	
	public double expectedStrength(String holeCards, String board) throws Exception {
		int index = getIndex(board);
		HashMap<String, Float> map = maps.get(index);
		if (!map.containsKey(holeCards) || !boards[index].equals(board))
			throw new Exception("AshePlus.HandRangeEvaluator.expectedStrength: Invalid cards/board!");
		return 1.0 - map.get(holeCards) / handCnt[3];
	}

	public double absoluteStength(String holeCards, String board) throws Exception {
		int index = getIndex(board);
		HashMap<String, Float> map = maps.get(index);
		if (!map.containsKey(holeCards) || !boards[index].equals(board))
			throw new Exception("AshePlus.HandRangeEvaluator.absoluteStrength: Invalid cards/board!");
		Set<String> hands = map.keySet();
		double rank = 0;
		double expRank = map.get(holeCards);
		for (Iterator<String> i = hands.iterator(); i.hasNext();) {
			String hand = i.next();
			if (compatible(hand, board + holeCards) && map.get(hand) < expRank)
				rank++;
		}
		adjustRange(0.5);
		return (handCnt[index] - rank) / handCnt[index];
	}

	public double relativeStrength(String holeCards, String board) throws Exception {
		int index = getIndex(board);
		HashMap<String, Float> map = maps.get(index);
		if (!map.containsKey(holeCards) || !boards[index].equals(board))
			throw new Exception("AshePlus.HandRangeEvaluator.relativeStrength: Invalid cards/board!");
		double expRank = map.get(holeCards);
		float rank = 0;
		for (Iterator<HandDatum> i = handRange.iterator(); i.hasNext();) {
			if (i.next().expRank < expRank)
				rank++;
			else
				break;
		}
		return 1.0 - rank / handRange.size();
	}

	private void initRange() {
		handRange = new Vector<HandDatum>();
		Set<String> hands = maps.get(0).keySet();
		for (Iterator<String> i = hands.iterator(); i.hasNext();) {
			String hand = i.next();
			if (compatible(hand, myHand.hand))
				handRange.add(new HandDatum(hand, maps.get(0).get(hand)));
		}
		Collections.sort(handRange);
	}

	private void updateRange() {
		for (int i = 0; i < handRange.size();) {
			String hand = handRange.get(i).hand;
			if (!compatible(hand, board))
				handRange.remove(i);
			else {
				handRange.get(i).expRank = maps.get(getIndex(board)).get(hand);
				i++;
			}
		}
		Collections.sort(handRange);
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

	private int getIndex(String board) {
		if (board.length() == 0)
			return 0;
		if (board.length() == 6)
			return 1;
		if (board.length() == 8)
			return 2;
		if (board.length() == 10)
			return 3;
		return -1;
	}

	public static final String HSDBPath = "B:\\HSDB";

	String preflopPath;
	String flopPath;
	String turnPath;
	String riverPath;
	String board;
	String[] boards;
	Vector<HandDatum> handRange;
	HandDatum myHand;
	private Vector<HashMap<String, Float>> maps;
	private static final int preflopLineCnt = 1326;
	private static final int flopLineCnt = 1176;
	private static final int turnLineCnt = 1128;
	private static final int riverLineCnt = 1081;
	private static final int[] handCnt = { 1225, 1081, 1035, 990 };
	private static final int stageCnt = 4;
}
