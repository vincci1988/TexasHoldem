package evolvable_players;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class LSTMHeadsUpPlayerGenome extends GenomeBase {

	public LSTMHeadsUpPlayerGenome(double[] genes) {
		this.genes = genes;
	}
	
	public LSTMHeadsUpPlayerGenome(String genomeFile) throws IOException {
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int inputSize = (int)Double.parseDouble(bufferedReader.readLine());
		int geneLength = (inputSize + 3) * 4 + 1;
		genes = new double[geneLength];
		genes[0] = inputSize;
		for (int i = 1; i < geneLength; i++)
			genes[i] = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
	}
	
	@Override
	public void mutate(double mutationRate, double mutationStrength) {
		Random rand = new Random();
		for (int i = 1; i < genes.length; i++)
			if (rand.nextDouble() < mutationRate)
				genes[i] += mutationStrength * rand.nextGaussian();
	}

	public double[] getGenes() {
		return genes;
	}
	
	double[] genes;

	@Override
	public void writeToFile(String pathToFile) throws FileNotFoundException {
		PrintWriter genomeLog = new PrintWriter(pathToFile);
		for (int i = 0; i < genes.length; i++)
			genomeLog.println(genes[i]);
		genomeLog.close();
	}

	@Override
	public GenomeBase crossOver(GenomeBase spouseGenome) {
		double[] childGenes = new double[genes.length];
		for (int i = 0; i < genes.length; i++) {
			if (i % 2 == 0) childGenes[i] = genes[i];
			else childGenes[i] = ((LSTMHeadsUpPlayerGenome)spouseGenome).getGenes()[i];
		}
		return new LSTMHeadsUpPlayerGenome(childGenes);
	}
}
