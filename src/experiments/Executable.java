package experiments;

import advanced_players.Shaco;
import advanced_players.Ahri;
import evolvable_players.*;
import holdem.NLHeadsUpTable;
import opponent_model.GameForest;
import simple_players.CallingMachine;
import simple_players.HotheadManiac;
import simple_players.Villian;

@SuppressWarnings("unused")
public class Executable {

	public static void main(String[] args) {
		/**
		 * Instruction: 1. CREATE EXPERIMENT CLASS 2. CALL "RUN"
		 */
		try {
			//LSTMNoLimitTester agent = new LSTMNoLimitTester(1, new LSTMNoLimitTesterGenome("LSTMNoLimitTesterGenome_1.0.txt"));
			
			//Shaco agent = new Shaco(1);
			
			Ahri agent = new Ahri(1, new LSTMNoLimitTesterGenome("LSTMNoLimitTesterGenome_1.0.txt"));
			
			NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, new CandidStatistician(-1), 50,
					20000, 500);
			double[] performances = headsUpTable.start();
			System.out.println(performances[0]);
			
			/*
			HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation(agent, 
					"NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt", 10);
			test.run();
			GameForest forest = new GameForest(1, "forest.txt");
			System.out.println(forest.display());*/
			
			agent.saveForest("forest.txt");
			
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}

	}

}
