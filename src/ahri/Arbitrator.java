package ahri;

import LSTMPlus.FFNetwork;
import LSTMPlus.Util;

public class Arbitrator {

	Arbitrator() {
		ChiefAnalyzer = new FFNetwork(CAInputDim, CAHiddenCnt, CAOutputDim);
		StateAnalyzer = new FFNetwork(SAInputDim, SAHiddenCnt, SAOutputDim);
	}

	Arbitrator(double[] genome) throws Exception {
		int CAGenomeLength = FFNetwork.getGenomeLength(CAInputDim, CAHiddenCnt, CAOutputDim);
		ChiefAnalyzer = new FFNetwork(CAInputDim, CAHiddenCnt, CAOutputDim, Util.head(genome, CAGenomeLength));
		int SAGenomeLength = FFNetwork.getGenomeLength(SAInputDim, SAHiddenCnt, SAOutputDim);
		StateAnalyzer = new FFNetwork(SAInputDim, SAHiddenCnt, SAOutputDim, Util.tail(genome, SAGenomeLength));
	}

	public static int getGenomeLength() {
		return FFNetwork.getGenomeLength(CAInputDim, CAHiddenCnt, CAOutputDim)
				+ FFNetwork.getGenomeLength(SAInputDim, SAHiddenCnt, SAOutputDim);
	}

	public double[] getGenome() {
		return Util.concat(ChiefAnalyzer.getGenome(), StateAnalyzer.getGenome());
	}
	
	boolean exploitable(double[][] moves) {
		for (int i = 0; i < moves.length; i++) {
			if (moves[i][0] < 10)
				return false;
		}
		return true;
	}

	boolean exploitable(double[][] moves, double[] current, double[][] priors) throws Exception {
		for (int i = 0; i < moves.length; i++) {
			if (!exploitable(getInput(moves[i], current, priors)))
				return false;
		}
		return true;
	}
	
	private boolean exploitable(double[] input) throws Exception {
		double[] CAInput = null;
		for (int i = 0; i < SACnt; i++)
			CAInput = Util.concat(CAInput, StateAnalyzer.activate(Util.subArray(input, i * SAInputDim, SAInputDim)));
		return ChiefAnalyzer.activate(CAInput)[0] > 0;
	}
	
	private double[] getInput(double[] move, double[] current, double[][] priors) {
		double[] input = new double[SACnt * SAInputDim];
		input[0] = move[0];
		input[1] = move[3];
		input[2] = current[0];
		input[3] = current[3];
		for (int i = 0; i < maxPriorCnt; i++) {
			if (priors != null && i < priors.length) {
				input[4 + i * SAInputDim] = priors[priors.length - 1 -i][0];
				input[4 + i * SAInputDim + 1] = priors[priors.length - 1 -i][3];
			}
		}	
		return normalize(input);
	}

	private double[] normalize(double[] x) {
		double[] y = new double[x.length];
		for (int i = 0; i < y.length; i++)
			y[i] = Math.tanh(0.1 * x[i]) * 2 - 1;
		return y;
	}

	static final int SAInputDim = 2;
	static final int SAHiddenCnt = 2;
	static final int SAOutputDim = 2;
	static final int SACnt = 5;
	static final int CAInputDim = SACnt * SAOutputDim;
	static final int CAHiddenCnt = SACnt;
	static final int CAOutputDim = 1;
	static final int maxPriorCnt = 3;
	
	FFNetwork ChiefAnalyzer;
	FFNetwork StateAnalyzer;
}
