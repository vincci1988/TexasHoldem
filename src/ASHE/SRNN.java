package ASHE;

import LSTMPlus.FFNetwork;
import LSTMPlus.Util;

class SRNN {
	
	SRNN(int inputDim, int outputDim) {
		this.iNetInputDim = inputDim;
		this.wNetInputDim = inputDim;
		this.outputDim = outputDim;
		constructByRandom();
	}

	SRNN(int iNetInputDim, int wNetInputDim, int outputDim) {
		this.iNetInputDim = iNetInputDim;
		this.wNetInputDim = wNetInputDim;
		this.outputDim = outputDim;
		constructByRandom();
	}
	
	SRNN(int inputDim, int outputDim, double[] genome) throws Exception {
		this.iNetInputDim = inputDim;
		this.wNetInputDim = inputDim;
		this.outputDim = outputDim;
		constructByGenome(genome);
	}

	SRNN(int iNetInputDim, int wNetInputDim, int outputDim, double[] genome) throws Exception {
		this.iNetInputDim = iNetInputDim;
		this.wNetInputDim = wNetInputDim;
		this.outputDim = outputDim;
		constructByGenome(genome);
	}
	
	void reset() {
		yPrev = Util.concat(null, y0);
	}

	double[] getGenome() {
		double[] genome = Util.concat(iNet.getGenome(), wNet.getGenome());
		genome = Util.concat(genome, y0);
		return genome;
	}

	static int getGenomeLength(int iNetInputDim, int wNetInputDim, int outputDim) {
		return FFNetwork.getGenomeLength(iNetInputDim, iNetInputDim, outputDim)
				+ FFNetwork.getGenomeLength(wNetInputDim, wNetInputDim, outputDim);
	}
	
	double[] activate(double[] input) throws Exception {
		return activate(input, input);
	}
	
	double[] activate(double[] iNetInput, double[] wNetInput) throws Exception {
		double[] update = iNet.activate(iNetInput);
		double[] weights = wNet.activate(wNetInput);
		double[] ans = new double[outputDim];
		for (int i = 0; i < outputDim; i++) { 
			weights[i] = (1 + weights[i]) / 2;
			ans[i] = yPrev[i] * (1 - weights[i]) + weights[i] * update[i];
		}
		yPrev = Util.concat(null, ans);
		return ans;
	}
	
	private void constructByRandom() {
		iNet = new FFNetwork(iNetInputDim, iNetInputDim, outputDim);
		wNet = new FFNetwork(wNetInputDim, wNetInputDim, outputDim);
		y0 = new double[outputDim];
		Util.gaussianInit(y0, 0, 0.25);
		yPrev = Util.concat(null, y0);
	}
	
	private void constructByGenome(double[] genome) throws Exception {
		int iNetGenomeLength = FFNetwork.getGenomeLength(iNetInputDim, iNetInputDim, outputDim);
		iNet = new FFNetwork(iNetInputDim, iNetInputDim, outputDim,
				Util.head(genome, iNetGenomeLength));
		int wNetGenomeLength = FFNetwork.getGenomeLength(wNetInputDim, wNetInputDim, outputDim);
		wNet = new FFNetwork(wNetInputDim, wNetInputDim, outputDim,
				Util.subArray(genome, iNetGenomeLength, wNetGenomeLength));
		y0 = Util.tail(genome, outputDim);
		yPrev = Util.concat(null, y0);
	}

	FFNetwork iNet;
	FFNetwork wNet;
	double[] y0;
	double[] yPrev;
	int iNetInputDim;
	int wNetInputDim;
	int outputDim;
}
