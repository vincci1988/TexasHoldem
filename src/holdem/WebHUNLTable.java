package holdem;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import ASHE.Ashe;
import exp_web.WebHUNLAPI;

public class WebHUNLTable extends TableBase {

	public WebHUNLTable(PlayerBase agent, int maxGameCnt) throws Exception {
		super(SB, ante, headsUpPlayerCnt);
		this.agent = agent;
		web = new WebHUNLAPI(maxGameCnt);
		this.opponent = new WebTester(-1, "Slumbot", web);
		this.buyInAmt = buyIn;
		this.maxDeckCnt = maxGameCnt;
		gameCnt = 0;
		agentPerformance = new double[2];
		gameLogWriter = null;
		log = false;
	}

	public double[] start() throws Exception {
		agent.dec(agent.getBalance());
		agent.deposit(buyInAmt * maxDeckCnt * 2);
		opponent.dec(opponent.getBalance());
		opponent.deposit(buyInAmt * maxDeckCnt * 2);
		gameCnt = 0;
		agent.matchStart();
		opponent.matchStart();
		log = false;
		for (int i = 0; i < maxDeckCnt; i++) {
			game();
			getReport();
		}
		return agentPerformance;
	}

	public double[] start(PrintWriter performanceLogWriter, String gameLog) throws Exception {
		agent.dec(agent.getBalance());
		agent.deposit(buyInAmt * maxDeckCnt * 2);
		opponent.dec(opponent.getBalance());
		opponent.deposit(buyInAmt * maxDeckCnt * 2);
		gameCnt = 0;
		log = true;
		gameLogWriter = new PrintWriter(gameLog);
		agent.matchStart();
		opponent.matchStart();
		for (int i = 0; i < maxDeckCnt; i++) {
			gameLogWriter.println("<BEGIN: GAME " + (i + 1) + ">");
			game();
			gameLogWriter.println("<END: GAME " + (i + 1) + ">\n");
			// ============== FOR ASHE TESTS ONLY!!! ==============
			if (agent instanceof Ashe)
				((Ashe) agent).saveForest("forest.txt");
			// ============== FOR ASHE TESTS ONLY!!! ==============
			if ((i + 1) == maxDeckCnt)
				performanceLogWriter.print(getReport());
		}
		gameLogWriter.close();
		return agentPerformance;
	}

	private String getReport() {
		int agentNetWin = agent.getBalance() - 2 * maxDeckCnt * buyInAmt;
		agentPerformance[0] = (((double) agentNetWin) * 1000 / BBAmt / gameCnt);
		if (gameCnt == maxDeckCnt)
			System.out.println("Agent Performance: " + agentPerformance[0] + " mBB/hand");
		return agentPerformance[0] + "\t";
	}

	@Override
	public boolean game() throws Exception {
		gameCnt++;
		opponent.buyIn(this, buyInAmt);
		agent.buyIn(this, buyInAmt);
		agent.gameStart();
		opponent.gameStart();
		playerCnt = 2;
		deal();
		if (preflop() || flop() || turn() || river()) {
			winBeforeShowdown();
		} else {
			getOpponentCards();
			showdown();
		}

		cleanUp();
		// System.out.println("<END: GAME " + gameCnt + ">\n");
		if (gameCnt % 100 == 0)
			System.out.println(gameCnt + " games completed.");
		return true;
	}

	private void getOpponentCards() throws Exception {
		String opponentHoleCards = web.getOppHoleCards();
		opponent.seat.holeCards = new HoleCards(new Card(opponentHoleCards.substring(0, 2)),
				new Card(opponentHoleCards.substring(2, 4)));
		opponent.seat.holeCards.seat = opponent.seat;
		if (log) {
			gameLogWriter.println("<BEGIN: HOLE CARDS>");
			gameLogWriter.println(seats[Button].player.getName() + " (B): " + seats[Button].getHoleCards());
			gameLogWriter.println(seats[BBIndex].player.getName() + ": " + seats[BBIndex].getHoleCards());
			gameLogWriter.println("<END: HOLE CARDS>");
		}
	}

	private void deal() throws Exception {
		web.deal();
		Button = getNext(Button);
		BBIndex = getNext(Button);
		int next = (Button + 1) % seatCnt;
		for (int i = 0; i < seatCnt; i++) {
			if (seats[next].player.getID() == agent.id) {
				seats[next].deal(web.getAgentHoleCards());
			} else {
				seats[next].deal("UNKNOWN");
			}
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
		String flop = web.getBoard();
		for (int i = 0; i < 3; i++)
			board.add(new Card(flop.substring(2 * i, 2 * i + 2)));
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	private boolean turn() throws Exception {
		String turn = web.getBoard().substring(6, 8);
		board.add(new Card(turn));
		currentBet = 0;
		minRaise = BBAmt;
		bet(getNext(Button));
		return pots.get(0).getSeatCnt() < 2;
	}

	private boolean river() throws Exception {
		String river = web.getBoard().substring(8, 10);
		board.add(new Card(river));
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
			if (log && action != null)
				gameLogWriter.println(getLogEntry(tableInfo, action));
			balanced = processAction(action) ? balanced + 1 : 1;
			broadcast(action, tableInfo);
		}
		updatePots();
	}

	private String getLogEntry(TableInfo tableInfo, ActionBase action) {
		if (action instanceof Raise)
			return tableInfo.summarize() + action.player.getName() + ": " + action.getClass().getSimpleName() + " ($"
					+ ((Raise) action).raiseToAmt + ")";
		return tableInfo.summarize() + action.player.getName() + ": " + action.getClass().getSimpleName();
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
		if (log)
			gameLogWriter.print(resultInfo);
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
	PlayerBase opponent;
	int buyInAmt;
	int gameCnt;

	public static final int headsUpPlayerCnt = 2;
	public static final int ante = 0;
	public static final int SB = 50;
	public static final int buyIn = 20000;
	public final int maxDeckCnt;

	private double[] agentPerformance;
	private PrintWriter gameLogWriter;
	private boolean log;
	private WebHUNLAPI web;

}
