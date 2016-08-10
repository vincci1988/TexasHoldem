package experiments;

public class Execution {

	public static void main(String[] args) {
		/**
		 * Instruction: 
		 * 1. CREATE EXPERIMENT CLASS 
		 * 2. CALL "RUN"
		 */
		try {
			NLHeadsUpEvaluation test = new NLHeadsUpEvaluation("NLHeadsUpPerformance.txt", "NLHeadsUpGameLog");
			// HumanTableManualTest test = new HumanTableManualTest();
			// TournamentChampionMatch test = new TournamentChampionMatch();
			test.run();
		} catch (Exception exception) {
			System.out.println(exception);
		}

	}

}
