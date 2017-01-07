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
		double potOdds = action instanceof Raise
				? 1.0 * (((Raise) action).getAmt() - info.currentBet)
						/ (info.potSize + 2 * ((Raise) action).getAmt() - ashe.getMyBet() - info.currentBet)
				: 1.0 * (ashe.getMyBet() + ashe.getMyStack() - info.currentBet) / 2 / AsheParams.stk;
		double smooth = potOdds * (1.0 + 0.7 * info.board.length() / 10.0);
		if (raiseNode == null) 
			return smooth;
		double fr = 1.0 * raiseNode.stats.oppFold / raiseNode.stats.frequency;
		if (raiseNode.stats.frequency < 10)
			return smooth * (1.0 - raiseNode.stats.frequency / 10.0) + fr * raiseNode.stats.frequency / 10.0;
		return fr;
	}

}
