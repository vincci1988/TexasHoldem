package ashe;

import java.util.Collections;

import holdem.ActionInfoBase;

class GameTree {

	GameTree(int rootCondition) {
		root = new Root(rootCondition);
		current = root;
	}

	GameTree(String serial) {
		root = (Root) parse(serial, null);
		current = root;
	}

	public String toString() {
		return toString(root);
	}

	String display() {
		return display(root);
	}

	int nodeCnt() {
		return nodeCnt(root);
	}

	NodeBase getCurrent() {
		return current;
	}

	boolean updateAction(ActionInfoBase actionInfo) {
		current = current.next(actionInfo);
		current.stats.frequency++;
		return isLeaf(current);
	}

	void backtrackWBS(double normalizedReward) {
		if (current != root) {
			for (; current != null; current = current.parent) {
				if (normalizedReward > 0)
					current.stats.oppFold++;
				else
					current.stats.myFold++;
				current.stats.cReward += normalizedReward;
				current.stats.significance += Math.abs(normalizedReward);
			}
			current = root;
		}
	}

	void backtrackSD(double normalizedReward, double opponentHandStrength) {
		if (current != root) {
			for (; current != null; current = current.parent) {
				current.stats.showdown++;
				current.stats.oSDH_M = (opponentHandStrength + current.stats.oSDH_M * (current.stats.showdown - 1))
						/ current.stats.showdown;
				current.stats.oSDH_SoS += Math.pow(opponentHandStrength, 2);
				current.stats.cReward += normalizedReward;
				current.stats.significance += Math.abs(normalizedReward);
			}
			current = root;
		}
	}

	void refresh() {
		refresh(root);
	}

	private boolean isLeaf(NodeBase node) {
		if (Math.abs(node.conditionCode) == 2 && !isFirstAction(node))
			return true;
		if (Math.abs(node.conditionCode) == 3 && !isFirstAction(node))
			return true;
		return false;
	}

	private String display(NodeBase node) {
		String res = "[" + node.display() + ":";
		for (int i = 0; i < node.children.size(); i++)
			res += display(node.children.get(i));
		res += "]";
		return res;
	}

	private String toString(NodeBase node) {
		String res = "[" + node;
		for (int i = 0; i < node.children.size(); i++)
			res += toString(node.children.get(i));
		res += "]";
		return res;
	}

	private int nodeCnt(NodeBase node) {
		int cnt = 1;
		for (int i = 0; i < node.children.size(); i++)
			cnt += nodeCnt(node.children.get(i));
		return cnt;
	}

	private void refresh(NodeBase node) {
		node.stats.significance -= Params.decay;
		if (node.stats.significance < 0)
			node.stats.significance = 0;
		if (!(node instanceof Root) && node.stats.significance == 0) {
				node.parent.children.remove(node);
		} else {
			Collections.sort(node.children);
			for (int i = 0; i < node.children.size(); i++)
				refresh(node.children.get(i));
		}
	}

	private boolean isFirstAction(NodeBase node) {
		return node.parent instanceof Root;
	}

	private static NodeBase parse(String tree, NodeBase parent) {
		int conditionCode = Integer.parseInt(tree.substring(1, tree.indexOf('(')));
		NodeBase node = null;
		String statsStr = tree.substring(tree.indexOf('(') + 1, tree.indexOf(')'));
		if (parent == null)
			node = new Root(conditionCode, statsStr);
		else
			node = conditionCode > 0 ? new OpponentAction(conditionCode, parent, statsStr)
					: new MyAction(conditionCode, parent, statsStr);
		String subtrees = tree.substring(tree.indexOf(')') + 1);
		int leftCnt = 0, start = 0, end = 0;
		for (int i = 0; leftCnt >= 0; i++) {
			if (subtrees.charAt(i) == '[') {
				leftCnt++;
				if (leftCnt == 1)
					start = i;
			} else if (subtrees.charAt(i) == ']') {
				leftCnt--;
				if (leftCnt == 0) {
					end = i + 1;
					node.children.add(parse(subtrees.substring(start, end), node));
				}
			}
		}
		return node;
	}

	Root root;
	private NodeBase current;
}
