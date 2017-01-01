package ASHE;

import java.util.Vector;

import holdem.ActionInfoBase;

abstract class NodeBase implements Comparable<NodeBase> {
	
	NodeBase(int conditionCode, NodeBase parent) {
		this.conditionCode = conditionCode;
		stats = new NodeStats();
		this.parent = parent;
		children = new Vector<NodeBase>();
	}	
	
	NodeBase(int conditionCode, NodeBase parent, String statsStr) {
		this.conditionCode = conditionCode;
		stats = new NodeStats(statsStr);
		this.parent = parent;
		children = new Vector<NodeBase>();
	}
	
	public int compareTo(NodeBase other) {
		if (this.stats.frequency == other.stats.frequency) return 0;
		return this.stats.frequency > other.stats.frequency ? -1 : 1;
	}
	
	public String toString() {
		return conditionCode + "(" + stats + ")";
	}
	
	String display() {
		String res = describeNode();
		res += "(" + stats + ")";
		return res;
	}
	
	abstract String describeNode();
	
	NodeBase next(ActionInfoBase actionInfo) {
		for (int i = 0; i < children.size(); i++)
			if (children.get(i).match(actionInfo))
				return children.get(i);
		NodeBase nextNode = getNewChild(actionInfo);
		children.add(nextNode);
		return nextNode;
	}
			
	abstract NodeBase getNewChild(ActionInfoBase actionInfo);
	abstract boolean match(ActionInfoBase actionInfo);
	
	final int conditionCode;
	NodeStats stats;
	NodeBase parent;
	Vector<NodeBase> children;
}
