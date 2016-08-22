package LSTM;

import java.util.Random;

public class Cell {

	public Cell(int inputSize) {
		this.inputSize = inputSize;
		inputWeights = new double[inputSize + 3];
		inputGateWeights = new double[inputSize + 3];
		forgetGateWeights = new double[inputSize + 3];
		outputGateWeights = new double[inputSize + 3];
		Random rand = new Random();
		for (int i = 0; i < inputSize + 3; i++) {
			inputWeights[i] = rand.nextGaussian();
			inputGateWeights[i] = rand.nextGaussian();
			forgetGateWeights[i] = rand.nextGaussian();
			outputGateWeights[i] = rand.nextGaussian();
		}
		inputWeights[inputSize + 2] = 0.0;
		cellState = 0;
		output = 0;
	}

	public Cell(double[] gene) throws Exception {
		inputSize = (int) gene[0];
		if (gene.length != (inputSize + 3) * 4 + 1)
			throw new Exception("LSTM.CELL.CELL(double[]): INVALID GENE LENGTH");
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
		inputWeights[inputSize + 2] = 0.0;
		cellState = 0;
		output = 0;
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

	public void reset() {
		cellState = 0;
		output = 0;
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
		double forgetGateOutput = Misc.sigmoid(Misc.dotProduct(input, forgetGateWeights));
		double inputGateOutput = Misc.sigmoid(Misc.dotProduct(input, inputGateWeights));
		double cellStateUpdate = Math.tanh(Misc.dotProduct(input, inputWeights));
		cellState = forgetGateOutput * cellState + inputGateOutput * cellStateUpdate;
		input[x.length + 2] = cellState;
		double outputGateOutput = Misc.sigmoid(Misc.dotProduct(input, outputGateWeights));
		output = outputGateOutput * Math.tanh(cellState);
		return output;
	}

	public final int inputSize;
	public double[] inputWeights;
	public double[] inputGateWeights;
	public double[] forgetGateWeights;
	public double[] outputGateWeights;
	public double cellState;
	public double output;
}
