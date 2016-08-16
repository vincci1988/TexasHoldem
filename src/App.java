import evolution.*;

public class App {

	public static void main(String[] args) {
		try {
			EvolutionBase evo = new LSTMPlayerEvolution();
			//EvolutionBase evo = new CandidStatisticianEvolution();
			evo.run();
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}
	}

}
