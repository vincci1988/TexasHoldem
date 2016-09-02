package LSTMPlus;

public class LSTMLayer implements Evolvable {

	public LSTMLayer(int inputDim, int outputDim) {
		this.inputDim = inputDim;
		this.outputDim = outputDim;
		Wz = new double[outputDim][inputDim];
		Wi = new double[outputDim][inputDim];
		Wf = new double[outputDim][inputDim];
		Wo = new double[outputDim][inputDim];
		Rz = new double[outputDim][outputDim];
		Ri = new double[outputDim][outputDim];
		Rf = new double[outputDim][outputDim];
		Ro = new double[outputDim][outputDim];
		pi = new double[outputDim];
		pf = new double[outputDim];
		po = new double[outputDim];
		bz = new double[outputDim];
		bi = new double[outputDim];
		bf = new double[outputDim];
		bo = new double[outputDim];
		initCellStates = new double[outputDim];
		initOutputs = new double[outputDim];
		Util.gaussianInit(Wz, 0, 1.0 / inputDim);
		Util.gaussianInit(Wi, 0, 1.0 / inputDim);
		Util.gaussianInit(Wf, 0, 1.0 / inputDim);
		Util.gaussianInit(Wo, 0, 1.0 / inputDim);
		Util.gaussianInit(Rz, 0, 1.0 / outputDim);
		Util.gaussianInit(Ri, 0, 1.0 / outputDim);
		Util.gaussianInit(Rf, 0, 1.0 / outputDim);
		Util.gaussianInit(Ro, 0, 1.0 / outputDim);
		Util.gaussianInit(pi, 0, 1.0 / outputDim);
		Util.gaussianInit(pf, 0, 1.0 / outputDim);
		Util.gaussianInit(po, 0, 1.0 / outputDim);
		Util.gaussianInit(bz, 0, 1.0 / outputDim);
		Util.gaussianInit(bi, 0, 1.0 / outputDim);
		Util.gaussianInit(bf, 0, 1.0 / outputDim);
		Util.gaussianInit(bo, 0, 1.0 / outputDim);
		Util.gaussianInit(initCellStates);
		Util.gaussianInit(initOutputs);
		cellStates = Util.head(initCellStates, outputDim);
		outputs = Util.head(initOutputs, outputDim);
	}

	public LSTMLayer(int inputDim, int outputDim, double[] genome) throws Exception {
		if (genome.length != getGenomeLength(inputDim, outputDim))
			throw new Exception("LSTMPlus.Layer.Layer(int,int,double[]): Invalid genome.");
		this.inputDim = inputDim;
		this.outputDim = outputDim;
		Wz = new double[outputDim][inputDim];
		Wi = new double[outputDim][inputDim];
		Wf = new double[outputDim][inputDim];
		Wo = new double[outputDim][inputDim];
		Rz = new double[outputDim][outputDim];
		Ri = new double[outputDim][outputDim];
		Rf = new double[outputDim][outputDim];
		Ro = new double[outputDim][outputDim];
		pi = new double[outputDim];
		pf = new double[outputDim];
		po = new double[outputDim];
		bz = new double[outputDim];
		bi = new double[outputDim];
		bf = new double[outputDim];
		bo = new double[outputDim];
		initCellStates = new double[outputDim];
		initOutputs = new double[outputDim];
		int start = 0;
		start = Util.initByGenome(Wz, genome, start);
		start = Util.initByGenome(Wi, genome, start);
		start = Util.initByGenome(Wf, genome, start);
		start = Util.initByGenome(Wo, genome, start);
		start = Util.initByGenome(Rz, genome, start);
		start = Util.initByGenome(Ri, genome, start);
		start = Util.initByGenome(Rf, genome, start);
		start = Util.initByGenome(Ro, genome, start);
		start = Util.initByGenome(pi, genome, start);
		start = Util.initByGenome(pf, genome, start);
		start = Util.initByGenome(po, genome, start);
		start = Util.initByGenome(bz, genome, start);
		start = Util.initByGenome(bi, genome, start);
		start = Util.initByGenome(bf, genome, start);
		start = Util.initByGenome(bo, genome, start);
		start = Util.initByGenome(initCellStates, genome, start);
		Util.initByGenome(initOutputs, genome, start);
		cellStates = Util.head(initCellStates, outputDim);
		outputs = Util.head(initOutputs, outputDim);
	}

	public double[] getGenome() {
		double[] genome = Util.serialize(Wz);
		genome = Util.concat(genome, Util.serialize(Wi));
		genome = Util.concat(genome, Util.serialize(Wf));
		genome = Util.concat(genome, Util.serialize(Wo));
		genome = Util.concat(genome, Util.serialize(Rz));
		genome = Util.concat(genome, Util.serialize(Ri));
		genome = Util.concat(genome, Util.serialize(Rf));
		genome = Util.concat(genome, Util.serialize(Ro));
		genome = Util.concat(genome, pi);
		genome = Util.concat(genome, pf);
		genome = Util.concat(genome, po);
		genome = Util.concat(genome, bz);
		genome = Util.concat(genome, bi);
		genome = Util.concat(genome, bf);
		genome = Util.concat(genome, bo);
		genome = Util.concat(genome, initCellStates);
		genome = Util.concat(genome, initOutputs);
		return genome;
	}
	
	public static int getGenomeLength(int inputDim, int outputDim) {
		return inputDim * outputDim * 4 + outputDim * outputDim * 4 + outputDim * 9;
	}
	
	public double[] attemptiveActivate(double[] x) throws Exception {
		if (x.length != inputDim)
			throw new Exception("LSTMPlus.Layer.activate(double[]): Invalid input length.");
		double[] zt = Util.tanh(Util.add(Util.add(Util.multiply(Wz, x), Util.multiply(Rz, outputs)), bz));
		double[] it = Util.sigmoid(Util.add(Util.add(Util.add(Util.multiply(Wi, x), Util.multiply(Ri, outputs)),
				Util.pointWiseMultiply(pi, cellStates)), bi));
		double[] ft = Util.sigmoid(Util.add(Util.add(Util.add(Util.multiply(Wf, x), Util.multiply(Rf, outputs)),
				Util.pointWiseMultiply(pf, cellStates)), bf));
		double[] trialStates = Util.add(Util.pointWiseMultiply(it, zt), Util.pointWiseMultiply(ft, cellStates));
		double[] ot = Util.sigmoid(Util.add(Util.add(Util.add(Util.multiply(Wo, x), Util.multiply(Ro, outputs)),
				Util.pointWiseMultiply(po, trialStates)), bo));
		double[] trialOutputs = Util.pointWiseMultiply(ot, Util.tanh(trialStates));
		return trialOutputs;
	}

	public double[] activate(double[] x) throws Exception {
		if (x.length != inputDim)
			throw new Exception("LSTMPlus.Layer.activate(double[]): Invalid input length.");
		double[] zt = Util.tanh(Util.add(Util.add(Util.multiply(Wz, x), Util.multiply(Rz, outputs)), bz));
		double[] it = Util.sigmoid(Util.add(Util.add(Util.add(Util.multiply(Wi, x), Util.multiply(Ri, outputs)),
				Util.pointWiseMultiply(pi, cellStates)), bi));
		double[] ft = Util.sigmoid(Util.add(Util.add(Util.add(Util.multiply(Wf, x), Util.multiply(Rf, outputs)),
				Util.pointWiseMultiply(pf, cellStates)), bf));
		cellStates = Util.add(Util.pointWiseMultiply(it, zt), Util.pointWiseMultiply(ft, cellStates));
		double[] ot = Util.sigmoid(Util.add(Util.add(Util.add(Util.multiply(Wo, x), Util.multiply(Ro, outputs)),
				Util.pointWiseMultiply(po, cellStates)), bo));
		outputs = Util.pointWiseMultiply(ot, cellStates);
		
		return outputs;
	}
	
	public void reset() {
		cellStates = Util.head(initCellStates, outputDim);
		outputs = Util.head(initOutputs, outputDim);
	}

	private double[][] Wz;
	private double[][] Wi;
	private double[][] Wf;
	private double[][] Wo;
	private double[][] Rz;
	private double[][] Ri;
	private double[][] Rf;
	private double[][] Ro;
	private double[] pi;
	private double[] pf;
	private double[] po;
	private double[] bz;
	private double[] bi;
	private double[] bf;
	private double[] bo;
	private double[] cellStates;
	private double[] outputs;
	private double[] initCellStates;
	private double[] initOutputs;

	public int inputDim;
	public int outputDim;
}
