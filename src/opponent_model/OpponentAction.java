package opponent_model;

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
		return "O-" + InternalTools.describeActionCode(conditionCode);
	}

	@Override
	NodeBase getNewChild(ActionInfoBase actionInfo) {
		return new MyAction(-InternalTools.getActionCode(actionInfo), this);
	}

	@Override
	boolean match(ActionInfoBase actionInfo) {
		return conditionCode == InternalTools.getActionCode(actionInfo);
	}
		
}
