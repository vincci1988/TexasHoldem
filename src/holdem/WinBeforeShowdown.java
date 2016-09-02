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
	
	public String boardForDisplay;
	public String board;
	public String winnerName;
	public int winnerID;
	public int potSize;
}
