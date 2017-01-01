package ashe_rulebased;

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
		return "A-" + InternalTools.describeActionCode(-conditionCode);
	}

	@Override
	NodeBase getNewChild(ActionInfoBase actionInfo) {
		return new OpponentAction(InternalTools.getActionCode(actionInfo), this);
	}

	@Override
	boolean match(ActionInfoBase actionInfo) {
		return conditionCode == -InternalTools.getActionCode(actionInfo);
	}

}
