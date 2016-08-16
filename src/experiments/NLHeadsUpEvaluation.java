package experiments;

import java.io.IOException;

import evolvable_players.*;
import holdem.NLHeadsUpTable;
import holdem.PlayerBase;
import simple_players.*;

@SuppressWarnings("unused")
public class NLHeadsUpEvaluation implements Exp {

	public NLHeadsUpEvaluation(String performanceLog, String gameLog) throws IOException, Exception {
		//Update to evaluate different agent / against different opponents
		//this.agent = new CandidStatistician(0, new CandidStatisticianGenome(2.195, 0.7147, 0.5669));
		this.agent = new LSTMHeadsUpPlayer(0, new LSTMHeadsUpPlayerGenome("LSTMHeadsUpPlayerGenome.txt"));
		CandidStatisticianGenome genome = new CandidStatisticianGenome("CandidStatisticianChampionGenome.txt");
		this.opponent = new CandidStatistician(1, genome);
		//this.opponent = new HotheadManiac(1);
		//this.opponent = new ScaredLimper(1);
		this.performanceLog = performanceLog;
		this.gameLog = gameLog;
	}
	
	@Override
	public void run() throws Exception {
		System.out.println("You are about to start a no-limit headsup evaluation.");
		System.out.println("Agent being evaluated: " + agent.getName());
		System.out.println("Opponent: " + opponent.getName());
		int SBAmt = 50;
		int buyInAmt = 20000;
		int maxDeckCnt = 1500;
		System.out.println("SB amount: " + SBAmt);
		System.out.println("Buy-in amount: " + buyInAmt);
		NLHeadsUpTable headsUpTable = new NLHeadsUpTable(agent, opponent, SBAmt, buyInAmt, maxDeckCnt);
		System.out.println("Agent Perforance: " + headsUpTable.start(performanceLog, gameLog) + " mBB/hand");
	}

	PlayerBase agent;
	PlayerBase opponent;
	String performanceLog;
	String gameLog;
}
