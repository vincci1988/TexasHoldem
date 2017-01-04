package ASHE;

import java.util.Stack;
import java.util.Vector;

import LSTMPlus.Util;
import holdem.ActionBase;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class FoldRateEstimator_SRNN extends EstimatorBase {

	FoldRateEstimator_SRNN(Ashe ashe) {
		super(ashe);
		featureExtractor = new SRNN(FEiNetDim, FEwNetDim, featureCnt);
		estimator = new SRNN(EstInputDim, EstwNetDim, EstOutputDim);
	}

	FoldRateEstimator_SRNN(Ashe ashe, double[] genome) throws Exception {
		super(ashe);
		featureExtractor = new SRNN(FEiNetDim, FEwNetDim, featureCnt,
				Util.head(genome, SRNN.getGenomeLength(FEiNetDim, FEwNetDim, featureCnt)));
		estimator = new SRNN(EstInputDim, EstwNetDim, EstOutputDim,
				Util.tail(genome, SRNN.getGenomeLength(EstInputDim, EstwNetDim, EstOutputDim)));
	}

	double[] getGenome() {
		return Util.concat(featureExtractor.getGenome(), estimator.getGenome());
	}

	static int getGenomeLength() {
		return SRNN.getGenomeLength(FEiNetDim, FEwNetDim, featureCnt)
				+ SRNN.getGenomeLength(EstInputDim, EstwNetDim, EstOutputDim);
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
		double foldProb = 0;
		double[] potOdds = new double[1];
		if (action instanceof Raise) 
			potOdds[0] = (((Raise) action).getAmt() - info.currentBet)
					/ (info.potSize + 2 * ((Raise) action).getAmt() - ashe.getMyBet() - info.currentBet);
		else 
			potOdds[0] = (ashe.getMyBet() + ashe.getMyStack() - info.currentBet) / 2 / AsheParams.stk;
		potOdds[0] = potOdds[0] * 4 - 1.0;
		estimator.reset();
		for (int i = 0; i < trace.size(); i++) {
			for (node = trace.get(i); node != null; node = node.parent)
				nodeStk.push(node);
			double[] features = null;
			featureExtractor.reset();
			double[] wNetInput = new double[1];
			wNetInput[0] = 2 * (1.0 + i) / trace.size() - 1;
			for (; !nodeStk.isEmpty();) {
				node = nodeStk.pop();
				features = featureExtractor.activate(getFEiNetInput(node), getFEwNetInput(node));
			}
			foldProb = estimator.activate(Util.concat(features, potOdds), wNetInput)[0];
		}
		foldProb = (1 + foldProb) / 2;
		return foldProb;
	}

	private double[] getFEiNetInput(NodeBase node) {
		double[] input = new double[FEiNetDim];
		input[0] = 1.0 * node.stats.oppFold / node.stats.frequency;
		input[1] = 1.0 * node.stats.myFold / node.stats.frequency;
		for (int i = 0; i < input.length; i++)
			input[i] = 2 * input[i] - 1;
		return input;
	}

	private double[] getFEwNetInput(NodeBase node) {
		double[] input = new double[FEwNetDim];
		input[0] = Util.tanh(0.1 * node.stats.frequency) * 2 - 1;
		return input;
	}

	static final int FEiNetDim = 2;
	static final int FEwNetDim = 1;
	static final int featureCnt = 3;
	static final int EstInputDim = 4;
	static final int EstwNetDim = 1;
	static final int EstOutputDim = 1;

	SRNN featureExtractor;
	SRNN estimator;
}
