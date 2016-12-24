package opponent_model;

import java.util.Vector;

import holdem.ActionBase;
import holdem.TableInfo;

public class Intel {

	Intel() {
		record = new Vector<NodeStats>();
		reset();
	}

	void reset() {
		current = null;
		record.clear();
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

	void updateRecord(NodeStats stats) {
		record.add(stats);
	}

	NodeStats query(ActionBase action, TableInfo tableInfo) {
		int code = -InternalTools.getActionCode(action, tableInfo);
		for (int i = 0; i < current.children.size(); i++) {
			if (code == current.children.get(i).conditionCode)
				return current.children.get(i).stats;
		}
		return null;
	}

	private NodeBase current;
	private Vector<NodeStats> record;
}
