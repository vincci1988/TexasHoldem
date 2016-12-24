package advanced_players;

import holdem.ActionInfoBase;
import holdem.FoldInfo;
import holdem.Result;

public class OpponentStats {

	public OpponentStats() {
		FR = new double[4];
		AA = new double[4];
		actionCnt = new double[4];
		roundCnt = new double[4];
	}

	public void reset() {
		for (int i = 0; i < FR.length; i++) {
			FR[i] = 0;
			AA[i] = 0;
			actionCnt[i] = 0;
			roundCnt[i] = 0;
		}
	}

	public double getFoldRate(String board) {
		int index = getIndex(board);
		double FRSum = 0;
		for (int i = index; i < FR.length; i++)
			FRSum += FR[i];
		return roundCnt[index] < 10 ? defaultFR[index] : FRSum / roundCnt[index];
	}

	public double getFoldRate() {
		return roundCnt[0] == 0 ? 0 : (sum(FR)) / roundCnt[0];
	}

	public double getHandRange() {
		double HR = 1.0;
		for (int i = 0; i < FR.length; i++)
			HR *= roundCnt[i] < 10 ? (1 - defaultFR[i]) : (1 - FR[i] / roundCnt[i]);
		return HR > 0.01 ? HR : 0.01;
	}

	public double getAggression(String board) {
		int index = getIndex(board);
		return actionCnt[index] < 10 ? defaultAA[index] : AA[index] / actionCnt[index];
	}

	public void gameUpdate(Result result) {
		int index = getIndex(result.board);
		for (int i = 0; i <= index; i++)
			roundCnt[i]++;
	}

	public void actionUpdate(ActionInfoBase actionInfo) {
		int index = getIndex(actionInfo.board);
		actionCnt[index]++;
		if (actionInfo instanceof FoldInfo)
			FR[index]++;
		AA[index] += actionInfo.AIA;
	}

	public String toString() {
		String report = "<BEGIN: OPPONENT STATS>\n";
		report += "FOLDING RATE: \n";
		report += "\tPREFLOP: " + (actionCnt[0] == 0 ? 0 : FR[0] / roundCnt[0]) + "\n";
		report += "\tFLOP: " + (roundCnt[1] == 0 ? 0 : FR[1] / roundCnt[1]) + "\n";
		report += "\tTURN: " + (roundCnt[2] == 0 ? 0 : FR[2] / roundCnt[2]) + "\n";
		report += "\tRIVER: " + (roundCnt[3] == 0 ? 0 : FR[3] / roundCnt[3]) + "\n";
		report += "\tTOTAL: " + getFoldRate() + "\n";
		report += "AGGRESSION: \n";
		report += "\tPREFLOP: " + (actionCnt[0] == 0 ? 0 : AA[0] / actionCnt[0]) + "\n";
		report += "\tFLOP: " + (actionCnt[1] == 0 ? 0 : AA[1] / actionCnt[1]) + "\n";
		report += "\tTURN: " + (actionCnt[2] == 0 ? 0 : AA[2] / actionCnt[2]) + "\n";
		report += "\tRIVER: " + (actionCnt[3] == 0 ? 0 : AA[3] / actionCnt[3]) + "\n";
		report += "<END: OPPONENT STATS>\n";
		return report;
	}

	private double sum(double[] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++)
			sum += A[i];
		return sum;
	}
	
	public double[] getStats(String board) {
		double[] stats = new double[3];
		stats[0] = getHandRange();
		stats[1] = getFoldRate(board);
		stats[2] = getAggression(board);
		return stats;
	}

	private int getIndex(String board) {
		switch (board.length()) {
		case 0:
			return 0;
		case 6:
			return 1;
		case 8:
			return 2;
		case 10:
			return 3;
		}
		return -1;
	}

	double[] FR;
	double[] AA;
	double[] actionCnt;
	double[] roundCnt;
	final double[] defaultFR = { 0.33, 0.45, 0.15, 0.15 };
	final double[] defaultAA = { 0.85, 0.25, 0.25, 0.15 };
}
