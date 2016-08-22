package evolvable_players;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import LSTM.Misc;

public class LSTMNoLimitTesterGenome extends NumericGenome {

	public LSTMNoLimitTesterGenome(double[] genes) {
		super(genes);
	}

	public LSTMNoLimitTesterGenome(String genomeFile) throws IOException {
		super(null);
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int inputSize = (int) Double.parseDouble(bufferedReader.readLine());
		int inputCellGenomeLength = ((inputSize + 3) * 4 + 1) * LSTMNoLimitTester.inputCellCnt;
		genes = new double[inputCellGenomeLength];
		genes[0] = inputSize;
		for (int i = 1; i < inputCellGenomeLength; i++)
			genes[i] = Double.parseDouble(bufferedReader.readLine());
		int hiddenInputSize = (int) Double.parseDouble(bufferedReader.readLine());
		int hiddenCellGenomeLength = ((hiddenInputSize + 3) * 4 + 1) * LSTMNoLimitTester.hiddenCellCnt;
		double[] hiddenCellGenome = new double[hiddenCellGenomeLength];
		hiddenCellGenome[0] = hiddenInputSize;
		for (int i = 1; i < hiddenCellGenomeLength; i++)
			hiddenCellGenome[i] = Double.parseDouble(bufferedReader.readLine());
		genes = Misc.concat(genes, hiddenCellGenome);
		double[] outputNetworkGenome = new double[LSTMNoLimitTester.inputCellCnt * 2
				+ (LSTMNoLimitTester.inputCellCnt + 1) * LSTMNoLimitTester.oNetHiddenCnt
				+ (LSTMNoLimitTester.oNetHiddenCnt + 1) * LSTMNoLimitTester.oNetOutputCnt];
		for (int i = 0; i < outputNetworkGenome.length; i++)
			outputNetworkGenome[i] = Double.parseDouble(bufferedReader.readLine());
		genes = Misc.concat(genes, outputNetworkGenome);
		double[] inputNetworkGenome = new double[LSTMNoLimitTester.gameNodeInputSize * 2
				+ (LSTMNoLimitTester.gameNodeInputSize + 1) * LSTMNoLimitTester.iNetHiddenCnt
				+ (LSTMNoLimitTester.iNetHiddenCnt + 1) * LSTMNoLimitTester.iNetOutputCnt];
		for (int i = 0; i < inputNetworkGenome.length; i++)
			inputNetworkGenome[i] = Double.parseDouble(bufferedReader.readLine());
		genes = Misc.concat(genes, inputNetworkGenome);
		bufferedReader.close();
	}

	public void mutate(double mutationRate, double mutationStrength) {
		Random rand = new Random();
		int inputCellGenomeLength = ((int) genes[0] + 3) * 4 + 1;
		for (int i = 0; i < inputCellGenomeLength * LSTMNoLimitTester.inputCellCnt; i++)
			if (i % inputCellGenomeLength != 0 && rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
		int start = inputCellGenomeLength * LSTMNoLimitTester.inputCellCnt;
		int hiddenCellGenomeLength = ((int) genes[start] + 3) * 4 + 1;
		int end = start + hiddenCellGenomeLength * LSTMNoLimitTester.hiddenCellCnt;
		for (int i = start; i < end; i++)
			if ((i - start) % hiddenCellGenomeLength != 0 && rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
		for (int i = end; i < genes.length; i++)
			if (rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
	}

	@Override
	public GenomeBase crossOver(GenomeBase spouseGenome) {
		double[] childGenes = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
			if (i % 2 == 0)
				childGenes[i] = genes[i];
			else
				childGenes[i] = ((LSTMNoLimitTesterGenome) spouseGenome).getGenes()[i];
		}
		return new LSTMNoLimitTesterGenome(childGenes);
	}
}
