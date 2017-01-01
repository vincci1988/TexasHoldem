package ASHE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import holdem.ActionInfoBase;
import holdem.HandInfo;
import holdem.Result;
import holdem.Showdown;
import holdem.WinBeforeShowdown;

public class GameForest implements Statistician {

	public GameForest(int myID) {
		trees = new GameTree[gameTreeCnt];
		intel = new Intel();
		this.myID = myID;
		boards = new String[boardCnt];
		reset();
	}

	public GameForest(int myID, String file) throws IOException {
		trees = new GameTree[gameTreeCnt];
		intel = new Intel();
		this.myID = myID;
		boards = new String[boardCnt];
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (int i = 0; i < trees.length; i++)
			trees[i] = new GameTree(reader.readLine());
		reader.close();
		prepare();
	}

	// MUST be called for each new match/opponent
	public void reset() {
		for (int i = 0; i < trees.length; i++)
			trees[i] = new GameTree(i);
		prepare();
	}

	// MUST be called for each new game
	public void prepare() {
		stage = 0;
		position = -1;
		intel.reset();
		boards[0] = "";
		for (int i = 1; i < boards.length; i++)
			boards[i] = "UNKNOWN";
	}

	// MUST be called by "getAction(TableInfo)" to ensure correct position value
	public Intel getIntel() {
		if (position == -1) 
			position = 0;
		NodeBase current = trees[index()].getCurrent();
		if (current instanceof Root)
			current.stats.frequency++;
		intel.updateCurrent(current);
		return intel;
	}

	// MUST be called by "observe(ActionInfoBase)" to ensure correct position
	// value
	public void updateAction(ActionInfoBase actionInfo) {
		if (position == -1) 
			position = 1;
		if (actionInfo.playerID != myID && trees[index()].getCurrent() instanceof Root)
			trees[index()].getCurrent().stats.frequency++;
		if (boards[stage].equals("UNKNOWN"))
			boards[stage] = actionInfo.board;
		if (trees[index()].updateAction(actionInfo) && stage < 3) {
			intel.updateRecord(trees[index()].getCurrent());
			stage++;
		}
	}

	// MUST be called by "observe(Result) to ensure correct pointer value
	public void updateResult(Result result) throws Exception {
		for (; stage >= 0; stage--) {
			if (result instanceof WinBeforeShowdown)
				trees[index()].backtrackWBS(((WinBeforeShowdown) result).winnerID == myID);
			else if (result instanceof Showdown) {
				trees[index()].backtrackSD(
						evaluator.getHandStength(getOpponentHoleCards((Showdown) result), boards[stage]));
			}
		}
		for (int i = 0; i < 4; i++) {
			trees[position + 2 * i].refresh();
		}
	}

	public String display() {
		String res = "";
		for (int i = 0; i < trees.length; i++)
			res += trees[i].display() + "\n";
		return res;
	}

	public String toString() {
		String res = "";
		for (int i = 0; i < trees.length; i++)
			res += trees[i] + "\n";
		return res;
	}

	public void save(String path) throws IOException {
		PrintWriter writer = new PrintWriter(path);
		for (int i = 0; i < trees.length; i++)
			writer.println(trees[i]);
		writer.close();
	}
	
	public int getTotalNodeCnt() {
		int cnt = 0;
		for (int i = 0; i < trees.length; i++)
			 cnt += trees[i].nodeCnt();
		return cnt;
	}

	private String getOpponentHoleCards(Showdown result) {
		ArrayList<HandInfo> hands = ((Showdown) result).potResults.get(0).handInfos;
		for (int i = 0; i < hands.size(); i++) {
			if (hands.get(i).playerID != myID)
				return hands.get(i).holeCards;
		}
		return null;
	}

	private int index() {
		return position + 2 * stage;
	}

	private GameTree[] trees;
	private Intel intel;
	private int myID;
	private int stage;
	private int position; 
	private String[] boards;
	
	private static final int gameTreeCnt = 8;
	private static final int boardCnt = 4;
}
