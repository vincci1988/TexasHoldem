package ASHE;

import holdem.ActionInfoBase;

class Root extends NodeBase {

	/*
	 * 	Condition Code Table 
	 * 	code 	position 	stage 
	 * 	0 		button 		preflop 
	 * 	1 		BB 			preflop 
	 * 	2		button 		flop 
	 * 	3 		BB 			flop 
	 * 	4 		button 		turn 
	 * 	5 		BB 			turn 
	 * 	6 		button 		river 
	 * 	7 		BB 			river
	 */
	Root(int conditionCode) {
		super(conditionCode, null);
	}
	
	Root(int conditionCode, String stats) {
		super(conditionCode, null, stats);
	}

	@Override	
	String describeNode() {
		return Tools.describeRootCode(conditionCode);
	}

	@Override
	NodeBase getNewChild(ActionInfoBase actionInfo) {
		if ((conditionCode == 0) || (conditionCode % 2 == 1 && conditionCode > 1))
			return new MyAction(-Tools.getActionCode(actionInfo), this);
		return new OpponentAction(Tools.getActionCode(actionInfo), this);
	}

	@Override
	boolean match(ActionInfoBase actionInfo) {
		// SHOULD "NEVER" BE CALLED!!
		return false;
	}


}
