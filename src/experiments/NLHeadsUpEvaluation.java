package experiments;

import java.io.IOException;

import evolvable_players.*;
import holdem.NLHeadsUpTable;
import holdem.PlayerBase;
import simple_players.*;

public class NLHeadsUpEvaluation implements Exp {

	public NLHeadsUpEvaluation(String performanceLog, String gameLog) throws IOException, Exception {
		// Update to evaluate different agent / against different opponents
		this.opponents = new PlayerBase[4];
		//this.agent = new LSTMNoLimitTester(0, new LSTMNoLimitTesterGenome("LSTMNoLimitTesterGenome.txt"));
		this.agent = new LSTMNoLimitTester(0, new LSTMNoLimitTesterGenome("LSTMNoLimitTesterGenome.txt"));
		this.opponents[0] = new CandidStatistician(1, new CandidStatisticianGenome(2.195, 0.7147, 0.5669));
		this.opponents[1] = new HotheadManiac(2);
		this.opponents[2] = new ScaredLimper(3);
		this.opponents[3] = new CallingMachine(4);
		this.performanceLog = performanceLog;
		this.gameLog = gameLog;
	}
	
	public NLHeadsUpEvaluation(PlayerBase agent, String performanceLog, String gameLog) throws IOException, Exception {
		// Update to evaluate different agent / against different opponents
		this.opponents = new PlayerBase[4];
		this.agent = agent;
		this.opponents[0] = new CandidStatistician(1, new CandidStatisticianGenome(2.195, 0.7147, 0.5669));
		this.opponents[1] = new HotheadManiac(2);
		this.opponents[2] = new ScaredLimper(3);
		this.opponents[3] = new CallingMachine(4);
		this.performanceLog = performanceLog;
		this.gameLog = gameLog;
	}

	@Override
	public void run() throws Exception {
		for (int i = 0; i < opponents.length; i++) {
			System.out.println("No-Limit Heads-up Evaluation:");
			System.out.println("Agent: " + agent.getName());
			System.out.println("Opponent: " + opponents[i].getName());
			int SBAmt = 50;
			int buyInAmt = 20000;
			int maxDeckCnt = 1500;
			System.out.println("SB amount: " + SBAmt);
			System.out.println("Buy-in amount: " + buyInAmt);
			NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, opponents[i], SBAmt, buyInAmt, maxDeckCnt);
			System.out.println("Agent Perforance: " + headsUpTable.start(i + "_" + performanceLog, i + "_" + gameLog) + " mBB/hand\n");
		}
	}

	PlayerBase agent;
	PlayerBase[] opponents;
	String performanceLog;
	String gameLog;
}
