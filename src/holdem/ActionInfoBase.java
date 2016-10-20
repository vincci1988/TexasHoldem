package holdem;

abstract public class ActionInfoBase {
	ActionInfoBase(ActionBase action, Board board) {
		this.boardForDisplay = board.display();
		this.board = board.toString();
		this.playerName = action.player.getName();
		this.playerID = action.player.id;
		this.amt = action.player.seat.bet;
		this.AIA = amt - action.bet;
	}
	
	abstract public String toString();
	
	protected String getStage() {
		if (board.length() == 0) return "Preflop";
		if (board.length() == 6) return "Flop";
		if (board.length() == 8) return "Turn";
		if (board.length() == 10) return "River";
		return null;
	}
	
	public  String boardForDisplay;
	public String board;
	public String playerName;
	public int playerID;
	public int amt;
	public int AIA; //Amount-In-Addition
}
