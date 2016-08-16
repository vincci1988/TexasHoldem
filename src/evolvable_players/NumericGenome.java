package evolvable_players;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public abstract class NumericGenome extends GenomeBase {
	
	NumericGenome(double[] genes) {
		this.genes = genes;
	}
	
	public double[] getGenes() {
		return genes;
	}

	@Override
	public abstract void mutate(double mutationRate, double mutationStrength);

	@Override
	public abstract GenomeBase crossOver(GenomeBase spouseGenome);

	@Override
	public void writeToFile(String pathToFile) throws FileNotFoundException {
		PrintWriter genomeLog = new PrintWriter(pathToFile);
		for (int i = 0; i < genes.length; i++)
			genomeLog.println(genes[i]);
		genomeLog.close();
	}

	protected double[] genes;
}
