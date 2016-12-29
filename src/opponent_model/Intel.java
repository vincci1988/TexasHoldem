package opponent_model;

import java.util.Vector;

import holdem.ActionBase;
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
	 * Note: a Prior is a terminal state of an early stage.
	 * State Vector Format (double)
	 * priors[0][]: preflop, priors[1][]: flop, priors[2][]: turn
	 * priors[x][0]: occurrences count
	 * priors[x][1]: opponent fold count
	 * priors[x][2]: my fold count
	 * priors[x][3]: showdown count
	 * 		Note: priors[x][0] == priors[x][1] + priors[x][2] + priors[x][3]
	 * priors[x][4]: mean of opponent hand strengths in showdowns (n = priors[x][3])
	 * priors[x][5]: sum-of-square of opponent hand strengths in showdowns
	 * priors[x][6]: cumulative reward in Big Blinds
	 */
	public double[][] prior() {
		if (record.size() == 0) return null;
		double[][] priors = new double[record.size()][];
		for (int i = 0; i < record.size(); i++) 
			priors[i] = record.get(i).stats.toArray();
		return priors;
	}
	
	/*
	 * Returns the state vector of corresponding action given current table state
	 * Returns a zero vector if no record of the action can be found
	 */
	public double[] evaluate(ActionBase action, TableInfo tableInfo) {
		NodeBase node = query(action, tableInfo);
		if (node != null)
			return node.stats.toArray();
		return InternalTools.zeros();
	}
	
	/*
	 * Returns the state vector of current state
	 * Returns a zero vector if this is the first visit of the current state
	 */
	public double[] currentState() {
		return current.stats.toArray();
	}
	
	/*
	 * Returns the total number of raises by both players in this round of betting
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

	NodeBase query(ActionBase action, TableInfo tableInfo) {
		int code = -InternalTools.getActionCode(action, tableInfo);
		for (int i = 0; i < current.children.size(); i++) {
			if (code == current.children.get(i).conditionCode)
				return current.children.get(i);
		}
		return null;
	}

	private NodeBase current;
	private Vector<NodeBase> record;
}
