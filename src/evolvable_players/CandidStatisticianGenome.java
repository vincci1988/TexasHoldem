package evolvable_players;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class CandidStatisticianGenome extends GenomeBase {

	public CandidStatisticianGenome(double conservativeness, double baseRateHeadsUp, double baseRateFullTable) {
		this.conservativeness = conservativeness;
		this.baseRateHeadsUp = baseRateHeadsUp;
		this.baseRateFullTable = baseRateFullTable;
	}
	
	public CandidStatisticianGenome(String genomeFile) throws IOException {
		FileReader fileReader = new FileReader(genomeFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		conservativeness = Double.parseDouble(bufferedReader.readLine());
		baseRateHeadsUp = Double.parseDouble(bufferedReader.readLine());
		baseRateFullTable = Double.parseDouble(bufferedReader.readLine());
		bufferedReader.close();
	}

	@Override
	public void mutate(double mutationRate, double mutationStrength) {
		Random random = new Random();
		conservativeness += 0.25 * random.nextGaussian();
		if (conservativeness < 1.0)
			conservativeness = 1.0;
		baseRateHeadsUp += 0.02 * random.nextGaussian();
		if (baseRateHeadsUp < 0.25)
			baseRateHeadsUp = 0.25;
		baseRateFullTable += 0.02 * random.nextGaussian();
		if (baseRateFullTable > 0.8)
			baseRateFullTable = 0.8;
		if (baseRateFullTable < baseRateHeadsUp) {
			double temp = baseRateFullTable;
			baseRateFullTable = baseRateHeadsUp;
			baseRateHeadsUp = temp;
		}
	}

	@Override
	public GenomeBase crossOver(GenomeBase spouseGenome) {
		return new CandidStatisticianGenome(conservativeness, ((CandidStatisticianGenome) spouseGenome).baseRateHeadsUp,
				baseRateFullTable);
	}

	@Override
	public void writeToFile(String pathToFile) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(pathToFile);
		writer.println(conservativeness);
		writer.println(baseRateHeadsUp);
		writer.println(baseRateFullTable);
		writer.close();
	}

	public double conservativeness;
	public double baseRateHeadsUp;
	public double baseRateFullTable;
}
