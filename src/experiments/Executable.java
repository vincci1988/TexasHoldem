package experiments;

import advanced_players.Shaco;
import ASHE.Ashe;
import ashe_rulebased.Ashe_RB;
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
			PlayerBase agent = new Ashe(1, "AsheGenome_Gen10.txt");  
			PlayerBase[] opponents = new PlayerBase[6];
			opponents[0] = new Ashe_RB(0);
			opponents[1] = new Shaco(-1);
			opponents[2] = new CandidStatistician(-2);
			opponents[3] = new HotheadManiac(-3);
			opponents[4] = new CallingMachine(-4);
			opponents[5] = new ScaredLimper(-5);			
			for (int i = 0; i < opponents.length; i++) {
				NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, opponents[i], 50,
					20000, 9000);
				double[] performances = headsUpTable.start("pfm_ashe_" + i + ".txt", "glog_ashe_" + i + ".txt");
				System.out.println(agent.getName() + " v.s. " + opponents[i].getName() + ": " + performances[0]);
				//((Ashe)agent).saveForest("forest_ashe_" + i + ".txt");
			}
				
			/*
			HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation(agent, 
					"NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt", 500);
			test.run();
			//GameForest forest = new GameForest(1, "forest.txt");
			//System.out.println(forest.display());
			//((Ashe)agent).saveForest("forest.txt");
			*/ 	 					
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}
	}

}
