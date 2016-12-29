package ashe;

import holdem.AllIn;
import holdem.Call;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

class RuleBasedAdviser extends AdviserBase {

	RuleBasedAdviser(Ashe player) {
		super(player);
	}

	@Override
	double evaluate(TableInfo info, double handStrength, int i) {
		double myTotalBet = (info.potSize - info.currentBet - player.getMyBet()) / 2 + player.getMyBet();
		if (actions.get(i) instanceof Fold) 
			return -myTotalBet;
		double pw = getWinProb(i, handStrength);
		double pf = getFoldProb(i);
		double extraChips = 0;
		if (actions.get(i) instanceof Call) {
			if (info.board.length() == 10)
				pf = 0;
			extraChips = info.currentBet - player.getMyBet();
		}
		if (actions.get(i) instanceof Raise) {
			extraChips = ((Raise) actions.get(i)).getAmt() - player.getMyBet();
		}
		if (actions.get(i) instanceof AllIn) {
			extraChips = player.getMyStack();
		}
		double pl = 1 - pw;
		double ps = 1 - pf;
		return ps * (pw - pl) * (myTotalBet + extraChips) + pf * (info.potSize - myTotalBet);
	}
	
	@Override
	double[][] extract(double[][] nodes) {
		if (nodes == null)
			return null;
		double[][] ans = new double[nodes.length][];
		for (int i = 0; i < ans.length; i++)
			ans[i] = extract(nodes[i]);
		return ans;
	}

	@Override
	double[] extract(double[] node) {
		double[] ans = new double[featureDim];
		double adjustment = AdviserKit.smooth(node[0], 10);
		ans[0] = (node[1] + adjustment) / (node[0] + 3 * adjustment);
		adjustment = AdviserKit.smooth(node[3], 5);
		ans[1] = getAdjustedMean(node[4], node[3], adjustment);
		ans[2] = getAdjustedStdDev(ans[1], node[5], node[3], adjustment);
		return ans;
	}

	private double getFoldProb(int i) {
		return merge(i, 0);
	}

	private double getWinProb(int i, double handStrength) {
		double mean = merge(i, 1);
		double dev = merge(i, 2);
		return AdviserKit.prob(mean, dev, handStrength);
	}

	private double merge(int move, int index) {
		double share = 0.5 / (1 + (priors == null ? 0 : priors.length));
		double ans = 0.5 * moves[move][index] + share * present[index];
		if (priors != null) {
			for (int i = 0; i < priors.length; i++) {
				ans += share * priors[i][index];
			}
		}
		return ans;
	}

	private static double getAdjustedMean(double mean, double showdown, double adjustment) {
		double smoothMean = 0.65;
		return (smoothMean * 2 * adjustment  + mean * showdown) / (showdown + 2 * adjustment);
	}

	private static double getAdjustedStdDev(double adjustedMean, double sum_of_square, double showdown,
			double adjustment) {
		double n = showdown + 2 * adjustment;
		double smoothSoS = 0.89;
		return Math.sqrt((sum_of_square + smoothSoS * adjustment) / (n - 1) - n * Math.pow(adjustedMean, 2) / (n - 1));
	}

	static final int featureDim = 3;
}
