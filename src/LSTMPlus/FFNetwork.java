package LSTMPlus;

public class FFNetwork implements Evolvable {

	public FFNetwork(int inputDim, int hiddenNodeNum, int outputDim) {
		this.inputDim = inputDim;
		this.hiddenNodeNum = hiddenNodeNum;
		this.outputDim = outputDim;
		H = new double[hiddenNodeNum][inputDim];
		O = new double[outputDim][hiddenNodeNum];
		bh = new double[hiddenNodeNum];
		bo = new double[outputDim];
		Util.gaussianInit(H, 0, 1.0 / inputDim);
		Util.gaussianInit(O, 0, 1.0 / hiddenNodeNum);
		Util.gaussianInit(bh, 0, 1.0 / hiddenNodeNum);
		Util.gaussianInit(bo, 0, 1.0 / outputDim);
		outputs = new double[outputDim];
	}

	public FFNetwork(int inputDim, int hiddenNodeNum, int outputDim, double[] genome) throws Exception {
		if (genome.length != getGenomeLength(inputDim, hiddenNodeNum, outputDim))
			throw new Exception("LSTMPlus.FFNetwork.FFNetwork(int,int,int,double[]): Invalid genome length.");
		this.inputDim = inputDim;
		this.hiddenNodeNum = hiddenNodeNum;
		this.outputDim = outputDim;
		H = new double[hiddenNodeNum][inputDim];
		O = new double[outputDim][hiddenNodeNum];
		bh = new double[hiddenNodeNum];
		bo = new double[outputDim];
		int start = 0;
		start = Util.initByGenome(H, genome, start);
		start = Util.initByGenome(O, genome, start);
		start = Util.initByGenome(bh, genome, start);
		Util.initByGenome(bo, genome, start);
		outputs = new double[outputDim];
	}

	public double[] getGenome() {
		double[] genome = Util.concat(Util.serialize(H), Util.serialize(O));
		genome = Util.concat(genome, bh);
		genome = Util.concat(genome, bo);
		return genome;
	}

	public static int getGenomeLength(int inputDim, int hiddenNodeNum, int outputDim) {
		return inputDim * hiddenNodeNum + outputDim * hiddenNodeNum + hiddenNodeNum + outputDim;
	}

	public double[] activate(double[] x) throws Exception {
		if (x.length != inputDim)
			throw new Exception("LSTMPlus.FFNetwork.activate(double[]): Invalid input length.");
		outputs = Util.tanh(Util.add(Util.multiply(O, Util.tanh(Util.add(Util.multiply(H, x), bh))), bo));
		return outputs;
	}

	private double[][] H;
	private double[][] O;
	private double[] bh;
	private double[] bo;
	private double[] outputs;

	public final int inputDim;
	public final int hiddenNodeNum;
	public final int outputDim;
}
