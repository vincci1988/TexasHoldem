package exp_local;

import java.io.IOException;

import evolvable_players.CandidStatistician;
import holdem.NLHeadsUpAdpTable;
import holdem.PlayerBase;
import simple_players.CallingMachine;
import simple_players.HotheadManiac;
import simple_players.ScaredLimper;

public class NLHeadsUpAdpEvaluation implements Exp {
	public NLHeadsUpAdpEvaluation(PlayerBase agent, int deckNum, int roundCnt) throws IOException, Exception {
		// Update to evaluate different agent / against different opponents
		this.opponents = new PlayerBase[4];
		this.agent = agent;
		this.opponents[0] = new CandidStatistician(-1);
		this.opponents[1] = new HotheadManiac(-2);
		this.opponents[2] = new CallingMachine(-3);
		this.opponents[3] = new ScaredLimper(-4);
		this.deckNum = deckNum;
		this.roundCnt = roundCnt;
	}

	public NLHeadsUpAdpEvaluation(PlayerBase agent, PlayerBase[] opponents, int deckNum, int roundCnt)
			throws IOException, Exception {
		// Update to evaluate different agent / against different opponents
		this.opponents = opponents;
		this.agent = agent;
		this.deckNum = deckNum;
		this.roundCnt = roundCnt;
	}

	@Override
	public void run() throws Exception {
		System.out.println("No-Limit Heads-up Adaptation Evaluation:");
		System.out.println("Agent: " + agent.getName());
		System.out.print("Opponent: ");
		for (int i = 0; i < opponents.length; i++) 
			System.out.print(opponents[i].getName() + " ");
		System.out.println();
		int SBAmt = 50;
		int buyInAmt = 20000;
		System.out.println("SB amount: " + SBAmt);
		System.out.println("Buy-in amount: " + buyInAmt);
		NLHeadsUpAdpTable headsUpAdpTable = new NLHeadsUpAdpTable(agent, opponents, SBAmt, buyInAmt, deckNum, roundCnt);
		System.out.println("Agent Perforance: " + headsUpAdpTable.start()[0] + " mBB/hand\n");

	}

	PlayerBase agent;
	PlayerBase[] opponents;
	int deckNum;
	int roundCnt;
}
