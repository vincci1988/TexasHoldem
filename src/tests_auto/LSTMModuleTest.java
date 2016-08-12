package tests_auto;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import LSTM.Module;

public class LSTMModuleTest {

	@Test
	public void genomeRestorationTest() {
		try {
			int inputSize = 10;
			Random rand = new Random();
			Module lstm1 = new Module(inputSize);
			Module lstm2 = new Module(lstm1.getGenome());
			int testNum = 50;
			for (int i = 0; i < testNum; i++) {
				double[] x = new double[inputSize];
				for (int j = 0; j < inputSize; j++)
					x[j] = rand.nextDouble();
				assertTrue(lstm1.activate(x) == lstm2.activate(x));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void computationValidationTest() {
		try {
			int inputSize = 10;
			Random rand = new Random();
			Module lstm = new Module(inputSize);
			int testNum = 50;
			for (int i = 0; i < testNum; i++) {
				double[] x = new double[inputSize];
				for (int j = 0; j < inputSize; j++)
					x[j] = rand.nextDouble();
				assertTrue(computeValidationVal(x, lstm) == lstm.activate(x));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	double computeValidationVal(double[] x, Module lstm) throws Exception {
		double[] input = new double[x.length + 3];
		for (int i = 0; i < x.length; i++)
			input[i] = x[i];
		input[x.length] = 1.0;
		input[x.length + 1] = lstm.output;
		input[x.length + 2] = lstm.cellState;
		double cellState = Module.sigmoid(Module.dotProduct(input, lstm.forgetGateWeights)) * lstm.cellState
				+ Module.sigmoid(Module.dotProduct(input, lstm.inputGateWeights))
						* Module.sigmoid(Module.dotProduct(input, lstm.inputWeights));
		input[x.length + 2] = cellState;
		double output = Module.sigmoid(cellState) * Module.sigmoid(Module.dotProduct(input, lstm.outputGateWeights));
		return output;
	}

}
