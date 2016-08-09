package holdem;

public class PlayerInfo {
	
	PlayerInfo(Seat seat, TableBase table, int playersInFront) {
		name = seat.player.getName();
		id = seat.player.id;
		bet = seat.bet;
		stack = seat.stack;
		position = playersInFront;
	}
	
	public String toString() {
		return "[" + position + "] PLAYER INFO: " + name + ", bet = " + bet + ", stack = " + stack;
	}
	
	public String name;
	public int id;
	public int bet;
	public int stack;
	public int position;
}
