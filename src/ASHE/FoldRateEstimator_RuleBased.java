package ASHE;

import holdem.ActionBase;
import holdem.Raise;
import holdem.TableInfo;

public class FoldRateEstimator_RuleBased extends EstimatorBase {

	FoldRateEstimator_RuleBased(Ashe ashe) {
		super(ashe);
	}

	@Override
	double estimate(TableInfo info, Intel intel, double handStrength, ActionBase action) throws Exception {
		NodeBase raiseNode = intel.next(action, info);
		if (raiseNode == null) {
			if (action instanceof Raise) 
				return (((Raise) action).getAmt() - info.currentBet)
						/ (info.potSize + 2 * ((Raise) action).getAmt() - ashe.getMyBet() - info.currentBet);
			return (ashe.getMyBet() + ashe.getMyStack() - info.currentBet) / 2 / AsheParams.stk;
		}
		NodeBase fd = null;
		for (int i = 0; i < raiseNode.children.size(); i++) {
			if (raiseNode.children.get(i).conditionCode == 1) {
				fd = raiseNode.children.get(i);
				break;
			}
		}
		return intel.getStateFreq(fd);
	}

}
