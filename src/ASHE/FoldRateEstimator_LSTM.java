package ASHE;

import java.util.Stack;
import java.util.Vector;

import LSTMPlus.LSTMLayer;
import LSTMPlus.Util;
import holdem.ActionBase;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class FoldRateEstimator_LSTM extends EstimatorBase {
	FoldRateEstimator_LSTM(Ashe ashe) {
		super(ashe);
		iLayer = new LSTMLayer(iLayerInputDim, cellCnt);
		wLayer = new LSTMLayer(wLayerInputDim, cellCnt);
	}

	FoldRateEstimator_LSTM(Ashe ashe, double[] genome) throws Exception {
		super(ashe);
		iLayer = new LSTMLayer(iLayerInputDim, cellCnt,
				Util.head(genome, LSTMLayer.getGenomeLength(iLayerInputDim, cellCnt)));
		wLayer = new LSTMLayer(wLayerInputDim, cellCnt,
				Util.head(genome, LSTMLayer.getGenomeLength(wLayerInputDim, cellCnt)));
	}

	double[] getGenome() {
		return Util.concat(iLayer.getGenome(), wLayer.getGenome());
	}

	static int getGenomeLength() {
		return LSTMLayer.getGenomeLength(iLayerInputDim, cellCnt)
				+ LSTMLayer.getGenomeLength(wLayerInputDim, cellCnt);
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
		double potOdds = 0;
		if (action instanceof Raise) 
			potOdds = (((Raise) action).getAmt() - info.currentBet)
					/ (info.potSize + 2 * ((Raise) action).getAmt() - ashe.getMyBet() - info.currentBet);
		else 
			potOdds = (ashe.getMyBet() + ashe.getMyStack() - info.currentBet) / 2 / AsheParams.stk;
		iLayer.reset();
		wLayer.reset();
		double impression = 0;
		double weight = 0;
		for (int i = 0; i < trace.size(); i++) {
			for (node = trace.get(i); node != null; node = node.parent)
				nodeStk.push(node);
			for (; !nodeStk.isEmpty();) {
				node = nodeStk.pop();
				double[] input = getLSTMInput(node);
				impression = iLayer.activate(input)[0];
				weight = wLayer.activate(Util.tail(input, 1))[0];
			}
		}
		impression = (impression + 1) / 2;
		weight = (weight + 1) / 2;
		return impression * weight + potOdds * (1 - weight);
	}

	private double[] getLSTMInput(NodeBase node) {
		double[] input = new double[iLayerInputDim];
		input[0] = 1.0 * node.stats.oppFold / node.stats.frequency;
		input[1] = Util.tanh(0.1 * node.stats.frequency);
		for (int i = 0; i < input.length; i++)
			input[i] = 2 * input[i] - 1;
		return input;
	}
	
	static int iLayerInputDim = 2;
	static int wLayerInputDim = 1;
	static int cellCnt = 1;
	
	LSTMLayer iLayer;
	LSTMLayer wLayer;
}
