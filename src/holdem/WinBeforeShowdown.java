package holdem;

public class WinBeforeShowdown extends Result {

	WinBeforeShowdown(PlayerBase winner, int potSize, Board board) {
		this.boardForDisplay = board.display();
		this.board = board.toString();
		this.winnerName = winner.getName();
		this.winnerID = winner.id;
		this.potSize = potSize;
	}
	
	@Override
	public String toString() {
		String report = "<BEGIN: WIN BEFORE SHOWDOWN>\nBoard: " + boardForDisplay + "\n";
		report += "Winner: " + winnerName + ", Pot Size = " + potSize + "\n";
		report += "<END: WIN BEFORE SHOWDOWN>\n";
		return report;
	}
	
	String boardForDisplay;
	String board;
	String winnerName;
	int winnerID;
	int potSize;
}
