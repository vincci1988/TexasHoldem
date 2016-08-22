package holdem;

import java.io.IOException;

public abstract class PlayerBase {

	public PlayerBase(int id) {
		this.id = id;
		balance = 0;
		seat = null;
	}

	public int getBalance() {
		return balance;
	}

	public void deposit(int amt) {
		balance += amt;
	}

	void dec(int amt) {
		balance -= amt;
		if (balance < 0)
			balance = 0;
	}
	
	public int getID() {
		return id;
	}

	public boolean buyIn(TableBase table, int amt) {
		if (amt > balance || amt <= 0)
			return false;
		seat = table.join(this, amt);
		return seat != null;
	}

	public boolean leave() {
		return (seat == null) ? true : seat.unmount();
	}
	
	public double getPotOdds(TableInfo info) {
		double amtToCall = info.currentBet - getMyBet();
		if (amtToCall > getMyStack()) amtToCall = getMyStack();
		return amtToCall / (amtToCall + info.potSize);
	}

	protected String peek() {
		return seat == null ? null : seat.getHoleCards();
	}
	
	protected int getMyBet() {
		return seat == null ? 0 : seat.bet;
	}
	
	protected int getMyStack() {
		return seat == null ? 0 : seat.stack;
	}

	ActionBase give(int amt) {
		if (amt < seat.stack) {
			seat.stack -= amt;
			seat.bet += amt;
			return new Call(this);
		}
		seat.bet += seat.stack;
		seat.stack = 0;
		return new AllIn(this);
	}

	Seat getSeat() {
		return seat;
	}
	
	public void gameStart() {
		//Defualt implementation: Do nothing;
	}
	
	public void matchStart() {
		//Defualt implementation: Do nothing;
	}

	ActionBase move(TableInfo info) throws Exception {
		if (info.playerInfos.size() < 2) return null;
		ActionBase action = getAction(info);
		if (action.getClass() == Check.class && seat.bet == info.currentBet)
			return action;
		if (action.getClass() == Call.class && seat.stack > info.currentBet - seat.bet && info.currentBet > seat.bet) {
			seat.stack -= (info.currentBet - seat.bet);
			seat.bet = info.currentBet;
			return action;
		}
		if ((action.getClass() == Raise.class) && seat.stack + seat.bet > ((Raise) action).raiseToAmt
				&& ((Raise) action).raiseToAmt >= info.currentBet + info.minRaise) {
			seat.stack -= (((Raise) action).raiseToAmt - seat.bet);
			seat.bet = ((Raise) action).raiseToAmt;
			return action;
		}
		if (action.getClass() == AllIn.class) {
			seat.bet += seat.stack;
			seat.stack = 0;
			return action;
		}
		return action.getClass() == Fold.class ? action : new Fold(action.player);
	}

	public abstract ActionBase getAction(TableInfo info) throws IOException, Exception;
	
	void observe(ActionBase action, Board board) {
		if (action.getClass() == Fold.class)
			observe(new FoldInfo(action, board));
		if (action.getClass() == Check.class)
			observe(new CheckInfo(action, board));
		if (action.getClass() == Call.class)
			observe(new CallInfo(action, board));
		if (action.getClass() == Raise.class)
			observe(new RaiseInfo(action, board));
		if (action.getClass() == AllIn.class)
			observe(new AllInInfo(action, board));
	}

	public abstract void observe(ActionInfoBase actionInfo);
	public abstract void observe(Result resultInfo) throws Exception ;
	public abstract String getName();
	
	protected final int id;
	private int balance;
	Seat seat;
}
