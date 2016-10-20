package experiments;

import advanced_players.Shaco;
import evolvable_players.*;
import simple_players.Villian;

@SuppressWarnings("unused")
public class Executable {

	public static void main(String[] args) {
		/**
		 * Instruction: 1. CREATE EXPERIMENT CLASS 2. CALL "RUN"
		 */
		try {
			//LSTMNoLimitTester agent = new LSTMNoLimitTester(1, new LSTMNoLimitTesterGenome("LSTMNoLimitTesterGenome.txt"));
			/*
			Shaco agent = new Shaco(1);
			HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation(agent, //new CandidStatistician(1),
					"NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt", 500);
			test.run();
			*/
			NLHeadsUpEvaluation eval = new NLHeadsUpEvaluation(new Shaco(1), 1500, "NLHeadsUpPerformance.txt",
					"NLHeadsUpGameLog.txt");
			eval.run();
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}

	}

}
