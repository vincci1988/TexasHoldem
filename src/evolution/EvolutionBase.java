package evolution;

import java.util.ArrayList;
import java.util.Random;

public abstract class EvolutionBase {
	
	public EvolutionBase() {
		population = new ArrayList<Agent>();
		random = new Random();
	}
	
	abstract public void run() throws Exception;
	
	abstract void select() throws Exception;
	abstract void reproduce();
	
	ArrayList<Agent> population;
	protected static Random random;
}
