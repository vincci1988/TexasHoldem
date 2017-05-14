package exp_web;

import holdem.WebHUNLTable;

import java.io.PrintWriter;

import holdem.PlayerBase;

public class WebHUNLEval {
	public WebHUNLEval(PlayerBase subject, int gameNum, int round) throws Exception {
		table = new WebHUNLTable(subject, gameNum);
		this.subject = subject.toString();
		this.round = round;
		this.gameNum = gameNum;
	}

	public void run() throws Exception {
		PrintWriter performanceLogWriter = new PrintWriter(performanceLog + "_" + subject + ".txt");
		performanceLogWriter
				.println("Web Test (" + subject + ")\n" + gameNum + " game(s), " + round + " round(s). (mBB/hand)");
		for (int i = 0; i < round; i++) {
			System.out.println("<TEST BEGIN: ROUND " + (i + 1) + ">");
			table.start(performanceLogWriter, gameLog + "_" + subject + "_" + (i + 1) + ".txt");
			System.out.println("<TEST END ROUND " + (i + 1) + ">");
		}
		performanceLogWriter.close();
	}

	WebHUNLTable table;
	final String performanceLog = "WebHUNLPerformance";
	final String gameLog = "WebHUNLGameLog";
	String subject;
	int round;
	int gameNum;
}
