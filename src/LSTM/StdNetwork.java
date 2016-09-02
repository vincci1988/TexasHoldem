package LSTM;

import java.io.BufferedReader;
import java.io.FileReader;

public class StdNetwork {

	public StdNetwork(int inputCnt, int hiddenCnt, int outputCnt) {
		this.inputCnt = inputCnt;
		this.hiddenCnt = hiddenCnt;
		this.outputCnt = outputCnt;
		inputWeights = new double[inputCnt * 2];
		hiddenLayerWeights = new double[(inputCnt + 1) * hiddenCnt];
		outputLayerWeights = new double[(hiddenCnt + 1) * outputCnt];
		outputs = new double[outputCnt];
		for (int i = 0; i < outputs.length; i++)
			outputs[i] = 0;
		Misc.gaussianInit(inputWeights);
		Misc.gaussianInit(hiddenLayerWeights);
		Misc.gaussianInit(outputLayerWeights);
	}
	
	public StdNetwork(int inputCnt, int hiddenCnt, int outputCnt, String genomeFile) throws Exception {
		int genomeLength = inputCnt * 2 + (inputCnt + 1) * hiddenCnt + (hiddenCnt + 1) * outputCnt;
		double[] genome = new double[genomeLength];
		FileReader freader = new FileReader(genomeFile);
		BufferedReader reader = new BufferedReader(freader); 
		for (int i = 0; i < genome.length; i++)
			genome[i] = Double.parseDouble(reader.readLine());
		reader.close();
		this.inputCnt = inputCnt;
		this.hiddenCnt = hiddenCnt;
		this.outputCnt = outputCnt;
		inputWeights = Misc.head(genome, inputCnt * 2);
		hiddenLayerWeights = Misc.subArray(genome, inputWeights.length, (inputCnt + 1) * hiddenCnt);
		outputLayerWeights = Misc.tail(genome, inputWeights.length + hiddenLayerWeights.length);
		outputs = new double[outputCnt];
	}

	public StdNetwork(int inputCnt, int hiddenCnt, int outputCnt, double[] genome) throws Exception {
		if (genome.length != inputCnt * 2 + (inputCnt + 1) * hiddenCnt + (hiddenCnt + 1) * outputCnt)
			throw new Exception("OutputNetwork: Invalid genome length.");
		this.inputCnt = inputCnt;
		this.hiddenCnt = hiddenCnt;
		this.outputCnt = outputCnt;
		inputWeights = Misc.head(genome, inputCnt * 2);
		hiddenLayerWeights = Misc.subArray(genome, inputWeights.length, (inputCnt + 1) * hiddenCnt);
		outputLayerWeights = Misc.tail(genome, inputWeights.length + hiddenLayerWeights.length);
		outputs = new double[outputCnt];
	}

	public double[] getGenome() {
		return Misc.concat(Misc.concat(inputWeights, hiddenLayerWeights), outputLayerWeights);
	}

	public double[] activate(double[] X) {
		for (int i = 0; i < outputCnt; i++) {
			double[] hiddenLayerInputs = new double[inputCnt + 1];
			hiddenLayerInputs[inputCnt] = 1.0;
			for (int j = 0; j < inputCnt; j++)
				hiddenLayerInputs[j] = Math.tanh(X[j] * inputWeights[2 * j] + inputWeights[2 * j + 1]);
			double[] outputLayerInputs = new double[hiddenCnt + 1];
			outputLayerInputs[hiddenCnt] = 1.0;
			for (int j = 0; j < hiddenCnt; j++) {
				double weightedSum = 0.0;
				for (int k = 0; k < hiddenLayerInputs.length; k++)
					weightedSum += hiddenLayerWeights[j * hiddenLayerInputs.length + k] * hiddenLayerInputs[k];
				outputLayerInputs[j] = Math.tanh(weightedSum);
			}
			for (int j = 0; j < outputCnt; j++) {
				double weightedSum = 0.0;
				for (int k = 0; k < outputLayerInputs.length; k++)
					weightedSum += outputLayerWeights[j * outputLayerInputs.length + k] * outputLayerInputs[k];
				outputs[j] = Math.tanh(weightedSum);
			}
		}
		return outputs;
	}

	public double[] inputWeights;
	public double[] hiddenLayerWeights;
	public double[] outputLayerWeights;
	public double[] outputs;
	public int inputCnt;
	public int hiddenCnt;
	public int outputCnt;
}
