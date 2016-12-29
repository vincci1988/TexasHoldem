package ashe;

import LSTMPlus.FFNetwork;
import LSTMPlus.Util;
import evolvable_players.Evolvable;
import evolvable_players.GenomeBase;
import holdem.AllIn;
import holdem.Call;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class NNAdviser extends AdviserBase implements Evolvable {

	NNAdviser(Ashe player) {
		super(player);
		networks = new FFNetwork[mergerCnt];
		for (int i = 0; i < networks.length; i++)
			networks[i] = new FFNetwork(mergerInputDim, mergerHiddenNodeCnt, mergerOutputDim);
	}

	NNAdviser(Ashe player, double[] genes) throws Exception {
		super(player);
		if (genes.length != getGenomeLength())
			throw new Exception("ashe.NNAdviser(Ashe,double[]): Invalid genome length.");
		networks = new FFNetwork[mergerCnt];
		int mergerGenomeLength = FFNetwork.getGenomeLength(mergerInputDim, mergerHiddenNodeCnt, mergerOutputDim);
		for (int i = 0; i < mergerCnt; i++) {
			double[] mergerGenome = Util.subArray(genes, i * mergerGenomeLength, mergerGenomeLength);
			networks[i] = new FFNetwork(mergerInputDim, mergerHiddenNodeCnt, mergerOutputDim, mergerGenome);
		}
	}

	@Override
	public GenomeBase getGenome() {
		double[] genome = null;
		for (int i = 0; i < networks.length; i++)
			genome = Util.concat(genome, networks[i].getGenome());
		return new AsheGenome(genome);
	}

	public static int getGenomeLength() {
		return mergerCnt * FFNetwork.getGenomeLength(mergerInputDim, mergerHiddenNodeCnt, mergerOutputDim);
	}

	@Override
	double evaluate(TableInfo info, double handStrength, int actionIndex) throws Exception {
		double myTotalBet = (info.potSize - info.currentBet - player.getMyBet()) / 2 + player.getMyBet();
		if (actions.get(actionIndex) instanceof Fold)
			return -myTotalBet;
		double pw = getWinProb(actionIndex, handStrength);
		double pf = getFoldProb(actionIndex);
		double extraChips = 0;
		if (actions.get(actionIndex) instanceof Call) {
			if (info.board.length() == 10)
				pf = 0;
			extraChips = info.currentBet - player.getMyBet();
		}
		if (actions.get(actionIndex) instanceof Raise) {
			extraChips = ((Raise) actions.get(actionIndex)).getAmt() - player.getMyBet();
		}
		if (actions.get(actionIndex) instanceof AllIn) {
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

	private double getFoldProb(int i) throws Exception {
		return (networks[0].activate(getInput(i, 0))[0] + 1) / 2;
	}

	private double getWinProb(int i, double handStrength) throws Exception {
		double mean = (networks[1].activate(getInput(i, 1))[0] + 1) / 2;
		double dev = (networks[2].activate(getInput(i, 2))[0] + 1) / 2;
		return AdviserKit.prob(mean, dev, handStrength);
	}

	private double[] getInput(int move, int index) throws Exception {
		double[] input = new double[5];
		for (int i = 0; i < 3; i++) {
			input[i] = priors != null && i < priors.length ? priors[i][index] : 0;
		}
		input[3] = moves[move][index];
		input[4] = present[index];
		return input;
	}

	private static double getAdjustedMean(double mean, double showdown, double adjustment) {
		double smoothMean = 0.65;
		return (smoothMean * 2 * adjustment + mean * showdown) / (showdown + 2 * adjustment);
	}

	private static double getAdjustedStdDev(double adjustedMean, double sum_of_square, double showdown,
			double adjustment) {
		double n = showdown + 2 * adjustment;
		double smoothSoS = 0.89;
		return Math.sqrt((sum_of_square + smoothSoS * adjustment) / (n - 1) - n * Math.pow(adjustedMean, 2) / (n - 1));
	}

	static final int featureDim = 3;
	static final int mergerCnt = 3;
	static final int mergerInputDim = 5;
	static final int mergerHiddenNodeCnt = 7;
	static final int mergerOutputDim = 1;

	FFNetwork[] networks;
}
