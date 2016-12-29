package ashe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import evolvable_players.GenomeBase;
import evolvable_players.NumericGenome;

public class AsheGenome extends NumericGenome {

	public AsheGenome(double[] genes) {
		super(genes);
	}
	
	public AsheGenome(String genomeFile) throws IOException {
		super(null);
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int genomeLength = NNAdviser.getGenomeLength();
		genes = new double[genomeLength];
		for (int i =0; i < genomeLength; i++) 
			genes[i] = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
	}

	@Override
	public void mutate(double mutationRate, double mutationStrength) {
		Random rand = new Random();
		for (int i = 0; i < genes.length; i++)
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
				childGenes[i] = ((AsheGenome) spouseGenome).getGenes()[i];
		}
		return new AsheGenome(childGenes);
	}

}
