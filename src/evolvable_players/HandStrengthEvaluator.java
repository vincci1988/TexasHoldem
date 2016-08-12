package evolvable_players;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class HandStrengthEvaluator extends HashMap<String, Float> {

	public HandStrengthEvaluator(String HSDBPath) {
		this.HSDBPath = HSDBPath;
		String separator = System.getProperty("os.name").contains("Windows") ? "\\" : "/";
		preflopPath = HSDBPath + separator + "preflop" + separator + "preflop.txt";
		flopPath = HSDBPath + separator + "flop" + separator;
		turnPath = HSDBPath + separator + "turn" + separator;
		riverPath = HSDBPath + separator + "river" + separator;
		board = "INIT";
	}

	public float getRank(String holeCards, String board) throws IOException {
		update(board);
		if (this.containsKey(holeCards))
			return this.get(holeCards);
		return -1;
	}

	public float getHandStength(String holeCards, String board, int opponentCnt) throws Exception {
		update(board);
		if (!this.containsKey(holeCards))
			return -1;
		if (opponentCnt == 0)
			return 0;
		return (riverHoleCardsCnt - get(holeCards)) / riverHoleCardsCnt;
	}

	private void update(String board) throws IOException {
		if (board.compareTo(this.board) == 0)
			return;
		this.board = board;
		this.clear();
		FileReader fileReader = new FileReader(getFilePath(board));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int lineCnt = getLineCnt();
		for (int i = 0; i < lineCnt; i++) {
			String line = bufferedReader.readLine();
			String holeCards = line.substring(0, 4);
			Float rank = Float.parseFloat(line.substring(5));
			this.put(holeCards, rank);
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
	private static final int preflopLineCnt = 1326;
	private static final int flopLineCnt = 1176;
	private static final int turnLineCnt = 1128;
	private static final int riverLineCnt = 1081;
	private static final int riverHoleCardsCnt = 990;
	private static final long serialVersionUID = 1L;
}
