package ASHE;

import holdem.ActionInfoBase;

class OpponentAction extends NodeBase {

	OpponentAction(int conditionCode, NodeBase parent) {
		super(conditionCode, parent);
	}
	
	OpponentAction(int conditionCode, NodeBase parent, String statsStr) {
		super(conditionCode, parent, statsStr);
	}
	
	@Override
	String describeNode() {
		return "O-" + Tools.describeActionCode(conditionCode);
	}

	@Override
	NodeBase getNewChild(ActionInfoBase actionInfo) {
		return new MyAction(-Tools.getActionCode(actionInfo), this);
	}

	@Override
	boolean match(ActionInfoBase actionInfo) {
		return conditionCode == Tools.getActionCode(actionInfo);
	}
		
}
