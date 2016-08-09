package holdem;

import java.util.ArrayList;

public abstract class TableBase {
	
	public TableBase(int SBAmt, int ante, int seatCnt) throws Exception {
		this.seatCnt = seatCnt;
		seats = new Seat[seatCnt];
		for (int i = 0; i < seatCnt; i++)
			seats[i] = new Seat();
		pots = new ArrayList<Pot>();
		board = new Board();
		currentBet = 0;
		minRaise = 0;
		Button = -1;
		BBIndex = -1;
		activePlayerCnt = 0;
		playerCnt = 0;
		this.ante = ante;
		this.SBAmt = SBAmt;
		BBAmt = 2 * SBAmt;
	}
	
	public int getPotSize() {
		int total = 0;
		for (int i = 0; i < pots.size(); i++)
			total += pots.get(i).getPotSize();
		for (int i = 0; i < seatCnt; i++)
			total += seats[i].bet;
		return total;
	}
	
	public int getActivePlayerCnt() {
		return activePlayerCnt;
	}
	
	public int getPlayerCnt() {
		int playerCnt = 0;
		for (int i = 0; i < seatCnt; i++)
			if (!seats[i].isEmpty())
				playerCnt++;
		return playerCnt;
	}
	
	int getNext(int index) {
		while (seats[(index = (index + 1) % seatCnt)].isEmpty())
			;
		return index;
	}
	
	Seat join(PlayerBase player, int buyInAmt) {
		Seat available = null;
		for (int i = 0; i < seatCnt; i++) {
			if (seats[i].isEmpty()) {
				available = seats[i];
				break;
			}
		}
		if (available == null)
			return null;
		available.mount(player, buyInAmt);
		return available;
	}
	
	public abstract boolean game() throws Exception;
	
	protected Seat[] seats;
	protected ArrayList<Pot> pots;
	protected Board board;
	protected int currentBet;
	protected int minRaise;
	protected int Button;
	protected int BBIndex;
	protected int activePlayerCnt;
	protected int playerCnt;
	protected int ante;
	protected int SBAmt;
	protected int BBAmt;

	public final int seatCnt;
}
