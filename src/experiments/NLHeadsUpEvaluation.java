package experiments;

import holdem.NLHeadsUpTable;
import holdem.PlayerBase;
import players.*;

public class NLHeadsUpEvaluation implements Exp {

	public NLHeadsUpEvaluation(String logPath) {
		//Update to evaluate different agent / against different opponents
		this.agent = new CandidStatistician(0);
		this.opponent = new CallingMachine(1);
		this.logPath = logPath;
	}
	
	@Override
	public void run() throws Exception {
		System.out.println("You are about to start a no-limit headsup manual test.");
		System.out.println("Agent being tested: " + agent.getName());
		System.out.println("Opponent: " + opponent.getName());
		int SBAmt = 50;
		int buyInAmt = 20000;
		int maxGameCnt = 1000;
		System.out.println("SB amount: " + SBAmt);
		System.out.println("Buy-in amount: " + buyInAmt);
		NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, opponent, SBAmt, buyInAmt, maxGameCnt);
		headsUpTable.start(logPath);
	}

	PlayerBase agent;
	PlayerBase opponent;
	String logPath;
}
