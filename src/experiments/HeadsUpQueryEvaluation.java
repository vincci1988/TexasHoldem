package experiments;

import holdem.HeadsUpQueryTable;
import holdem.PlayerBase;

public class HeadsUpQueryEvaluation implements Exp {

	public HeadsUpQueryEvaluation(PlayerBase subject, String performanceLog, String gameLog, int gameNum) throws Exception {
		table = new HeadsUpQueryTable(subject, "Slumbot", 50, 20000, gameNum);
		this.performanceLog = performanceLog;
		this.gameLog = gameLog;
	}

	@Override
	public void run() throws Exception {
		table.start(performanceLog, gameLog);
	}

	HeadsUpQueryTable table;
	String performanceLog;
	String gameLog;
}
