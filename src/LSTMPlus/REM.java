package LSTMPlus;

public class REM implements Evolvable {

	public REM(int inputDim) {
		this.inputDim = inputDim;
		output = 0.0;
		memory = new double[inputDim + 1];
		weights = new double[inputDim + 1];
		Util.gaussianInit(weights, 0.0, GaussianInitVar);
	}

	public REM(int inputDim, double[] genome) {
		this.inputDim = inputDim;
		output = 0.0;
		memory = new double[inputDim + 1];
		weights = Util.head(genome, genome.length);
	}

	@Override
	public double[] getGenome() {
		return weights;
	}

	public static int getGenomeLength(int inputDim) {
		return inputDim + 1;
	}

	public double activate(double[] x) throws Exception {
		if (x.length != inputDim)
			throw new Exception("LSTMPlus.REM.activate(double[]): Invalid input length.");
		double[] bias = { 1.0 };
		double[] temp = Util.concat(bias, Util.add(Util.multiply(Util.tail(memory, inputDim), forgetRate), x));
		output = (Util.dotProduct(temp, weights));
		return output;
	}
	
	public void memorize(double[] x) {
		double[] bias = { 1.0 };
		memory = Util.concat(bias, Util.add(Util.multiply(Util.tail(memory, inputDim), forgetRate), x));
	}

	public void BP(double error) {
		//double[] negativedw = Util.multiply(memory, error);
		//weights = Util.add(weights, Util.multiply(negativedw, learningRate));
		reset();
	}

	public void reset() {
		output = 0;
		memory = new double[inputDim + 1];
	}

	public int inputDim;
	public double output;
	private double[] memory;
	private double[] weights;

	public static double forgetRate = 0;
	public static double learningRate = 0.01;
	public static final double GaussianInitVar = 0.01;
}
