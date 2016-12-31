package ashe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import holdem.ActionInfoBase;
import holdem.FoldInfo;
import holdem.HandInfo;
import holdem.Result;
import holdem.Showdown;
import holdem.WinBeforeShowdown;

public class GameForest implements Statistician {

	public GameForest(int myID) {
		trees = new GameTree[8];
		intel = new Intel();
		this.myID = myID;
		boards = new String[4];
		boards[0] = "";
		reset();
	}

	public GameForest(int myID, String file) throws IOException {
		trees = new GameTree[8];
		intel = new Intel();
		this.myID = myID;
		boards = new String[4];
		boards[0] = "";
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
		myPreviousBet = 0;
		myCurrentBet = 0;
		stage = 0;
		position = -1;
		intel.reset();
		for (int i = 0; i < boards.length; i++)
			boards[i] = "UNKNOWN";
	}

	// MUST be called by "getAction(TableInfo)" to ensure correct position value
	public Intel getIntel() {
		if (position == -1) {
			position = 0;
			myCurrentBet = Params.BB / 2;
		}
		NodeBase current = trees[index()].getCurrent();
		if (current instanceof Root)
			current.stats.frequency++;
		intel.updateCurrent(current);
		return intel;
	}

	// MUST be called by "observe(ActionInfoBase)" to ensure correct position
	// value
	public void updateAction(ActionInfoBase actionInfo) {
		if (position == -1) {
			position = 1;
			myCurrentBet = Params.BB;
		}
		if (actionInfo.playerID != myID && trees[index()].getCurrent() instanceof Root)
			trees[index()].getCurrent().stats.frequency++;
		if (boards[stage].equals("UNKNOWN"))
			boards[stage] = actionInfo.board;
		if (actionInfo.playerID == myID && !(actionInfo instanceof FoldInfo))
			myCurrentBet = actionInfo.amt;
		if (trees[index()].updateAction(actionInfo) && stage < 3) {
			intel.updateRecord(trees[index()].getCurrent());
			myPreviousBet += myCurrentBet;
			myCurrentBet = 0;
			stage++;
		}
	}

	// MUST be called by "observe(Result) to ensure correct pointer value
	public void updateResult(Result result) throws Exception {
		double normalizedReward = getNormalizedReward(result);
		for (; stage >= 0; stage--) {
			if (result instanceof WinBeforeShowdown)
				trees[index()].backtrackWBS(normalizedReward);
			else if (result instanceof Showdown)
				trees[index()].backtrackSD(normalizedReward,
						evaluator.getHandStength(getOpponentHoleCards((Showdown) result), boards[stage], 1));
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

	private int getMyTotalBet() {
		return myPreviousBet + myCurrentBet;
	}

	private double getNormalizedReward(Result result) {
		if (result instanceof Showdown) {
			if (((Showdown) result).potResults.get(0).winnerCnt == 1) {
				if (won(result))
					return 1.0 * (((Showdown) result).potResults.get(0).potSize - getMyTotalBet()) / Params.BB;
				return -1.0 * getMyTotalBet() / Params.BB;
			}
			else
				return 1.0 * getMyTotalBet() / Params.BB;
		} else if (result instanceof WinBeforeShowdown) {
			if (won(result))
				return 1.0 * (((WinBeforeShowdown) result).potSize - getMyTotalBet()) / Params.BB;
			return -1.0 * getMyTotalBet() / Params.BB;
		}
		return 0;
	}

	private boolean won(Result result) {
		if (result instanceof WinBeforeShowdown)
			return ((WinBeforeShowdown) result).winnerID == myID;
		if (result instanceof Showdown)
			return ((Showdown) result).potResults.get(0).handInfos.get(0).playerID == myID;
		return false;
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
	private int myPreviousBet;
	private int myCurrentBet;
	private int stage; // -1:=UNKNOWN, 0:=PREFLOP, 1:=FLOP, 2:=TURN, 3:=RIVER
	private int position; // -1:=UNKNOWN, 0:=BUTTON, 1:=BB
	private String[] boards;
}
