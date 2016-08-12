package LSTM;

import java.util.Random;

public class Module {

	public Module(int inputSize) {
		this.inputSize = inputSize;
		inputWeights = new double[inputSize + 3];
		inputGateWeights = new double[inputSize + 3];
		forgetGateWeights = new double[inputSize + 3];
		outputGateWeights = new double[inputSize + 3];
		Random rand = new Random();
		for (int i = 0; i < inputSize + 3; i++) {
			inputWeights[i] = (rand.nextGaussian());
			inputGateWeights[i] = (rand.nextGaussian());
			forgetGateWeights[i] = (rand.nextGaussian());
			outputGateWeights[i] = (rand.nextGaussian());
		}
		cellState = 0;
		output = 0;
	}

	public Module(double[] gene) throws Exception {
		inputSize = (int) gene[0];
		if (gene.length != (inputSize + 3) * 4 + 1)
			throw new Exception("LSTM.MODULE.MODULE(double[]): INVALID GENE LENGTH");
		inputWeights = new double[inputSize + 3];
		inputGateWeights = new double[inputSize + 3];
		forgetGateWeights = new double[inputSize + 3];
		outputGateWeights = new double[inputSize + 3];
		int p = 1;
		for (int i = 0; i < inputSize + 3; i++) {
			inputWeights[i] = gene[p];
			inputGateWeights[i] = gene[p + inputSize + 3];
			forgetGateWeights[i] = gene[p + 2 * inputSize + 6];
			outputGateWeights[i] = gene[p + 3 * inputSize + 9];
			p++;
		}
	}
	
	public double[] getGenome() {
		double[] gene = new double[1 + (inputSize + 3) * 4];
		gene[0] = inputSize;
		int p = 1;
		for (int i = 0; i < inputSize + 3; i++) {
			gene[p] = inputWeights[i];
			gene[p + inputSize + 3] = inputGateWeights[i];
			gene[p + 2 * inputSize + 6] = forgetGateWeights[i];
			gene[p + 3 * inputSize + 9] = outputGateWeights[i];
			p++;
		}
		return gene;
	}

	public double activate(double[] x) throws Exception {
		if (x.length != inputSize)
			throw new Exception("LSTM.MODULE.ACTIVATE(double[]): INVALID INPUT VECTOR LENGTH");
		double[] input = new double[inputSize + 3];
		for (int i = 0; i < x.length; i++)
			input[i] = x[i];
		input[x.length] = 1.0;
		input[x.length + 1] = output;
		input[x.length + 2] = cellState;
		double forgetGateOutput = sigmoid(dotProduct(input, forgetGateWeights));
		double inputGateOutput = sigmoid(dotProduct(input, inputGateWeights));
		double cellStateUpdate = tanh(dotProduct(input, inputWeights));
		cellState = forgetGateOutput * cellState + inputGateOutput * cellStateUpdate;
		input[x.length + 2] = cellState;
		double outputGateOutput = sigmoid(dotProduct(input, outputGateWeights));
		output = outputGateOutput * tanh(cellState);
		return output;
	}

	static public double dotProduct(double[] x, double[] y) throws Exception {
		if (x.length != y.length)
			throw new Exception("LSTM.MODULE.dotProduct(double[],double[]): INEQUAL VECTOR LENGTH");
		double result = 0;
		for (int i = 0; i < x.length; i++)
			result += x[i] * y[i];
		return result;
	}

	static public double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	static public double tanh(double x) {
		return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
	}

	public final int inputSize;
	public double[] inputWeights;
	public double[] inputGateWeights;
	public double[] forgetGateWeights;
	public double[] outputGateWeights;
	public double cellState;
	public double output;
}
