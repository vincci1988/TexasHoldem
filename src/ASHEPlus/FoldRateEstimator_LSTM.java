package ASHEPlus;

import java.util.Vector;

import LSTMPlus.FFNetwork;
import LSTMPlus.LSTMLayer;
import LSTMPlus.Util;
import holdem.ActionBase;
import holdem.Fold;
import holdem.Raise;
import holdem.TableInfo;

public class FoldRateEstimator_LSTM extends EstimatorBase {
	FoldRateEstimator_LSTM(AshePlus ashe) {
		super(ashe);
		iLayer = new LSTMLayer(iLayerInputDim, cellCnt);
		aNet = new FFNetwork(aNetInputDim, aNetHiddenNodeCnt, aNetOutputDim);
	}

	FoldRateEstimator_LSTM(AshePlus ashe, double[] genome) throws Exception {
		super(ashe);
		iLayer = new LSTMLayer(iLayerInputDim, cellCnt,
				Util.head(genome, LSTMLayer.getGenomeLength(iLayerInputDim, cellCnt)));
		aNet = new FFNetwork(aNetInputDim, aNetHiddenNodeCnt, aNetOutputDim,
				Util.tail(genome, FFNetwork.getGenomeLength(aNetInputDim, aNetHiddenNodeCnt, aNetOutputDim)));
	}

	double[] getGenome() {
		return Util.concat(iLayer.getGenome(), aNet.getGenome());
	}

	static int getGenomeLength() {
		return LSTMLayer.getGenomeLength(iLayerInputDim, cellCnt)
				+ FFNetwork.getGenomeLength(aNetInputDim, aNetHiddenNodeCnt, aNetOutputDim);
	}

	@Override
	double estimate(TableInfo info, Intel intel, double handStrength, ActionBase action) throws Exception {
		if (action instanceof Fold)
			return 0.0;
		Vector<NodeBase> trace = new Vector<NodeBase>();
		trace.addAll(intel.record);
		NodeBase node = intel.next(action, info);
		if (node != null)
			trace.add(node);
		else
			trace.add(intel.current);
		double[] potOdds = new double[1];
		if (action instanceof Raise)
			potOdds[0] = 1.0 * (((Raise) action).getAmt() - info.currentBet)
					/ (info.potSize + 2 * ((Raise) action).getAmt() - ashe.getMyBet() - info.currentBet);
		else
			potOdds[0] = 1.0 * (ashe.getMyBet() + ashe.getMyStack() - info.currentBet) / 2 / Params.stk;
		double fr_base = smooth(node, potOdds[0], info);
		iLayer.reset();
		double[] impression = null;
		for (int i = 0; i < trace.size(); i++) {
			double influence = 0;
			if (i == trace.size() - 1)
				influence = trace.lastElement() == intel.current ? 0.5 : 1;
			else
				influence = Math.pow(0.5, trace.size() - i);
			double[] input = getLSTMInput(trace.get(i), influence);
			impression = iLayer.activate(input);
		}
		impression = Util.concat(impression, potOdds);
		double adjustment = aNet.activate(impression)[0];
		adjustment = (adjustment + 1) / 2;
		return Math.pow(fr_base, adjustment);
	}
	
	private double smooth(NodeBase node, double potOdds, TableInfo info) {
		double smooth = potOdds * (1 + info.board.length() / 10.0);
		if (node == null)
			return smooth;
		double sampleRate = 1.0 * node.stats.oppFold / node.stats.frequency;
		if (node != null && node.stats.frequency > 10) 
			return sampleRate;	
		double weight = node.stats.frequency / 10.0;
		return smooth * (1 - weight) + weight * sampleRate;
	}

	private double[] getLSTMInput(NodeBase node, double distance) {
		double[] input = new double[iLayerInputDim];
		input[0] = 1.0 * node.stats.oppFold / node.stats.frequency;
		input[1] = Util.tanh(0.1 * node.stats.frequency);
		input[2] = distance;
		for (input[3] = 0; node.parent != null; node = node.parent)
			if (node.conditionCode > 3)
				input[3]++;
		input[3] = Math.tanh(input[3]);
		for (int i = 0; i < input.length; i++)
			input[i] = 2 * input[i] - 1;
		return input;
	}

	static int iLayerInputDim = 4;
	static int cellCnt = 9;
	static int aNetInputDim = cellCnt + 1;
	static int aNetHiddenNodeCnt = 7;
	static int aNetOutputDim = 1;

	LSTMLayer iLayer;
	FFNetwork aNet;
}
