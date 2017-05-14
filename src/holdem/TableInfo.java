package holdem;

import java.util.ArrayList;

public class TableInfo {

	TableInfo(TableBase table) {
		playerInfos = new ArrayList<PlayerInfo>();
		int playersInFront = 0;
		int next = ((table.board.size() == 0 ? table.BBIndex : table.Button) + 1) % table.seatCnt;
		for (int i = 0; i < table.seatCnt; i++) {
			Seat opponent = table.seats[next];
			if (opponent.active && opponent.stack + opponent.bet > 0)
				playerInfos.add(new PlayerInfo(table.seats[next], table, playersInFront++));
			next = (next + 1) % table.seatCnt;
		}
		board = table.board.toString();
		boardForDisplay = table.board.display();
		BBAmt = table.BBAmt;
		currentBet = table.currentBet;
		minRaise = table.minRaise;
		potSize = table.getPotSize();
		playerCnt = table.getPlayerCnt();
	}

	public String toString() {
		String report = new String();
		report += "<BEGIN: TABLE INFO (BBAmt = " + BBAmt + ")> \n";
		if (board.length() == 0)
			report += "Preflop\n";
		else if (board.length() == 6)
			report += "Flop: " + boardForDisplay + "\n";
		else if (board.length() == 8)
			report += "Turn: " + boardForDisplay + "\n";
		else if (board.length() == 10)
			report += "River: " + boardForDisplay + "\n";
		report += "Current Bet = " + currentBet + "\n";
		report += "Min Raise = " + minRaise + "\n";
		report += "Pot Size = " + potSize + "\n";
		report += "Player count (active & inactive) = " + playerCnt + "\n";
		for (int i = 0; i < playerInfos.size(); i++) {
			report += playerInfos.get(i) + "\n";
		}
		report += "<END: TABLE INFO>";
		return report;
	}
	
	public String summarize() {
		String summary = new String();
		if (board.length() == 0)
			summary += "Preflop: ";
		else if (board.length() == 6)
			summary += "Flop (" + boardForDisplay + "): ";
		else if (board.length() == 8)
			summary += "Turn (" + boardForDisplay + "): ";
		else if (board.length() == 10)
			summary += "River (" + boardForDisplay + "): ";
		summary += "CB = " + currentBet + ", ";
		summary += "MR = " + minRaise + ", ";
		summary += "PS = " + potSize + " | ";
		return summary;
	}

	public ArrayList<PlayerInfo> playerInfos;
	public String board;
	public String boardForDisplay;
	public int BBAmt;
	public int currentBet;
	public int minRaise;
	public int potSize;
	public int playerCnt;
	public int buttonID;
}
