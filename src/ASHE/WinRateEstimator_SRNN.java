package ASHE;

import java.util.Stack;
import java.util.Vector;

import LSTMPlus.Util;
import holdem.ActionBase;
import holdem.AllIn;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class WinRateEstimator_SRNN extends EstimatorBase {

	WinRateEstimator_SRNN(Ashe ashe) {
		super(ashe);
		featureExtractor = new SRNN(FEiNetDim, FEwNetDim, featureCnt);
		adjuster = new SRNN(featureCnt, ADwNetDim, ADOutputDim);
	}

	WinRateEstimator_SRNN(Ashe ashe, double[] genome) throws Exception {
		super(ashe);
		featureExtractor = new SRNN(FEiNetDim, FEwNetDim, featureCnt,
				Util.head(genome, SRNN.getGenomeLength(FEiNetDim, FEwNetDim, featureCnt)));
		adjuster = new SRNN(featureCnt, ADwNetDim, ADOutputDim,
				Util.tail(genome, SRNN.getGenomeLength(featureCnt, ADwNetDim,  ADOutputDim)));
	}

	double[] getGenome() {
		return Util.concat(featureExtractor.getGenome(), adjuster.getGenome());
	}

	static int getGenomeLength() {
		return SRNN.getGenomeLength(FEiNetDim, FEwNetDim, featureCnt)
				+ SRNN.getGenomeLength(featureCnt, ADwNetDim, ADOutputDim);
	}

	@Override
	double estimate(TableInfo info, Intel intel, double handStrength, ActionBase action) throws Exception {
		if (action instanceof Fold)
			return 0.0;
		Stack<NodeBase> nodeStk = new Stack<NodeBase>();
		Vector<NodeBase> trace = new Vector<NodeBase>();
		trace.addAll(intel.record);
		NodeBase node = intel.next(action, info);
		if (node != null)
			trace.add(node);
		else 
			trace.add(intel.current);
		double adjustment = 0;
		adjuster.reset();
		for (int i = 0; i < trace.size(); i++) {
			for (node = trace.get(i); node != null; node = node.parent)
				nodeStk.push(node);
			double[] features = null;
			featureExtractor.reset();
			for (; !nodeStk.isEmpty();) {
				node = nodeStk.pop();
				features = featureExtractor.activate(getFEiNetInput(node), getFEwNetInput(node));
			}
			double[] wNetInput = new double[1];
			wNetInput[0] = 2 * (1.0 + i) / trace.size() - 1;
			adjustment = adjuster.activate(features, wNetInput)[0];
		}
		adjustment = (1 + adjustment) / 2;
		return Math.pow(adjustHandStrength(handStrength, action, info), 1 / adjustment);
	}

	private double[] getFEiNetInput(NodeBase node) {
		double[] input = new double[FEiNetDim];
		input[0] = 1.0 * node.stats.showdown / node.stats.frequency;
		input[1] = 1.0 * node.stats.myFold / node.stats.frequency;
		input[2] = node.stats.oSDH_M;
		input[3] = node.stats.showdown < 2 ? 0
				: Math.sqrt(node.stats.oSDH_SoS / (node.stats.showdown - 1)
						- node.stats.showdown * Math.pow(input[2], 2) / (node.stats.showdown - 1)) / 0.5;
		for (int i = 0; i < input.length; i++)
			input[i] = 2 * input[i] - 1;
		return input;
	}

	private double[] getFEwNetInput(NodeBase node) {
		double[] input = new double[FEwNetDim];
		input[0] = Util.tanh(0.1 * node.stats.frequency) * 2 - 1;
		input[1] = Util.tanh(0.2 * node.stats.showdown) * 2 - 1;
		return input;
	}

	private double adjustHandStrength(double handStrength, ActionBase action, TableInfo info) {
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
		return handStrength;
	}

	static final int FEiNetDim = 5;
	static final int FEwNetDim = 2;
	static final int featureCnt = 3;
	static final int ADwNetDim = 1;
	static final int ADOutputDim = 1;

	SRNN featureExtractor;
	SRNN adjuster;
}
