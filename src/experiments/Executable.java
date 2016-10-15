package experiments;

import evolvable_players.*;

public class Executable {

	public static void main(String[] args) {
		/**
		 * Instruction: 1. CREATE EXPERIMENT CLASS 2. CALL "RUN"
		 */
		try {
			LSTMNoLimitTester agent = new LSTMNoLimitTester(1, new LSTMNoLimitTesterGenome("LSTMNoLimitTesterGenome.txt"));
			HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation(agent, //new CandidStatistician(1),
					"NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt", 500);
			test.run();
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}

	}

}
