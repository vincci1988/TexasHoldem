package ASHE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class StrengthEvaluator {

	public StrengthEvaluator(String HSDBPath) {
		this.HSDBPath = HSDBPath;
		String separator = System.getProperty("os.name").contains("Windows") ? "\\" : "/";
		preflopPath = HSDBPath + separator + "preflop" + separator + "preflop.txt";
		flopPath = HSDBPath + separator + "flop" + separator;
		turnPath = HSDBPath + separator + "turn" + separator;
		riverPath = HSDBPath + separator + "river" + separator;
		boards = new String[stageCnt];
		for (int i = 0; i < boards.length; i++)
			boards[i] = "INIT";
		maps = new Vector<HashMap<String, Float>>();
		for (int i = 0; i < stageCnt; i++) 
			maps.add(new HashMap<String, Float>());
	}
	
	public float getMaxStrength(String board) throws IOException {
		update(board);
		HashMap<String, Float> map = maps.get(getIndex(board));
		Set<String> holeCards = map.keySet();
		float maxStrength = 0;
		for (Iterator<String> i = holeCards.iterator(); i.hasNext();) {
			float winRate = (riverHoleCardsCnt - map.get(i.next())) / riverHoleCardsCnt;
			if (maxStrength < winRate) 
				maxStrength = winRate;
		}
		return maxStrength;
	}

	public float getRank(String holeCards, String board) throws IOException {
		update(board);
		HashMap<String, Float> map = maps.get(getIndex(board));
		if (map.containsKey(holeCards))
			return map.get(holeCards);
		return -1;
	}

	public float getHandStength(String holeCards, String board) throws Exception {
		update(board);
		HashMap<String, Float> map = maps.get(getIndex(board));
		if (!map.containsKey(holeCards))
			return -1;
		return (riverHoleCardsCnt - map.get(holeCards)) / riverHoleCardsCnt;
	}

	private void update(String board) throws IOException {
		int index = getIndex(board);
		if (boards[index].equals(board)) 
			return;
		boards[index] = board;
		HashMap<String, Float> map = maps.get(index);
		map.clear();
		FileReader fileReader = new FileReader(getFilePath(board));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int lineCnt = getLineCnt(board);
		for (int i = 0; i < lineCnt; i++) {
			String line = bufferedReader.readLine();
			String holeCards = line.substring(0, 4);
			Float rank = Float.parseFloat(line.substring(5));
			map.put(holeCards, rank);
		}
		bufferedReader.close();
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

	private int getLineCnt(String board) {
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

	String HSDBPath;
	String preflopPath;
	String flopPath;
	String turnPath;
	String riverPath;
	String[] boards;
	Vector<HashMap<String, Float>> maps;
	private static final int preflopLineCnt = 1326;
	private static final int flopLineCnt = 1176;
	private static final int turnLineCnt = 1128;
	private static final int riverLineCnt = 1081;
	private static final int riverHoleCardsCnt = 990;
	private static final int stageCnt = 4;
}
