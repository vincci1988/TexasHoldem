package LSTMPlus;

public class REMQ {

	public REMQ(int actionDim, int stateDim) {
		this.actionDim = actionDim;
		this.stateDim = stateDim;
		actionWeights = new REM[actionDim + 1];
		for (int i = 0; i < actionWeights.length; i++)
			actionWeights[i] = new REM(stateDim);
		memory = new double[actionDim + 1];
	}

	public REMQ(int actionDim, int stateDim, double[] genome) throws Exception {
		if (getGenomeLength(actionDim, stateDim) != genome.length)
			throw new Exception("LSTMPlus.REMQ.REMQ(int,int,double[]): Invalid genome length.");
		int REMGenomeLength = REM.getGenomeLength(stateDim);
		this.actionDim = actionDim;
		this.stateDim = stateDim;
		actionWeights = new REM[actionDim + 1];
		for (int i = 0; i < actionWeights.length; i++)
			actionWeights[i] = new REM(stateDim, Util.subArray(genome, REMGenomeLength * i, REMGenomeLength));
		memory = new double[actionDim + 1];
	}

	public double[] getGenome() {
		double[] genome = null;
		for (int i = 0; i < actionWeights.length; i++)
			genome = Util.concat(genome, actionWeights[i].getGenome());
		return genome;
	}

	public static int getGenomeLength(int actionDim, int stateDim) {
		return (actionDim + 1) * REM.getGenomeLength(stateDim);
	}

	public double activate(double[] action, double[] state) throws Exception {
		double[] weights = new double[actionWeights.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = actionWeights[i].activate(state);
		}
		double[] bias = { 1 };
		output = Util.tanh(Util.dotProduct(Util.concat(action, bias), weights));
		return output;
	}
	
	public void memorize(double[] action, double[] state) {
		double[] bias = {1};
		memory = Util.add(Util.multiply(memory, forgetRate), Util.concat(action, bias));
		memory[memory.length - 1] = 1.0;
		for (int i = 0; i < actionWeights.length; i++)
			actionWeights[i].memorize(state);
	}
	
	public void reset() {
		for (int i = 0; i < actionWeights.length; i++)
			actionWeights[i].reset();
		memory = new double[actionDim + 1];
		output = 0;
	}
	
	public void BP(double reward) {
		for (int i = 0; i < actionWeights.length; i++) {
			double error = (reward - output) * (1 - output * output) * memory[i];
			actionWeights[i].BP(error);
		}
		memory = new double[actionDim + 1];
		output = 0;
	}

	REM[] actionWeights;
	double[] memory;
	double output;

	int actionDim;
	int stateDim;
	public static final double forgetRate = 0.1;
}
