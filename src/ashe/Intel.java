package ashe;

import java.util.Vector;

import holdem.ActionBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class Intel {

	Intel() {
		record = new Vector<NodeBase>();
		reset();
	}

	void reset() {
		current = null;
		record.clear();
	}

	/*
	 * Note: a Prior is a terminal state of an early stage. State Vector Format
	 * (double) priors[0][]: preflop, priors[1][]: flop, priors[2][]: turn
	 * priors[x][0]: occurrences count priors[x][1]: opponent fold count
	 * priors[x][2]: my fold count priors[x][3]: showdown count Note:
	 * priors[x][0] == priors[x][1] + priors[x][2] + priors[x][3] priors[x][4]:
	 * mean of opponent hand strengths in showdowns (n = priors[x][3])
	 * priors[x][5]: sum-of-square of opponent hand strengths in showdowns
	 * priors[x][6]: cumulative reward in Big Blinds
	 */
	public double[][] prior() {
		if (record.size() == 0)
			return null;
		double[][] priors = new double[record.size()][];
		for (int i = 0; i < record.size(); i++)
			priors[i] = record.get(i).stats.toArray();
		return priors;
	}

	/*
	 * Returns the state vector of corresponding action given current table
	 * state Returns a zero vector if no record of the action can be found
	 */
	public double[] evaluate(ActionBase action, TableInfo tableInfo) {
		NodeBase node = query(action, tableInfo);
		if (node != null)
			return node.stats.toArray();
		return InternalTools.zeros();
	}

	/*
	 * Returns the state vector of current state Returns a zero vector if this
	 * is the first visit of the current state
	 */
	public double[] currentState() {
		return current.stats.toArray();
	}

	/*
	 * Returns the total number of raises by both players in this round of
	 * betting
	 */
	public int getBetCnt() {
		int betCnt = 0;
		NodeBase node = current;
		for (; node.parent != null; node = node.parent) {
			if (Math.abs(node.conditionCode) > 3)
				betCnt++;
		}
		betCnt += node.conditionCode < 2 ? 1 : 0;
		return betCnt;
	}

	public boolean button() {
		NodeBase node = current;
		while (node.parent != null)
			node = node.parent;
		return node.conditionCode % 2 == 0;
	}

	public double esimateWinRate(double handStrength) {
		NodeBase gameRoot = record.firstElement();
		while (gameRoot.parent != null)
			gameRoot = gameRoot.parent;
		double showdownProb = 1.0 * (1.0 + gameRoot.stats.showdown + 0.5 * gameRoot.stats.myFold)
				/ (1.0 + gameRoot.stats.frequency);
		return Math.pow(handStrength, 1 / showdownProb);
	}

	public double getStateFreq() {
		return getStateFreq(current);
	}

	public double evaluate(double winRate, ActionBase action, TableInfo info) {
		double fp = getFoldProb(action, info);
		if (action instanceof Raise)
			return fp * (info.potSize + info.currentBet - action.getBet()) / 2 + (1 - fp) * (2 * winRate - 1)
					* ((info.potSize - info.currentBet - action.getBet()) / 2 + ((Raise) action).getAmt());
		if (action instanceof AllIn)
			return fp * (info.potSize + info.currentBet - action.getBet()) / 2
					+ (1 - fp) * (2 * winRate - 1) * Params.stk;
		if (action instanceof Call)
			return (2 * winRate - 1) * (info.potSize + info.currentBet - action.getBet()) / 2;
		if (action instanceof Fold)
			return -(info.potSize + action.getBet() - info.currentBet) / 2;
		if (action instanceof Check) {
			if (button())
				return (2 * winRate - 1) * info.potSize / 2;
			else {
				double expectedRaise = 0;
				double raiseProb = 0;
				NodeBase checkNode = this.query(action, info);
				if (checkNode != null) {
					for (int i = 0; i < checkNode.children.size(); i++) {
						if (checkNode.children.get(i).conditionCode > 3) {
							raiseProb += checkNode.children.get(i).stats.frequency;
							expectedRaise += checkNode.children.get(i).stats.frequency * InternalTools
									.getRaiseAmtToPot(checkNode.children.get(i).conditionCode, info.potSize);
						}
					}
					raiseProb /= checkNode.stats.frequency;
					expectedRaise /= checkNode.stats.frequency;
				}
				return (1 - raiseProb) * (2 * winRate - 1) * info.potSize / 2
						+ raiseProb * (winRate > expectedRaise / (expectedRaise + info.potSize)
								? (2 * winRate - 1) * info.potSize * (0.5 + expectedRaise) : -info.potSize / 2);
			}
		}
		return 0;
	}

	public double getFoldEquity(double handStrength, Raise raise, TableInfo info) {
		double fp = getFoldProb(raise, info);
		double winRate = Math.pow(handStrength, 1.0 + fp);
		return fp * (info.potSize + info.currentBet - raise.getBet()) / 2 + (1 - fp)
				* ((2 * winRate - 1) * ((info.potSize - info.currentBet - raise.getBet()) / 2 + raise.getAmt()));
	}

	public double getFoldProb(ActionBase raise, TableInfo tableInfo) {
		NodeBase raiseNode = query(raise, tableInfo);
		if (raiseNode == null)
			return 0;
		NodeBase fd = null;
		for (int i = 0; i < raiseNode.children.size(); i++) {
			if (raiseNode.children.get(i).conditionCode == 1) {
				fd = raiseNode.children.get(i);
				break;
			}
		}
		return getStateFreq(fd);
	}

	public String toString() {
		String report = "<BEGIN: INTEL>\n";
		if (record.size() > 0) {
			report += "<BEGIN: PRIOR STAGE(S)>\n";
			for (int i = 0; i < record.size(); i++) {
				report += InternalTools.getStageName(i) + ":\n";
				report += record.get(i).display() + "\n";
			}
			report += "<END: PRIOR STAGE(S)>\n";
		}
		report += "<BEGIN: CURRENT STATE>\n";
		report += "<" + current.describeNode() + ">\n";
		report += current.stats.display() + "\n";
		report += "<END: CURRENT STATE>\n";
		report += "<BEGIN: NEXT STATE(S)>\n";
		if (current.children.size() == 0)
			report += "NONE RECORDED\n";
		else {
			for (int i = 0; i < current.children.size(); i++) {
				report += "<" + current.children.get(i).describeNode() + ">\n";
				report += current.children.get(i).stats.display() + "\n";
			}
		}
		report += "<END: NEXT STATE(S)>\n";
		report += "<END: INTEL>";
		return report;
	}

	void updateCurrent(NodeBase current) {
		this.current = current;
	}

	void updateRecord(NodeBase leaf) {
		record.add(leaf);
	}

	private NodeBase query(ActionBase action, TableInfo tableInfo) {
		int code = -InternalTools.getActionCode(action, tableInfo);
		for (int i = 0; i < current.children.size(); i++) {
			if (code == current.children.get(i).conditionCode)
				return current.children.get(i);
		}
		return null;
	}

	private double getStateFreq(NodeBase node) {
		if (node == null)
			return 0;
		if (node.parent == null)
			return 1.0;
		if (node.parent.stats.frequency < 10)
			return node.stats.frequency / (1.0 + 0.9 * node.parent.stats.frequency);
		return node.stats.frequency * 1.0 / (1.0 + node.parent.stats.frequency);
	}

	private NodeBase current;
	private Vector<NodeBase> record;
}
