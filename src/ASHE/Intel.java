package ASHE;

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

	NodeBase[] prior() {
		if (record.size() == 0)
			return null;
		NodeBase[] priors = new NodeBase[record.size()];
		for (int i = 0; i < record.size(); i++)
			priors[i] = record.get(i);
		return priors;
	}

	NodeBase currentState() {
		return current;
	}

	NodeBase next(ActionBase action, TableInfo tableInfo) {
		int code = -Tools.getActionCode(action, tableInfo);
		for (int i = 0; i < current.children.size(); i++) {
			if (code == current.children.get(i).conditionCode)
				return current.children.get(i);
		}
		return null;
	}
	
	double getStateFreq(NodeBase node) {
		if (node == null)
			return 0;
		if (node.parent == null)
			return 1.0;
		if (node.parent.stats.frequency < 10)
			return node.stats.frequency / (1.0 + 0.9 * node.parent.stats.frequency);
		return node.stats.frequency * 1.0 / (1.0 + node.parent.stats.frequency);
	}

	int getBetCnt() {
		int betCnt = 0;
		NodeBase node = current;
		for (; node.parent != null; node = node.parent) {
			if (Math.abs(node.conditionCode) > 3)
				betCnt++;
		}
		betCnt += node.conditionCode < 2 ? 1 : 0;
		return betCnt;
	}

	boolean button() {
		NodeBase node = current;
		while (node.parent != null)
			node = node.parent;
		return node.conditionCode % 2 == 0;
	}

	public String toString() {
		String report = "<BEGIN: INTEL>\n";
		if (record.size() > 0) {
			report += "<BEGIN: PRIOR STAGE(S)>\n";
			for (int i = 0; i < record.size(); i++) {
				report += Tools.getStageName(i) + ":\n";
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

	NodeBase current;
	Vector<NodeBase> record;
}
