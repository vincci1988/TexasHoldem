package holdem;

//import java.text.DecimalFormat;
//import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

public class NLHeadsUpAdpTable extends TableBase {

	public NLHeadsUpAdpTable(PlayerBase agent, PlayerBase[] opponents, int SBAmt, int buyInAmt, int maxGameCnt,
			int roundCnt) throws Exception {
		super(SBAmt, ante, headsUpPlayerCnt);
		this.agent = agent;
		this.opponents = opponents;
		this.buyInAmt = buyInAmt;
		this.maxDeckCnt = maxGameCnt;
		this.roundCnt = roundCnt;
		gameCnt = 0;
		gameNetWins = new double[maxDeckCnt * opponents.length];
		agentPerformance = new double[2];
	}

	public double[] start() throws Exception {
		for (int r = 0; r < roundCnt; r++) {
			for (int o = 0; o < opponents.length; o++) {
				agent.matchStart();
				agent.dec(agent.getBalance());
				agent.deposit(buyInAmt * maxDeckCnt);
				opponents[o].matchStart();
				opponents[o].dec(opponents[o].getBalance());
				opponents[o].deposit(buyInAmt * maxDeckCnt);
				decks = new Deck[maxDeckCnt];
				for (int i = 0; i < maxDeckCnt; i++) {
					decks[i] = new Deck();
					decks[i].shuffle();
				}
				for (int i = 0; i < maxDeckCnt; i++) {
					int previousBalance = agent.getBalance();
					agent.buyIn(this, buyInAmt);
					opponents[o].buyIn(this, buyInAmt);
					agent.gameStart();
					opponents[o].gameStart();
					deck = decks[i];
					deck.reset();
					game();
					gameNetWins[maxDeckCnt * o + i] += getGameNetWin(previousBalance);
				}
			}			
			/*
			System.out.print((r + 1) + ": ");
			NumberFormat formatter = new DecimalFormat("#0.00"); 
			for (int i = 0; i < gameNetWins.length; i++) {
				if ((i) % maxDeckCnt == 0) System.out.print(opponents[i / maxDeckCnt].getName() + ": ");
				System.out.print(formatter.format(gameNetWins[i] / (r + 1)) + " ");
				if ((i + 1) % maxDeckCnt == 0) System.out.print("| ");
			}
			System.out.println();
			*/
		}
		return agentPerformance;
	}

	private double getGameNetWin(int previousBalance) {
		return (((double) (agent.getBalance() - previousBalance)) * 1000 / BBAmt);
	}

	@Override
	public boolean game() throws Exception {
		gameCnt++;
		playerCnt = 2;
		deal();
		if (preflop() || flop() || turn() || river())
			winBeforeShowdown();
		else
			showdown();
		cleanUp();
		return true;
	}

	private void deal() throws Exception {
		Button = getNext(Button);
		BBIndex = getNext(Button);
		int next = (Button + 1) % seatCnt;
		for (int i = 0; i < seatCnt; i++) {
			seats[next].deal(deck);
			next = (next + 1) % seatCnt;
		}
		activePlayerCnt = playerCnt;
	}

	private boolean preflop() throws Exception {
		for (int i = 0; i < seatCnt; i++)
			processAction(seats[i].request(ante));
		int SBIndex = Button;
		processAction(seats[SBIndex].request(SBAmt));
		processAction(seats[BBIndex].request(BBAmt));
		currentBet = ante + BBAmt;
		minRaise = BBAmt;
		pots.add(new Pot());
		bet((BBIndex + 1) % seatCnt);
		return pots.get(0).getSeatCnt() < 2;
	}

	private boolean flop() throws Exception {
		for (int i = 0; i < 3; i++)
			board.add(deck.draw());
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	private boolean turn() throws Exception {
		board.add(deck.draw());
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	private boolean river() throws Exception {
		board.add(deck.draw());
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	private void bet(int next) throws Exception {
		int balanced = 0;
		for (; balanced < seatCnt && activePlayerCnt > 1; next = (next + 1) % seatCnt) {
			TableInfo tableInfo = new TableInfo(this);
			ActionBase action = seats[next].playerMove(tableInfo);
			balanced = processAction(action) ? balanced + 1 : 1;
			broadcast(action, tableInfo);
		}
		updatePots();
	}

	private boolean processAction(ActionBase action) {
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

	private void broadcast(ActionBase action, TableInfo info) {
		if (action != null) {
			for (int i = 0; i < seatCnt; i++)
				seats[i].receive(action, info);
		}
	}

	private void broadcast(Result resultInfo) throws Exception {
		for (int i = 0; i < seatCnt; i++)
			seats[i].receive(resultInfo);
	}

	private void updatePots() {
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
					if (bets.get(i).stack == 0)
						pot.isFull = true;
					if (bets.get(i).bet == 0)
						bets.remove(i);
					else
						i++;
				}
			}
		}
	}

	protected void winBeforeShowdown() throws Exception {
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

	void cleanUp() {
		for (int i = 0; i < headsUpPlayerCnt; i++) {
			seats[i].holeCards = null;
			seats[i].bet = 0;
			seats[i].active = false;
			seats[i].unmount();
		}
		pots.clear();
		board.clear();
		activePlayerCnt = 0;
		playerCnt = 0;
	}

	PlayerBase agent;
	PlayerBase[] opponents;
	int buyInAmt;
	int gameCnt;
	int roundCnt;
	Deck[] decks;
	Deck deck;

	public static final int headsUpPlayerCnt = 2;
	public static final int ante = 0;
	public final int maxDeckCnt;

	private double[] agentPerformance;
	private double[] gameNetWins;
}
