package holdem;

public class ActionStats {

	public void reset() {
		raiseAmt = new double[4];
		for (int i = 0; i < raiseAmt.length; i++)
			raiseAmt[i] = 0;
		raiseCnt = new double[4];
		for (int i = 0; i < raiseCnt.length; i++)
			raiseCnt[i] = 0;
		actionCnt = new double[4];
		for (int i = 0; i < actionCnt.length; i++)
			actionCnt[i] = 0;
		foldCnt = 0;
		opponentRaised = 0;
	}

	public void update(int myID, ActionInfoBase actionInfo) {
		if (actionInfo.playerID == myID) {
			actionCnt[getIndex(actionInfo.board)]++;
			if (actionInfo instanceof RaiseInfo) {
				raiseAmt[getIndex(actionInfo.board)] += actionInfo.aia;
				raiseCnt[getIndex(actionInfo.board)]++;
			}
			if (actionInfo instanceof FoldInfo) {
				foldCnt++;
			}
		} else {
			if (actionInfo instanceof RaiseInfo)
				opponentRaised++;
		}
	}

	public String toString() {
		String result = "Action Stats:\n";
		result += "Avg Raise Amt (preflop) = " + raiseAmt[0] / (raiseCnt[0] + 1) + " " + raiseCnt[0] + "\n";
		result += "Avg Raise Amt (flop) = " + raiseAmt[1] / (raiseCnt[1] + 1) + " " + raiseCnt[1] + "\n";
		result += "Avg Raise Amt (turn) = " + raiseAmt[2] / (raiseCnt[2] + 1) + " " + raiseCnt[2] + "\n";
		result += "Avg Raise Amt (river) = " + raiseAmt[3] / (raiseCnt[3] + 1) + " " + raiseCnt[3] + "\n";
		result += "Raise Frequency (preflop) = " + raiseCnt[0] / (actionCnt[0] + 1) + " " + actionCnt[0] + "\n";
		result += "Raise Frequency (flop) = " + raiseCnt[1] / (actionCnt[1] + 1) + " " + actionCnt[1] + "\n";
		result += "Raise Frequency (turn) = " + raiseCnt[2] / (actionCnt[2] + 1) + " " + actionCnt[2] + "\n";
		result += "Raise Frequency (river) = " + raiseCnt[3] / (actionCnt[3] + 1) + " " + actionCnt[3] + "\n";
		result += "Fold Frequency (FF) = " + foldCnt / (opponentRaised + 1) + " " + foldCnt + " " + opponentRaised
				+ "\n";
		return result;
	}

	private int getIndex(String board) {
		if (board.length() == 0)
			return 0;
		if (board.length() == 6)
			return 1;
		if (board.length() == 8)
			return 2;
		return 3;
	}

	public double[] raiseAmt;
	public double[] raiseCnt;
	public double foldCnt;
	public double opponentRaised;
	public double[] actionCnt;
}
