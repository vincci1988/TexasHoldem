package tests_auto;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import LSTM.Misc;
import LSTM.Cell;

public class LSTMCellTest {

	@Test
	public void genomeRestorationTest() {
		try {
			int inputSize = 10;
			Random rand = new Random();
			Cell lstm1 = new Cell(inputSize);
			Cell lstm2 = new Cell(lstm1.getGenome());
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
			Cell lstm = new Cell(inputSize);
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

	double computeValidationVal(double[] x, Cell lstm) throws Exception {
		double[] input = new double[x.length + 3];
		for (int i = 0; i < x.length; i++)
			input[i] = x[i];
		input[x.length] = 1.0;
		input[x.length + 1] = lstm.output;
		input[x.length + 2] = lstm.cellState;
		double cellState = Misc.sigmoid(Misc.dotProduct(input, lstm.forgetGateWeights)) * lstm.cellState
				+ Misc.sigmoid(Misc.dotProduct(input, lstm.inputGateWeights))
						* Math.tanh(Misc.dotProduct(input, lstm.inputWeights));
		input[x.length + 2] = cellState;
		double output = Math.tanh(cellState) * Misc.sigmoid(Misc.dotProduct(input, lstm.outputGateWeights));
		return output;
	}

}
