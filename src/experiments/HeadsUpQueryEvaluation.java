package experiments;

import evolvable_players.*;
import holdem.HeadsUpQueryTable;

public class HeadsUpQueryEvaluation implements Exp {

	public HeadsUpQueryEvaluation(String performanceLog, String gameLog) {
		this.performanceLog = performanceLog;
		this.gameLog = gameLog;
	}

	@Override
	public void run() throws Exception {
		HeadsUpQueryTable table = new HeadsUpQueryTable(new CandidStatistician(1), "Slumbot", 50, 20000, 1);
		table.start(performanceLog, gameLog);
	}

	String performanceLog;
	String gameLog;
}
