package holdem;

import java.util.ArrayList;

public class Showdown extends Result {
	
	Showdown(Board board, ArrayList<PotResultInfo> potResults) {
		this.boardForDisplay = board.display();
		this.board = board.toString();
		this.potResults = potResults;
	}

	@Override
	public String toString() {
		String report = "<BEGIN: SHOWDOWN>\nBoard: " + boardForDisplay + "\n";
		for (int i = 0; i < potResults.size(); i++) {
			if (i > 0) report += "Side Pot[" + i + "]: ";
			report += potResults.get(i);
		}
		report += "<END: SHOWDOWN>\n";
		return report;
	}
	
	String boardForDisplay;
	String board;
	ArrayList<PotResultInfo> potResults;
}
