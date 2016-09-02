package evolvable_players;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class LSTMHeadsUpPlayerGenome extends NumericGenome {

	public LSTMHeadsUpPlayerGenome(double[] genes) {
		super(genes);
	}
	
	public LSTMHeadsUpPlayerGenome(String genomeFile) throws IOException {
		super(null);
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int cellCnt = (int) Double.parseDouble(bufferedReader.readLine());
		int inputSize = (int) Double.parseDouble(bufferedReader.readLine());
		int genomeLength = 1 + ((inputSize + 3) * 4 + 1) * cellCnt;
		genes = new double[genomeLength];
		genes[0] = cellCnt;
		genes[1] = inputSize;
		for (int i = 2; i < genomeLength; i++)
			genes[i] = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
	}
	
	public void mutate(double mutationRate, double mutationStrength) {
		Random rand = new Random();
		int cellGeneLength = ((int) genes[1] + 3) * 4 + 1;
		for (int i = 1; i < 1 + cellGeneLength * LSTMHeadsUpPlayer.cellCnt; i++)
			if ((i - 1) % cellGeneLength != 0 && rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
	}
	/*
	public LSTMHeadsUpPlayerGenome(String genomeFile) throws IOException {
		super(null);
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int cNetGeneLength = LSTMHeadsUpPlayer.cellCnt * 2
				+ (LSTMHeadsUpPlayer.cellCnt + 1) * LSTMHeadsUpPlayer.hiddenCnt
				+ (LSTMHeadsUpPlayer.hiddenCnt + 1) * LSTMHeadsUpPlayer.outputSize;
		genes = new double[cNetGeneLength];
		for (int i = 0; i < genes.length; i++)
			genes[i] = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
	}

	public void mutate(double mutationRate, double mutationStrength) {
		Random rand = new Random();
		for (int i = 0; i < genes.length; i++)
			if (rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
	}
	*/
	@Override
	public GenomeBase crossOver(GenomeBase spouseGenome) {
		double[] childGenes = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
			if (i % 2 == 0)
				childGenes[i] = genes[i];
			else
				childGenes[i] = ((LSTMHeadsUpPlayerGenome) spouseGenome).getGenes()[i];
		}
		return new LSTMHeadsUpPlayerGenome(childGenes);
	}

}
