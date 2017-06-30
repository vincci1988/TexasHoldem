package simple_dynamic_players;

import java.util.Vector;

import evolvable_players.Statistician;
import holdem.ActionInfoBase;
import holdem.AllInInfo;
import holdem.CallInfo;
import holdem.CheckInfo;
import holdem.FoldInfo;
import holdem.RaiseInfo;

public class OppStats implements Statistician {

	public OppStats() {
		foldCnts = new double[5];
		stageCnts = new double[4];
		myMoveCnts = new double[4][];
		for (int i = 0; i < myMoveCnts.length; i++)
			myMoveCnts[i] = new double[6]; // fold check call .5s 1s all-in
		boards = new String[4];
		boards[0] = "";
		trace = new Vector<Vector<Integer>>();
		for (int i = 0; i < 4; i++)
			trace.add(new Vector<Integer>());
		reset();
	}

	public void reset() {
		for (int i = 0; i < foldCnts.length; i++)
			foldCnts[i] = 0;
		for (int i = 0; i < stageCnts.length; i++)
			stageCnts[i] = 1;
		for (int i = 0; i < myMoveCnts.length; i++) {
			for (int j = 0; j < myMoveCnts[i].length; j++)
				myMoveCnts[i][j] = 1;
		}
		for (int i = 1; i < boards.length; i++)
			boards[i] = null;
		currentStage = 0;
		gameCnt = 0;
	}

	public void newGame() {
		for (int i = 0; i < foldCnts.length; i++)
			foldCnts[i] *= decayRate;
		for (int i = 0; i < stageCnts.length; i++)
			if (stageCnts[i] > 1)
				stageCnts[i] *= decayRate;
		for (int i = 0; i < myMoveCnts.length; i++) {
			for (int j = 0; j < myMoveCnts[i].length; j++)
				if (myMoveCnts[i][j] > 1)
					myMoveCnts[i][j] *= decayRate;
		}
		for (int i = 1; i < boards.length; i++)
			boards[i] = null;
		for (int i = 0; i < trace.size(); i++)
			trace.get(i).clear();
		currentStage = 0;
		stageCnts[0]++;
		gameCnt++;
	}

	public void updateOpponentAction(ActionInfoBase actionInfo) {
		checkStage(actionInfo.board);
		if (!(actionInfo instanceof FoldInfo))
			trace.get(currentStage).add(getAggrLvl(actionInfo.aggression));
		if (actionInfo instanceof FoldInfo) {
			foldCnts[currentStage]++;
			foldCnts[4]++;
		}
	}

	public void updateMyAction(ActionInfoBase actionInfo) {
		checkStage(actionInfo.board);
		trace.get(currentStage).add(-getAggrLvl(actionInfo.aggression));
		if (actionInfo instanceof FoldInfo)
			myMoveCnts[currentStage][0]++;
		else if (actionInfo instanceof CheckInfo)
			myMoveCnts[currentStage][1]++;
		else if (actionInfo instanceof CallInfo)
			myMoveCnts[currentStage][2]++;
		else if (actionInfo instanceof RaiseInfo) {
			if (getAggrLvl(actionInfo.aggression) == 2)
				myMoveCnts[currentStage][3]++;
			else
				myMoveCnts[currentStage][4]++;
		} else if (actionInfo instanceof AllInInfo)
			myMoveCnts[currentStage][5]++;
	}

	public void updateShowDown(String holeCards) throws Exception {
		double[] strengths = new double[4];
		for (int i = 0; i < boards.length; i++) {
			if (boards[i] != null)
				strengths[i] = Statistician.evaluator.getHandStength(holeCards, boards[i], 1);
		}
	}

	public double getFoldRate() {
		return foldCnts[4] / stageCnts[0];
	}

	public double getFoldRate(int stage) {
		return foldCnts[stage] / stageCnts[stage];
	}

	public double getFoldRate(String board) {
		return getFoldRate(getStage(board));
	}

	public static int getStage(String board) {
		if (board.length() == 0)
			return 0;
		if (board.length() == 6)
			return 1;
		if (board.length() == 8)
			return 2;
		return 3;
	}

	public void report() {
		System.out.println("GAME CNT: " + gameCnt);
		System.out.print("FOLD RTR: ");
		for (int i = 0; i < 4; i++)
			System.out.print(getFoldRate(i) + " ");
		System.out.println(getFoldRate());
		for (int i = 0; i < trace.size(); i++) {
			for (int j = 0; j < trace.get(i).size(); j++)
				System.out.print(trace.get(i).get(j) + " ");
			System.out.println();
		}
		for (int i = 0; i < myMoveCnts.length; i++) {
			for (int j = 0; j < myMoveCnts[i].length; j++)
				System.out.print(myMoveCnts[i][j] + " ");
			System.out.println();
		}
	}

	private int getAggrLvl(double trace) {
		if (trace >= 0.70)
			return 5;
		if (trace >= 0.55)
			return 4;
		if (trace >= 0.40)
			return 3;
		if (trace >= 0.25)
			return 2;
		if (trace >= 0.10)
			return 1;
		return 0;
	}

	private void checkStage(String board) {
		if (getStage(board) != currentStage) {
			currentStage++;
			stageCnts[currentStage]++;
			boards[currentStage] = board;
		}
	}

	public int gameCnt;
	public double[] stageCnts;
	public double[] foldCnts;
	public double[][] myMoveCnts; //checkBB, .5s_init, 1s_init, all-in_init
								  

	private Vector<Vector<Integer>> trace;
	private String[] boards;
	private int currentStage;
	private final double decayRate = 0.95;

}
