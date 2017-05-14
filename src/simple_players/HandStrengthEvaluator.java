package simple_players;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class HandStrengthEvaluator extends HashMap<String, HandData> {

	public HandStrengthEvaluator(String HSDBPath) {
		this.HSDBPath = HSDBPath;
		String separator = System.getProperty("os.name").contains("Windows") ? "\\" : "/";
		preflopPath = HSDBPath + separator + "preflop" + separator + "preflop.txt";
		flopPath = HSDBPath + separator + "flop" + separator;
		turnPath = HSDBPath + separator + "turn" + separator;
		riverPath = HSDBPath + separator + "river" + separator;
		board = "INIT";
		data = new Vector<HandData>();
	}

	public float getRankStrength(String holeCards, String board) throws IOException {
		update(board);
		if (!this.containsKey(holeCards))
			return -1;
		String c1 = holeCards.substring(0, 2);
		String c2 = holeCards.substring(2, 4);
		int absRank = (int)this.get(holeCards).absRank;
		float rank = 0;
		for (int i = 0; i < absRank; i++) {
			String hand = data.get(i).hand;
			if (!hand.contains(c1) && !hand.contains(c2))
				rank++;
		}
		if (board.length() == 0)
			return 1 - rank / preflopHandNum;
		if (board.length() == 6)
			return 1 - rank / flopHandNum;
		if (board.length() == 8)
			return 1 - rank / turnHandNum;
		if (board.length() == 10)
			return 1 - rank / riverHandNum;
		return rank;
	}

	public float getHandStength(String holeCards, String board, int opponentCnt) throws Exception {
		update(board);
		if (!this.containsKey(holeCards))
			return -1;
		if (opponentCnt == 0)
			return 0;
		return (riverHoleCardsCnt - get(holeCards).expRank) / riverHoleCardsCnt;
	}

	private void update(String board) throws IOException {
		if (board.compareTo(this.board) == 0)
			return;
		this.board = board;
		this.clear();
		data.clear();
		FileReader fileReader = new FileReader(getFilePath(board));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int lineCnt = getLineCnt();
		float current = -1, absRank = -1;
		for (int i = 0; i < lineCnt; i++) {
			String line = bufferedReader.readLine();
			String holeCards = line.substring(0, 4);
			Float expRank = Float.parseFloat(line.substring(5));
			if (expRank > current) {
				current = expRank;
				absRank = i;
			}
			HandData datum = new HandData(holeCards, absRank, expRank);
			this.put(holeCards, datum);
			data.add(datum);
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

	String HSDBPath;
	String preflopPath;
	String flopPath;
	String turnPath;
	String riverPath;
	String board;
	Vector<HandData> data;
	private static final int preflopLineCnt = 1326;
	private static final int flopLineCnt = 1176;
	private static final int turnLineCnt = 1128;
	private static final int riverLineCnt = 1081;
	private static final int riverHoleCardsCnt = 990;
	private static final int preflopHandNum = 1225;
	private static final int flopHandNum = 1081;
	private static final int turnHandNum = 1035;
	private static final int riverHandNum = 990;
	private static final long serialVersionUID = 1L;
}
