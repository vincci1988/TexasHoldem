
import stats.DominationData;

public class App {

	public static void main(String[] args) {
		try {
			DominationData stats = new DominationData();
			//EvolutionBase evo = new CandidStatisticianEvolution();
			stats.compute("dominate.txt");
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}
	}

}
