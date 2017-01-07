package experiments;

import advanced_players.Shaco;
import ASHE.Ashe;
import ASHE.AsheParams;
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
		try {	
			PlayerBase agent = new Ashe(478, "AsheGenome_0090478.txt");
			autoEval(agent);
			//manualEval(agent);			 	 					
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}
	}
	
	static void autoEval(PlayerBase agent) throws Exception {
		PlayerBase[] opponents = new PlayerBase[1];
		opponents[0] = new Ashe(-1, "AsheGenome_WRE.txt");
		/*
		opponents[0] = new Ashe_RB(-1);
		opponents[1] = new Shaco(-2);
		opponents[2] = new CandidStatistician(-3);
		opponents[3] = new HotheadManiac(-4);
		opponents[4] = new CallingMachine(-5);
		opponents[5] = new ScaredLimper(-6);		
		*/			
		for (int i = 0; i < opponents.length; i++) {
			int gameCnt = 3000; 
			NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, opponents[i], 50,
				20000, gameCnt);
			double[] performances = headsUpTable.start("pfm_ashe_" + i + ".txt", "glog_ashe_" + i + ".txt");
			System.out.println(agent.getName() + " v.s. " + opponents[i].getName() + ": " + performances[0]);
		}
	}
	
	static void manualEval(PlayerBase agent) throws Exception {
		HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation(agent, 
				"NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt", 500);
		test.run();
	}

}
