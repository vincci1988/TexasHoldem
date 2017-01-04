package ASHE;

import holdem.ActionBase;
import holdem.AllIn;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class WinRateEstimator_RuleBased extends EstimatorBase {

	WinRateEstimator_RuleBased(Ashe ashe) {
		super(ashe);
	}
	
	double estimate(TableInfo info, Intel intel, double handStrength, ActionBase action) {
		if (action instanceof Fold)
			return 0.0;
		if (action instanceof Raise) {
			double potOdds = 1.0 * (((Raise) action).getAmt() - info.currentBet)
					/ (info.potSize + 2 * ((Raise) action).getAmt() - info.currentBet - ashe.getMyBet());
			if (handStrength < potOdds)
				return 0;
			handStrength = 1.0 - (1.0 - handStrength) / (1.0 - potOdds);
		}
		if (action instanceof AllIn && ashe.getMyStack() + ashe.getMyBet() > info.currentBet) {
			double potOdds = (ashe.getMyStack() + ashe.getMyBet() - info.currentBet) / AsheParams.stk / 2;
			if (handStrength < potOdds)
				return 0;
			handStrength = 1.0 - (1.0 - handStrength) / (1.0 - potOdds);
		}
		NodeBase node = intel.record.firstElement();
		while (node.parent != null)
			node = node.parent;
		double showdownProb = 1.0 * (1.0 + node.stats.showdown + 0.5 * node.stats.myFold)
				/ (1.0 + node.stats.frequency);
		return Math.pow(handStrength, 1 / showdownProb);
	}

}
