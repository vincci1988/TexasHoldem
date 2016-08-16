package experiments;

public class Executable {

	public static void main(String[] args) {
		/**
		 * Instruction: 
		 * 1. CREATE EXPERIMENT CLASS 
		 * 2. CALL "RUN"
		 */
		try {
			NLHeadsUpEvaluation test = new NLHeadsUpEvaluation("NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt");
			// HumanTableManualTest test = new HumanTableManualTest();
			// TournamentChampionMatch test = new TournamentChampionMatch();
			test.run();
		} catch (Exception exception) {
			System.out.println(exception);
		}

	}

}
