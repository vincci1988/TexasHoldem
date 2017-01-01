package ASHE;

import holdem.ActionInfoBase;

class MyAction extends NodeBase {
	
	MyAction(int conditionCode, NodeBase parent) {
		super(conditionCode, parent);
	}
	
	MyAction(int conditionCode, NodeBase parent, String statsStr) {
		super(conditionCode, parent, statsStr);
	}
	
	@Override
	String describeNode() {
		return "A-" + Tools.describeActionCode(-conditionCode);
	}

	@Override
	NodeBase getNewChild(ActionInfoBase actionInfo) {
		return new OpponentAction(Tools.getActionCode(actionInfo), this);
	}

	@Override
	boolean match(ActionInfoBase actionInfo) {
		return conditionCode == -Tools.getActionCode(actionInfo);
	}

}
