package evolvable_players;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class LSTMNoLimitTesterGenome extends NumericGenome {

	public LSTMNoLimitTesterGenome(double[] genes) {
		super(genes);
	}

	public LSTMNoLimitTesterGenome(String genomeFile) throws IOException {
		super(null);
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int inputSize = (int) Double.parseDouble(bufferedReader.readLine());
		int genomeLength = ((inputSize + 3) * 4 + 1) * LSTMNoLimitTester.cellCnt;
		genes = new double[genomeLength];
		genes[0] = inputSize;
		for (int i = 1; i < genomeLength; i++)
			genes[i] = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
	}

	public void mutate(double mutationRate, double mutationStrength) {
		Random rand = new Random();
		int cellGenomeLength = ((int)genes[0] + 3) * 4 + 1;
		for (int i = 0; i < genes.length; i++)
			if (i % cellGenomeLength != 0 && rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
	}

	@Override
	public GenomeBase crossOver(GenomeBase spouseGenome) {
		double[] childGenes = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
			if (i % 2 == 0) childGenes[i] = genes[i];
			else childGenes[i] = ((LSTMNoLimitTesterGenome)spouseGenome).getGenes()[i];
		}
		return new LSTMNoLimitTesterGenome(childGenes);
	}
}
