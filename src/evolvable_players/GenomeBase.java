package evolvable_players;

import java.io.FileNotFoundException;

public abstract class GenomeBase {
	
	public abstract void mutate(double mutationRate, double mutationStrength);
	public abstract GenomeBase crossOver(GenomeBase spouseGenome);
	public abstract void writeToFile(String pathToFile) throws FileNotFoundException;
}
