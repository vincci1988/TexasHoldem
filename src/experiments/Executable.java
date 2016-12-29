package experiments;

import advanced_players.Shaco;
import ashe.Ashe;
import ahri.Ahri;
import evolvable_players.*;
import holdem.NLHeadsUpTable;
import holdem.PlayerBase;
import opponent_model.GameForest;
import simple_players.CallingMachine;
import simple_players.HotheadManiac;
import simple_players.ScaredLimper;
import simple_players.Villian;

@SuppressWarnings("unused")
public class Executable {

	public static void main(String[] args) {
		/**
		 * Instruction: 1. CREATE EXPERIMENT CLASS 2. CALL "RUN"
		 */
		try {
			PlayerBase agent = new Ahri(1, "AhriGenome.txt");
			
			PlayerBase[] opponents = new PlayerBase[5];
			opponents[0] = new ScaredLimper(-1);
			opponents[1] = new CallingMachine(-2);
			opponents[2] = new HotheadManiac(-3);
			opponents[3] = new CandidStatistician(-4);
			opponents[4] = new Shaco(-5);
			for (int i = 0; i < opponents.length; i++) {
				NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, opponents[i], 50,
					20000, 1000);
				double[] performances = headsUpTable.start("pfm_ahri_" + i + ".txt", "glog_ahri_" + i + ".txt");
				System.out.println(agent.getName() + " v.s. " + opponents[i].getName() + ": " + performances[0]);
				((Ahri)agent).saveForest("forest_ahri_" + i + ".txt");
			}
			
			/*
			HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation(agent, 
					"NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt", 500);
			test.run();
			GameForest forest = new GameForest(1, "forest.txt");
			System.out.println(forest.display());
			((Ashe)agent).saveForest("forest.txt");
			*/
			
			
			
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}

	}

}
