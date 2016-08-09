package holdem;

import java.util.ArrayList;
import java.util.Collections;

public class GameTable extends TableBase {

	public GameTable(int SBAmt, int ante) throws Exception {
		super(SBAmt, ante, GameTable.seatCnt);
		deck = new Deck();
	}

	public TableInfo getTableInfo() {
		return new TableInfo(this);
	}

	public boolean game() throws Exception {
		playerCnt = getPlayerCnt();
		if (playerCnt < 2)
			return false;
		deal();
		if (preflop() || flop() || turn() || river())
			winBeforeShowdown();
		else
			showdown();
		cleanUp();
		return true;
	}

	void deal() throws Exception {
		Button = getNext(Button);
		if (playerCnt == 2)
			BBIndex = getNext(Button);
		else
			BBIndex = getNext(getNext(Button));
		int next = (BBIndex + 1) % seatCnt;
		deck.shuffle();
		for (int i = 0; i < seatCnt; i++) {
			seats[next].deal(deck);
			next = (next + 1) % seatCnt;
		}
		activePlayerCnt = playerCnt;
	}

	boolean preflop() throws Exception {
		for (int i = 0; i < seatCnt; i++)
			processAction(seats[i].request(ante));
		int SBIndex = playerCnt == 2 ? Button : getNext(Button);
		processAction(seats[SBIndex].request(SBAmt));
		processAction(seats[BBIndex].request(BBAmt));
		currentBet = ante + BBAmt;
		minRaise = BBAmt;
		pots.add(new Pot());
		bet((BBIndex + 1) % seatCnt);
		return pots.get(0).getSeatCnt() < 2;
	}

	boolean flop() throws Exception {
		for (int i = 0; i < 3; i++)
			board.add(deck.draw());
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	boolean turn() throws Exception {
		board.add(deck.draw());
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	boolean river() throws Exception {
		board.add(deck.draw());
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	void bet(int next) throws Exception {
		int balanced = 0;
		for (; balanced < seatCnt && activePlayerCnt > 1; next = (next + 1) % seatCnt) {
			ActionBase action = seats[next].playerMove(new TableInfo(this));
			balanced = processAction(action) ? balanced + 1 : 1;
			broadcast(action);
		}
		updatePots();
	}

	boolean processAction(ActionBase action) {
		if (action == null || action.getClass() == Check.class || action.getClass() == Call.class)
			return true;
		Seat seat = action.player.getSeat();
		if (action.getClass() == Fold.class) {
			seat.active = false;
			activePlayerCnt--;
			Pot pot = pots.get(pots.size() - 1);
			if (pot.isFull) {
				pot = new Pot();
				pots.add(pot);
			}
			pot.inc(seat.bet);
			seat.bet = 0;
			return true;
		}
		if (action.getClass() == AllIn.class) {
			if (seat.bet - currentBet > minRaise)
				minRaise = seat.bet - currentBet;
			if (seat.bet > currentBet) {
				currentBet = seat.bet;
				return false;
			}
			return true;
		}
		int raiseAmt = ((Raise) action).raiseToAmt - currentBet;
		if (raiseAmt > minRaise)
			minRaise = raiseAmt;
		currentBet += raiseAmt;
		return false;
	}

	void broadcast(ActionBase action) {
		if (action != null) {
			for (int i = 0; i < seatCnt; i++)
				seats[i].receive(action, board);
		}
	}

	void broadcast(Result resultInfo) {
		for (int i = 0; i < seatCnt; i++)
			seats[i].receive(resultInfo);
	}

	void updatePots() {
		ArrayList<Seat> bets = new ArrayList<Seat>();
		for (int i = 0; i < seatCnt; i++)
			if (seats[i].bet > 0)
				bets.add(seats[i]);
		if (bets.size() == 0) {
			Pot pot = pots.get(pots.size() - 1);
			for (int i = 0; i < pot.seats.size();) {
				if (!pot.seats.get(i).active)
					pot.seats.remove(i);
				else
					i++;
			}
		} else {
			Collections.sort(bets);
			while (bets.size() > 0) {
				Pot pot = pots.get(pots.size() - 1);
				if (pot.isFull) {
					pot = new Pot();
					pots.add(pot);
				}
				int bet = bets.get(0).bet;
				pot.inc(bet * bets.size());
				pot.seats.clear();
				for (int i = 0; i < bets.size();) {
					if (bets.get(i).active)
						pot.seats.add(bets.get(i));
					bets.get(i).bet -= bet;
					if (bets.get(i).stack == 0) pot.isFull = true;
					if (bets.get(i).bet == 0)
						bets.remove(i);
					else
						i++;
				}
			}
		}
	}

	protected void winBeforeShowdown() {
		Seat winner = pots.get(0).getSeat(0);
		winner.stack += getPotSize();
		broadcast(new WinBeforeShowdown(winner.player, getPotSize(), board));
	}

	protected void showdown() throws Exception {
		ArrayList<PotResultInfo> potResults = new ArrayList<PotResultInfo>();
		for (; !pots.isEmpty(); pots.remove(0)) {
			int winnerCnt = 1;
			ArrayList<Hand> hands = new ArrayList<Hand>();
			for (int i = 0; i < pots.get(0).getSeatCnt(); i++)
				hands.add(Judge.getBestHand(board, pots.get(0).getSeat(i).holeCards));
			Collections.sort(hands);
			for (int i = 0; i < hands.size() - 1; i++) {
				if (hands.get(i).equals(hands.get(i + 1)))
					winnerCnt++;
				else
					break;
			}
			for (int i = 0; i < winnerCnt; i++)
				hands.get(i).getHoleCards().seat.stack += pots.get(0).getPotSize() / winnerCnt;
			potResults.add(new PotResultInfo(pots.get(0).getPotSize(), winnerCnt, hands));
		}
		broadcast(new Showdown(board, potResults));
	}
	
	String stackReport() {
		String report = "<BEGIN: STACK REPORT>\n";
		for (int i = 0; i < seats.length; i++) {
			if (seats[i].player != null) 
				report += (seats[i].player.getName() + ": " + seats[i].stack + "\n");
		}
		report += "<END: STACK REPORT>";
		return report;
	}

	void cleanUp() {
		for (int i = 0; i < seatCnt; i++)
			seats[i].clear();
		pots.clear();
		board.clear();
		activePlayerCnt = 0;
	}
	
	protected Deck deck;
	
	public static final int seatCnt = 10;
}
